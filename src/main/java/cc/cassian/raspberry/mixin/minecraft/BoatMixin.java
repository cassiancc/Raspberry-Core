package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.common.api.leash.Leashable;
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
    @Nullable
    private CompoundTag leashInfoTag;

    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void raspberry$defineLeashData(CallbackInfo ci) {
        this.entityData.define(DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void raspberry$tickLeash(CallbackInfo ci) {
        if (!this.level.isClientSide) {
            Leashable.tickLeash(this);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void raspberry$saveLeash(CompoundTag compound, CallbackInfo ci) {
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
    public void closeRangeLeashBehavior(Entity holder) {
        Vec3 direction = holder.position().subtract(this.position()).normalize();
        double speed = 0.05;
        this.setDeltaMovement(this.getDeltaMovement().add(direction.scale(speed)));
    }

    @Override
    public void setDelayedLeashHolderId(int id) {
        this.delayedLeashHolderId = id;
        if (this.level != null) {
            Entity entity = this.level.getEntity(id);
            if (entity != null) {
                this.setLeashedTo(entity, false);
            }
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