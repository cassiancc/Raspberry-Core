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

import cc.cassian.raspberry.compat.vanillabackport.leash.Leashable;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PathfinderMob.class)
public abstract class LeashBehaviorMixin extends Mob implements Leashable {
    
    @Unique
    private double raspberry$angularMomentum;

    protected LeashBehaviorMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public double raspberry$angularMomentum() {
        return this.raspberry$angularMomentum;
    }

    @Override
    public void setRaspberry$angularMomentum(double raspberry$angularMomentum) {
        this.raspberry$angularMomentum = raspberry$angularMomentum;
    }

    @Inject(method = "tickLeash", at = @At("HEAD"), cancellable = true)
    private void raspberry$onTickLeash(CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        ci.cancel();

        MobAccessor accessor = (MobAccessor) this;
        CompoundTag leashInfoTag = accessor.raspberry$getLeashInfoTag();
        int delayedId = accessor.raspberry$getDelayedLeashHolderId();

        if (leashInfoTag != null) {
            this.raspberry$restoreLeashFromSave(accessor, leashInfoTag);
        }

        if (delayedId != 0) {
            this.raspberry$restoreLeashFromId(accessor, delayedId);
        }

        if (this.isLeashed()) {
            Entity holder = this.getLeashHolder();
            if (holder != null && !holder.isAlive()) {
                this.dropLeash(true, true);
            }
            Leashable.tickLeash(this);
        }
    }

    @Unique
    private void raspberry$restoreLeashFromSave(MobAccessor accessor, CompoundTag tag) {
        if (this.level() instanceof ServerLevel serverLevel) {
            if (tag.hasUUID("UUID")) {
                UUID uuid = tag.getUUID("UUID");
                Entity entity = serverLevel.getEntity(uuid);
                
                if (entity != null) {
                    this.setLeashedTo(entity, true);
                    accessor.raspberry$setLeashInfoTag(null);
                }
                
            } else if (tag.contains("X", 99)) {
                net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
                this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(this.level(), pos), true);
                accessor.raspberry$setLeashInfoTag(null);
            }
        }
    }

    @Unique
    private void raspberry$restoreLeashFromId(MobAccessor accessor, int id) {
        Entity entity = this.level().getEntity(id);
        if (entity != null) {
            this.setLeashedTo(entity, true);
            accessor.raspberry$setDelayedLeashHolderId(0);
        }
    }
}