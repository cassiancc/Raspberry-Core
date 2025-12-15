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
    private static final EntityDataAccessor<OptionalInt> raspberry$DATA_ID_LEASH_HOLDER_ID = SynchedEntityData.defineId(LeashFenceKnotEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);

    @Unique 
    private int raspberry$delayedLeashHolderId;
    
    @Unique 
    @Nullable 
    private Entity raspberry$leashHolder;
   
    @Unique 
    @Nullable 
    private CompoundTag raspberry$leashInfoTag;
    
    @Unique 
    private int raspberry$tickCount = 0;

    @Unique
    private final KnotConnectionManager raspberry$connectionManager = new KnotConnectionManager();

    @Override
    public KnotConnectionManager raspberry$getConnectionManager() {
        return raspberry$connectionManager;
    }

    @Override
    public String getEncodeId() {
        return EntityType.getKey(this.getType()).toString();
    }

    @Override
    public void tick() {
        super.tick();

        if (!ModConfig.get().backportLeash) {
            if (!this.level.isClientSide) {
                if (Leashable.leashableLeashedTo((LeashFenceKnotEntity)(Object)this).isEmpty()) {
                    this.discard();
                }
            }
            return;
        }

        this.raspberry$tickCount++;

        if (!this.level.isClientSide) {
            if (this.raspberry$leashInfoTag != null) {
                this.raspberry$restoreLeashFromSave();
            }
            
            if (this.raspberry$leashHolder != null && !this.raspberry$leashHolder.isAlive()) {
                this.raspberry$clearLeashHolder();
            }

            Leashable.tickLeash(this);
            
            raspberry$connectionManager.checkDistance((LeashFenceKnotEntity)(Object)this);
            
            boolean hasVanilla = !Leashable.leashableLeashedTo((LeashFenceKnotEntity)(Object)this).isEmpty();
            boolean isLeashedByPlayer = this.raspberry$leashHolder != null; 
            boolean hasCustom = raspberry$connectionManager.hasConnections();

            if (!hasVanilla && !isLeashedByPlayer && !hasCustom) {
                this.discard();
            }
        }

        if (this.level.isClientSide && this.raspberry$delayedLeashHolderId != 0 && this.getLeashHolder() == null) {
            Entity entity = this.level.getEntity(this.raspberry$delayedLeashHolderId);
            if (entity != null) {
                this.setLeashedTo(entity, false);
                this.raspberry$delayedLeashHolderId = 0;
            }
        }
    }

    @Inject(method = "survives", at = @At("HEAD"), cancellable = true)
    private void raspberry$onSurvives(CallbackInfoReturnable<Boolean> cir) {
        if (!this.level.getBlockState(this.getPos()).is(net.minecraft.tags.BlockTags.FENCES)) {
             LeashFenceKnotEntity self = (LeashFenceKnotEntity)(Object)this;
             if (raspberry$connectionManager.hasConnections()) {
                 raspberry$connectionManager.clearAllConnections(this.level, self);
             }
        }
    }
    
    @Inject(method = "dropItem", at = @At("HEAD"))
    private void raspberry$onBreak(Entity breaker, CallbackInfo ci) {
         LeashFenceKnotEntity self = (LeashFenceKnotEntity)(Object)this;
         KnotInteractionHelper.discardCustomConnections(self, breaker);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void raspberry$onSave(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (this.raspberry$leashHolder != null) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("UUID", this.raspberry$leashHolder.getUUID());
            compound.put("Leash", tag);
        } else if (this.raspberry$leashInfoTag != null) {
            compound.put("Leash", this.raspberry$leashInfoTag.copy());
        }

        raspberry$connectionManager.writeToNbt(compound);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void raspberry$onLoad(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (compound.contains("Leash", 10)) {
            this.raspberry$leashInfoTag = compound.getCompound("Leash");
        }

        raspberry$connectionManager.readFromNbt(compound);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void raspberry$onInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ModConfig.get().backportLeash || this.level.isClientSide) return;

        LeashFenceKnotEntity knot = (LeashFenceKnotEntity)(Object)this;

        if (player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.ShearsItem) {
            return;
        }

        InteractionResult result = KnotInteractionHelper.handleKnotInteraction(player, knot);
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(result);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        if (ModConfig.get().backportLeash) {
            this.entityData.define(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());
        }
    }

    @Unique
    private void raspberry$restoreLeashFromSave() {
        if (this.raspberry$leashInfoTag != null && this.level instanceof ServerLevel serverLevel) {
            if (this.raspberry$leashInfoTag.hasUUID("UUID")) {
                UUID uuid = this.raspberry$leashInfoTag.getUUID("UUID");
                Entity entity = serverLevel.getEntity(uuid);
                if (entity != null) {
                    this.setLeashedTo(entity, true);
                    return;
                }
            }
            // Give up if we can't find holder after 100 ticks
            if (this.raspberry$tickCount > 100) {
                this.raspberry$leashInfoTag = null;
            }
        }
    }

    @Unique
    private void raspberry$clearLeashHolder() {
        this.raspberry$leashHolder = null;
        this.raspberry$leashInfoTag = null;
        this.entityData.set(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
        }
        
        boolean hasVanilla = !Leashable.leashableLeashedTo((LeashFenceKnotEntity)(Object)this).isEmpty();
        boolean hasCustom = raspberry$connectionManager.hasConnections();

        if (!hasVanilla && !hasCustom) {
            this.discard();
        }
    }

    @Override
    public boolean isLeashed() {
        if (!ModConfig.get().backportLeash) return false;
        return this.entityData.get(raspberry$DATA_ID_LEASH_HOLDER_ID).isPresent();
    }

    @Nullable
    @Override
    public Entity getLeashHolder() {
        if (!ModConfig.get().backportLeash) return null;

        if (this.raspberry$leashHolder == null && this.entityData.get(raspberry$DATA_ID_LEASH_HOLDER_ID).isPresent()) {
            if (this.level.isClientSide) {
                this.raspberry$leashHolder = this.level.getEntity(this.entityData.get(raspberry$DATA_ID_LEASH_HOLDER_ID).getAsInt());
            }
        }
        return this.raspberry$leashHolder;
    }

    @Override
    public void setLeashedTo(Entity entity, boolean sendPacket) {
        if (!ModConfig.get().backportLeash) return;

        this.raspberry$leashHolder = entity;
        this.raspberry$leashInfoTag = null;
        this.entityData.set(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.of(entity.getId()));

        if (sendPacket && this.level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, entity));
        }
    }

    @Override
    public void dropLeash(boolean broadcast, boolean dropItem) {
        if (!ModConfig.get().backportLeash) return;

        if (this.raspberry$leashHolder != null) {
            this.raspberry$leashHolder = null;
            this.raspberry$leashInfoTag = null;
            this.entityData.set(raspberry$DATA_ID_LEASH_HOLDER_ID, OptionalInt.empty());

            if (!this.level.isClientSide) {
                if (dropItem) {
                    this.spawnAtLocation(net.minecraft.world.item.Items.LEAD);
                }
                if (broadcast && this.level instanceof ServerLevel serverLevel) {
                    serverLevel.getChunkSource().broadcast(this, new ClientboundSetEntityLinkPacket(this, null));
                }
            }
        }
    }

    @Override
    public void setDelayedLeashHolderId(int id) {
        if (!ModConfig.get().backportLeash) return;
        this.raspberry$delayedLeashHolderId = id;
        this.dropLeash(false, false);
        if (this.level != null && id != 0) {
            Entity entity = this.level.getEntity(id);
            if (entity != null) {
                this.setLeashedTo(entity, false);
            }
        }
    }

    @Override
    public Vec3 getLeashOffset(float partialTick) {
        return new Vec3(0.0, 0.2, 0.0);
    }
}