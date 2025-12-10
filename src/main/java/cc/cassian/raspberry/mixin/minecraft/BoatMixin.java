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

import cc.cassian.raspberry.common.api.leash.InterpolationHandler;
import cc.cassian.raspberry.common.api.leash.Leashable;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
    private static final EntityDataAccessor<OptionalInt> DATA_ID_LEASH_HOLDER_ID = SynchedEntityData.defineId(Boat.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    @Unique
    private int delayedLeashHolderId;

    @Unique
    @Nullable
    private Entity leashHolder;

    @Unique 
    private final InterpolationHandler interpolation = new InterpolationHandler(this, 3);

    @Unique
    @Nullable
    private CompoundTag leashInfoTag;

    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void raspberry$defineLeashData(CallbackInfo ci) {
        this.entityData.define(DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());
    }

    @Unique
    private void raspberry$restoreLeashFromSave() {
        if (this.leashInfoTag != null && this.level instanceof ServerLevel serverLevel) {
            if (this.leashInfoTag.hasUUID("UUID")) {
                UUID uuid = this.leashInfoTag.getUUID("UUID");
                Entity entity = serverLevel.getEntity(uuid);
                if (entity != null) {
                    this.setLeashedTo(entity, true);
                    return;
                }
            } else if (this.leashInfoTag.contains("X", 99) && this.leashInfoTag.contains("Y", 99) && this.leashInfoTag.contains("Z", 99)) {
                BlockPos pos = net.minecraft.nbt.NbtUtils.readBlockPos(this.leashInfoTag);
                this.setLeashedTo(net.minecraft.world.entity.decoration.LeashFenceKnotEntity.getOrCreateKnot(this.level, pos), true);
                return;
            }

            if (this.tickCount > 100) {
                this.spawnAtLocation(Items.LEAD);
                this.leashInfoTag = null;
            }
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void raspberry$tickLeash(CallbackInfo ci) {
        if (!this.level.isClientSide && ModConfig.get().backportLeash) {
            if (this.leashInfoTag != null) {
                this.raspberry$restoreLeashFromSave();
            }
            
            if (this.leashHolder != null) {
                if (!this.isAlive() || !this.leashHolder.isAlive()) {
                    this.dropLeash(true, true);
                }
            }

            Leashable.tickLeash(this);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void raspberry$saveLeash(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (this.leashHolder != null) {
            CompoundTag tag = new CompoundTag();
            if (this.leashHolder instanceof LivingEntity) {
                tag.putUUID("UUID", this.leashHolder.getUUID());
            } else if (this.leashHolder instanceof HangingEntity) {
                BlockPos pos = ((HangingEntity) this.leashHolder).getPos();
                tag.putInt("X", pos.getX());
                tag.putInt("Y", pos.getY());
                tag.putInt("Z", pos.getZ());
            }
            compound.put("Leash", tag);
        } else if (this.leashInfoTag != null) {
            compound.put("Leash", this.leashInfoTag.copy());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void raspberry$readLeash(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (compound.contains("Leash", 10)) {
            this.leashInfoTag = compound.getCompound("Leash");
        }
    }

    @Override
    public boolean isLeashed() {
        return this.entityData.get(DATA_ID_LEASH_HOLDER_ID).isPresent();
    }

    @Nullable
    @Override
    public Entity getLeashHolder() {
        if (this.leashHolder == null && this.entityData.get(DATA_ID_LEASH_HOLDER_ID).isPresent()) {
            if (this.level.isClientSide) {
                this.leashHolder = this.level.getEntity(this.entityData.get(DATA_ID_LEASH_HOLDER_ID).getAsInt());
            }
        }
        return this.leashHolder;
    }

    @Override
    public void setLeashedTo(Entity entity, boolean sendPacket) {
        this.leashHolder = entity;
        this.leashInfoTag = null;
        this.entityData.set(DATA_ID_LEASH_HOLDER_ID, OptionalInt.of(entity.getId()));

        if (sendPacket && this.level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, entity));
        }
    }

    @Override
    public void dropLeash(boolean broadcast, boolean dropItem) {
        if (this.leashHolder != null) {
            this.leashHolder = null;
            this.leashInfoTag = null;
            this.entityData.set(DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

            if (!this.level.isClientSide && dropItem) {
                this.spawnAtLocation(Items.LEAD);
            }

            if (!this.level.isClientSide && broadcast && this.level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
            }
        }
    }

    @Override
    public void setDelayedLeashHolderId(int id) {
        this.delayedLeashHolderId = id;
        this.dropLeash(false, false);
        if (this.level != null && id != 0) {
            Entity entity = this.level.getEntity(id);
            if (entity != null) {
                this.setLeashedTo(entity, false);
            }
        }
    }

    @Inject(method = "lerpTo", at = @At("HEAD"), cancellable = true)
    private void raspberry$lerpTo(double x, double y, double z, float yRot, float xRot, int lerpSteps, boolean teleport, CallbackInfo ci) {
        if (ModConfig.get().backportLeash) {
            ci.cancel();
            this.interpolation.interpolateTo(new Vec3(x, y, z), yRot, xRot);
        }
    }

    @Inject(method = "tickLerp", at = @At("HEAD"), cancellable = true)
    private void raspberry$tickLerp(CallbackInfo ci) {
        if (ModConfig.get().backportLeash) {
            ci.cancel();
            if (this.isControlledByLocalInstance()) {
                this.interpolation.cancel();
                this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
            } 
            
            this.interpolation.interpolate();
        }
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.88F * this.getBbHeight(), 0.64F * this.getBbWidth());
    }
    
    @Override
    public void removeAfterChangingDimensions() {
        super.removeAfterChangingDimensions();
        if (ModConfig.get().backportLeash) {
            this.dropLeash(true, false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void raspberry$resolveLeash(CallbackInfo ci) {
        if (this.level.isClientSide && this.delayedLeashHolderId != 0 && this.getLeashHolder() == null) {
            Entity entity = this.level.getEntity(this.delayedLeashHolderId);
            if (entity != null) {
                this.setLeashedTo(entity, false);
                this.delayedLeashHolderId = 0;
            }
        }
    }
}