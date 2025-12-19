/* The MIT License (MIT)

Copyright (c) 2025

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.InterpolationHandler;
import cc.cassian.raspberry.compat.vanillabackport.leash.Leashable;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;
import java.util.UUID;

@Mixin(Boat.class)
public abstract class BoatMixin extends Entity implements Leashable {

    @Unique
    private static final EntityDataAccessor<OptionalInt> DATA_ID_LEASH_HOLDER_ID = SynchedEntityData.defineId(BoatMixin.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    @Unique
    private int raspberry$delayedLeashHolderId;

    @Unique
    @Nullable
    private Entity raspberry$leashHolder;

    @Unique
    private final InterpolationHandler raspberry$interpolation = new InterpolationHandler(this, 3);

    @Unique
    @Nullable
    private CompoundTag raspberry$leashInfoTag;

    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void raspberry$defineLeashData(CallbackInfo ci) {
        this.entityData.define(DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());
    }

    @Unique
    private void raspberry$restoreLeashFromSave() {
        if (this.raspberry$leashInfoTag != null && this.level instanceof ServerLevel serverLevel) {
            if (this.raspberry$leashInfoTag.hasUUID("UUID")) {
                UUID uuid = this.raspberry$leashInfoTag.getUUID("UUID");
                Entity entity = serverLevel.getEntity(uuid);
                if (entity != null) {
                    this.raspberry$setLeashedTo(entity, true);
                    return;
                }
            } else if (this.raspberry$leashInfoTag.contains("X", 99) && this.raspberry$leashInfoTag.contains("Y", 99) && this.raspberry$leashInfoTag.contains("Z", 99)) {
                BlockPos pos = net.minecraft.nbt.NbtUtils.readBlockPos(this.raspberry$leashInfoTag);
                this.raspberry$setLeashedTo(net.minecraft.world.entity.decoration.LeashFenceKnotEntity.getOrCreateKnot(this.level, pos), true);
                return;
            }

            if (this.tickCount > 100) {
                this.spawnAtLocation(Items.LEAD);
                this.raspberry$leashInfoTag = null;
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void raspberry$tickLeash(CallbackInfo ci) {
        if (!this.level.isClientSide && ModConfig.get().backportLeash) {
            if (this.raspberry$leashInfoTag != null) {
                this.raspberry$restoreLeashFromSave();
            }

            if (this.raspberry$leashHolder != null) {
                if (!this.isAlive() || !this.raspberry$leashHolder.isAlive()) {
                    this.raspberry$dropLeash(true, true);
                }
            }

            Leashable.tickLeash(this);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void raspberry$saveLeash(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (this.raspberry$leashHolder != null) {
            CompoundTag tag = new CompoundTag();
            if (this.raspberry$leashHolder instanceof HangingEntity hangingEntity) {
                BlockPos pos = hangingEntity.getPos();
                tag.putInt("X", pos.getX());
                tag.putInt("Y", pos.getY());
                tag.putInt("Z", pos.getZ());
            } else {
                tag.putUUID("UUID", this.raspberry$leashHolder.getUUID());
            }
            compound.put("Leash", tag);
        } else if (this.raspberry$leashInfoTag != null) {
            compound.put("Leash", this.raspberry$leashInfoTag.copy());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void raspberry$readLeash(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (compound.contains("Leash", 10)) {
            this.raspberry$leashInfoTag = compound.getCompound("Leash");
        }
    }

    @Override
    public boolean raspberry$isLeashed() {
        return this.entityData.get(DATA_ID_LEASH_HOLDER_ID).isPresent();
    }

    @Nullable
    @Override
    public Entity raspberry$getLeashHolder() {
        if (this.raspberry$leashHolder == null && this.entityData.get(DATA_ID_LEASH_HOLDER_ID).isPresent()) {
            if (this.level.isClientSide) {
                this.raspberry$leashHolder = this.level.getEntity(this.entityData.get(DATA_ID_LEASH_HOLDER_ID).getAsInt());
            }
        }
        return this.raspberry$leashHolder;
    }

    @Override
    public void raspberry$setLeashedTo(Entity entity, boolean sendPacket) {
        this.raspberry$leashHolder = entity;
        this.raspberry$leashInfoTag = null;
        this.raspberry$delayedLeashHolderId = 0;
        this.entityData.set(DATA_ID_LEASH_HOLDER_ID, OptionalInt.of(entity.getId()));

        if (sendPacket && !this.level.isClientSide && this.level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, entity));
        }
    }

    @Override
    public void raspberry$dropLeash(boolean broadcast, boolean dropItem) {
        boolean wasLeashed = this.raspberry$leashHolder != null;

        this.raspberry$leashHolder = null;
        this.raspberry$leashInfoTag = null;
        this.raspberry$delayedLeashHolderId = 0;
        this.entityData.set(DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

        if (!this.level.isClientSide && wasLeashed) {
            if (dropItem) {
                this.spawnAtLocation(Items.LEAD);
            }

            if (broadcast && this.level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
            }
        }
    }

    @Override
    public void raspberry$setDelayedLeashHolderId(int id) {
        this.raspberry$delayedLeashHolderId = id;

        this.raspberry$leashHolder = null;
        this.entityData.set(DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

        if (id != 0) {
            Entity entity = this.level.getEntity(id);
            if (entity != null) {
                this.raspberry$setLeashedTo(entity, false);
                this.raspberry$delayedLeashHolderId = 0;
            }
        }
    }

    @Inject(method = "lerpTo", at = @At("HEAD"), cancellable = true)
    private void raspberry$lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport, CallbackInfo ci) {
        if (ModConfig.get().backportLeash) {
            ci.cancel();
            this.raspberry$interpolation.interpolateTo(new Vec3(x, y, z), yRot, xRot);
        }
    }

    @Inject(method = "tickLerp", at = @At("HEAD"), cancellable = true)
    private void raspberry$tickLerp(CallbackInfo ci) {
        if (ModConfig.get().backportLeash) {
            ci.cancel();
            if (this.isControlledByLocalInstance()) {
                this.raspberry$interpolation.cancel();
                this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
            }

            this.raspberry$interpolation.interpolate();
        }
    }

    @Override
    public Vec3 raspberry$getLeashOffset(float partialTick) {
        return new Vec3(0.0, 0.88F * this.getBbHeight(), 0.64F * this.getBbWidth());
    }

    public @NotNull Vec3 getRopeHoldPosition(float partialTicks) {
        return this.getPosition(partialTicks).add(0.0D, this.getEyeHeight() * 0.88D, 0.0D);
    }

    @Override
    public void removeAfterChangingDimensions() {
        super.removeAfterChangingDimensions();
        if (ModConfig.get().backportLeash) {
            this.raspberry$dropLeash(true, false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void raspberry$resolveLeash(CallbackInfo ci) {
        if (this.level.isClientSide && this.raspberry$delayedLeashHolderId != 0 && this.raspberry$getLeashHolder() == null) {
            Entity entity = this.level.getEntity(this.raspberry$delayedLeashHolderId);
            if (entity != null) {
                this.raspberry$setLeashedTo(entity, false);
                this.raspberry$delayedLeashHolderId = 0;
            }
        }
    }
}