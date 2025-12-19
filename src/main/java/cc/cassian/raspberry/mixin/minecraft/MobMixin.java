package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.KnotConnectionManager;
import cc.cassian.raspberry.compat.vanillabackport.leash.Leashable;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "canBeLeashed", at = @At("HEAD"), cancellable = true)
    private void raspberry$canBeLeashed(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.get().backportLeash) {
            cir.setReturnValue(!(this instanceof Enemy));
        }   
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void raspberry$fixLeashSaving(CompoundTag compound, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        Mob mob = (Mob) (Object) this;
        Entity holder = mob.getLeashHolder();

        if (holder != null && !(holder instanceof net.minecraft.world.entity.LivingEntity) && !(holder instanceof net.minecraft.world.entity.decoration.HangingEntity)) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("UUID", holder.getUUID());
            compound.put("Leash", tag);
        }
    }

    @Inject(method = "dropLeash", at = @At("HEAD"))
    private void raspberry$checkKnotOnUnleash(boolean broadcast, boolean dropItem, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        Mob mob = (Mob) (Object) this;
        Entity holder = mob.getLeashHolder();

        if (holder instanceof LeashFenceKnotEntity knot) {
            List<Leashable> leashedEntities = Leashable.leashableLeashedTo(knot);
            
            boolean hasOtherVanilla = leashedEntities.stream().anyMatch(entity -> entity != mob);
            boolean hasCustom = KnotConnectionManager.getManager(knot).hasConnections();
            
            if (!hasOtherVanilla && !hasCustom) {
                knot.discard();
            }
        }
    }
}