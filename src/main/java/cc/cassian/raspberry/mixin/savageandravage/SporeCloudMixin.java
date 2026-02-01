package cc.cassian.raspberry.mixin.savageandravage;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.savage_and_ravage.common.entity.projectile.SporeCloud;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SporeCloud.class)
public class SporeCloudMixin {

    @WrapOperation(method = "spawnAreaEffectCloud", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/AreaEffectCloud;setDuration(I)V"))
    private void raspberry_modifySporeDuration(AreaEffectCloud aoe, int duration, Operation<Void> original) {
        if (ModConfig.get().creeperSporesDurationModifier != 1.0 && ((SporeCloud) (Object) this).getOwner() instanceof Player) {
            duration = (int) (duration * ModConfig.get().creeperSporesDurationModifier);
        }

        original.call(aoe, duration);
    }
}