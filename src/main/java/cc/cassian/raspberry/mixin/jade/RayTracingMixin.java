package cc.cassian.raspberry.mixin.jade;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import snownee.jade.overlay.RayTracing;

@Mixin(RayTracing.class)
public class RayTracingMixin {

    @WrapOperation(method = "fire", remap = false, at = @At(value = "INVOKE", target = "Lsnownee/jade/overlay/RayTracing;rayTrace(Lnet/minecraft/world/entity/Entity;D)Lnet/minecraft/world/phys/HitResult;"), require = 0)
    private HitResult increaseJadeReach(RayTracing instance, Entity entity, double playerReach, Operation<HitResult> original) {
        if (ModConfig.get().jadeRequiresScoping && entity instanceof Player player && player.isScoping())
            playerReach = 100D;
        return original.call(instance, entity, playerReach);
    }
}