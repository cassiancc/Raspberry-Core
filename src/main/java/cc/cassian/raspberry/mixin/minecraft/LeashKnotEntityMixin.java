/* MIT License

Copyright (c) 2025 Martin Kadlec

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.*;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.UUID;

@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashKnotEntityMixin extends HangingEntity implements Leashable, KnotConnectionAccess {

    protected LeashKnotEntityMixin(EntityType<? extends HangingEntity> type, Level level) {
        super(type, level);
    }

    @Unique
    private static final EntityDataAccessor<OptionalInt> raspberry$DATA_ID_LEASH_HOLDER_ID = SynchedEntityData.defineId(LeashKnotEntityMixin.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    @Unique
    private int raspberry$delayedLeashHolderId;

    @Unique
    @Nullable
    private Entity raspberry$leashHolder;

    @Unique
    @Nullable
    private CompoundTag raspberry$pendingLeashTag;

    @Unique
    private final KnotConnectionManager raspberry$connectionManager = new KnotConnectionManager();

    @Override
    public KnotConnectionManager raspberry$getConnectionManager() {
        return raspberry$connectionManager;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        if (ModConfig.get().backportLeash) {
            this.entityData.define(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!ModConfig.get().backportLeash) return;

        if (!this.level().isClientSide()) {
            if (this.raspberry$leashHolder != null && !this.raspberry$leashHolder.isAlive()) {
                this.raspberry$dropLeash(true, false);
            }

            Leashable.tickLeash(this);
            raspberry$connectionManager.checkDistance((LeashFenceKnotEntity)(Object)this);
        }

        if (this.raspberry$delayedLeashHolderId != 0 && this.raspberry$getLeashHolder() == null) {
            Entity entity = this.level().getEntity(this.raspberry$delayedLeashHolderId);
            if (entity != null) {
                this.raspberry$setLeashedTo(entity, false);
                this.raspberry$delayedLeashHolderId = 0;
            }
        }
    }

    @Override
    public void raspberry$onLeashRemoved() {
        if (!ModConfig.get().backportLeash) return;

        LeashFenceKnotEntity self = (LeashFenceKnotEntity)(Object)this;

        boolean hasVanilla = !Leashable.leashableLeashedTo(self).isEmpty();
        boolean hasCustom = raspberry$connectionManager.hasConnections();
        boolean isLeashed = this.raspberry$isLeashed();

        if (!hasVanilla && !hasCustom && !isLeashed) {
            self.discard();
        }
    }

    @Override
    public void raspberry$dropLeash(boolean broadcast, boolean dropItem) {
        if (!ModConfig.get().backportLeash) return;

        boolean wasLeashed = this.raspberry$leashHolder != null;

        this.raspberry$leashHolder = null;
        this.raspberry$pendingLeashTag = null;
        this.raspberry$delayedLeashHolderId = 0;
        this.entityData.set(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

        if (!this.level().isClientSide && wasLeashed) {
            if (dropItem) {
                this.spawnAtLocation(Items.LEAD);
            }

            if (broadcast && this.level() instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
            }

            this.raspberry$onLeashRemoved();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void raspberry$onSave(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (this.raspberry$leashHolder != null) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("UUID", this.raspberry$leashHolder.getUUID());
            compound.put("Leash", tag);
        } else if (this.raspberry$pendingLeashTag != null) {
            compound.put("Leash", this.raspberry$pendingLeashTag.copy());
        }
        raspberry$connectionManager.writeToNbt(compound);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void raspberry$onLoad(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (compound.contains("Leash", 10)) {
            this.raspberry$pendingLeashTag = compound.getCompound("Leash");
        }
        raspberry$connectionManager.readFromNbt(compound);
    }

    @Override
    public boolean raspberry$isLeashed() {
        if (!ModConfig.get().backportLeash) return false;
        return this.entityData.get(raspberry$DATA_ID_LEASH_HOLDER_ID).isPresent();
    }

    @Nullable
    @Override
    public Entity raspberry$getLeashHolder() {
        if (!ModConfig.get().backportLeash) return null;

        if (this.level().isClientSide) {
            OptionalInt holderId = this.entityData.get(raspberry$DATA_ID_LEASH_HOLDER_ID);
            if (holderId.isEmpty()) {
                this.raspberry$leashHolder = null;
            } else if (this.raspberry$leashHolder == null || this.raspberry$leashHolder.getId() != holderId.getAsInt()) {
                this.raspberry$leashHolder = this.level().getEntity(holderId.getAsInt());
            }
            return this.raspberry$leashHolder;
        }

        if (this.raspberry$leashHolder == null && this.raspberry$pendingLeashTag != null && this.level() instanceof ServerLevel serverLevel) {
            if (this.raspberry$pendingLeashTag.hasUUID("UUID")) {
                UUID uuid = this.raspberry$pendingLeashTag.getUUID("UUID");
                Entity entity = serverLevel.getEntity(uuid);
                if (entity != null) {
                    this.raspberry$setLeashedTo(entity, true);
                    this.raspberry$pendingLeashTag = null;
                }
            } else {
                this.raspberry$pendingLeashTag = null;
            }
        }

        return this.raspberry$leashHolder;
    }

    @Override
    public void raspberry$setLeashedTo(Entity entity, boolean sendPacket) {
        if (!ModConfig.get().backportLeash) return;

        this.raspberry$leashHolder = entity;
        this.raspberry$pendingLeashTag = null;
        this.raspberry$delayedLeashHolderId = 0;
        this.entityData.set(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.of(entity.getId()));

        if (sendPacket && !this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, entity));
        }
    }

    @Override
    public void raspberry$setDelayedLeashHolderId(int id) {
        if (!ModConfig.get().backportLeash) return;

        this.raspberry$delayedLeashHolderId = id;
        this.raspberry$leashHolder = null;
        this.entityData.set(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

        if (id != 0) {
            Entity entity = this.level().getEntity(id);
            if (entity != null) {
                this.raspberry$setLeashedTo(entity, false);
                this.raspberry$delayedLeashHolderId = 0;
            }
        }
    }

    @Override
    public Vec3 raspberry$getLeashOffset(float partialTick) {
        return new Vec3(0.0, 0.2, 0.0);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void raspberry$onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ModConfig.get().backportLeash) return;
        if (this.level().isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        LeashFenceKnotEntity knot = (LeashFenceKnotEntity)(Object)this;
        InteractionResult result = KnotInteractionHelper.handleKnotInteraction(player, hand, knot);

        this.raspberry$onLeashRemoved();

        cir.setReturnValue(result);
    }

    @Inject(method = "dropItem", at = @At("HEAD"))
    private void raspberry$onDropItem(Entity breaker, CallbackInfo ci) {
        if (ModConfig.get().backportLeash && !this.level().isClientSide) {
            LeashFenceKnotEntity knot = (LeashFenceKnotEntity)(Object)this;
            raspberry$connectionManager.clearAllConnections(this.level(), knot);

            if (this.raspberry$isLeashed()) {
                this.raspberry$dropLeash(true, true);
            }
        }
    }
}