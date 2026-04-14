package cc.cassian.raspberry.mixin.additional_enchantments;

import cc.cassian.raspberry.registry.RaspberryParticleTypes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.additional_enchantments.capability.ProjectileData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ProjectileData.class)
public class ProjectileDataMixin {
    @Unique
    private static final ThreadLocal<Projectile> CURRENT_PROJECTILE = new ThreadLocal<>();

    @Inject(method = "searchForHomingTarget", at = @At("HEAD"), cancellable = true, remap = false)
    private void captureInstance(Projectile instance, CallbackInfo ci) {
        CURRENT_PROJECTILE.set(instance);

        // Tridents only home in water
        if (instance instanceof ThrownTrident && !instance.isInWaterRainOrBubble()) {
            ci.cancel();
        }

        // Wait a few ticks before searching for target
        if (instance.tickCount < 3) {
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "lambda$searchForHomingTarget$0", at = @At(value = "RETURN"))
    private boolean searchForHomingTarget(boolean original, @Local(argsOnly = true) LivingEntity entity) {
        if (!original) return false;
        // Discard targets not in front of projectile
        Projectile instance = CURRENT_PROJECTILE.get();;
        if (instance != null) {
            // Always include glowing targets
            if (entity.isCurrentlyGlowing()) {
                return true;
            }
            Vec3 projectileMovement = instance.getDeltaMovement().normalize();
            Vec3 vectorToTarget = instance.position().vectorTo(entity.position()).normalize();
            // 60deg cone in front of projectile
            return projectileMovement.dot(vectorToTarget) > 0.5F;
        }
        return true;
    }

    @WrapOperation(method = "lambda$searchForHomingTarget$2", at = @At(value = "INVOKE", target = "de/cadentem/additional_enchantments/capability/ProjectileData.setHomingTarget (Lnet/minecraft/world/entity/projectile/Projectile;Lnet/minecraft/world/entity/LivingEntity;)V"), remap = false)
    private void prioritiseGlowingTargets(ProjectileData projectileData, Projectile instance, LivingEntity target, Operation<Void> original, @Local(name = "entities") List<LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            if (entity.isCurrentlyGlowing()) {
                target = entity;
                break;
            }
        }
        original.call(projectileData, instance, target);
    }

    @WrapOperation(method = "handleHomingMovement", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/projectile/Projectile.setDeltaMovement (Lnet/minecraft/world/phys/Vec3;)V"))
    private void lerpMovement(Projectile instance, Vec3 newMovement, Operation<Void> original) {
        Level level = instance.level;
        Vec3 projectilePosition = instance.position();
        Vec3 movement = instance.getDeltaMovement();
        Vec3 particleMovement = movement.normalize().scale(0.1);

        // Tridents only home in water
        if (instance instanceof ThrownTrident && !instance.isInWaterRainOrBubble()) {
            return;
        }

        if (level.random.nextFloat() < 0.1) {
            level.addParticle(ParticleTypes.SCULK_SOUL, projectilePosition.x, projectilePosition.y, projectilePosition.z, 0, 0, 0);
        } else {
            level.addParticle(RaspberryParticleTypes.HOMING.get(), projectilePosition.x, projectilePosition.y, projectilePosition.z, particleMovement.x, particleMovement.y, particleMovement.z);
        }

        Vec3 pull = newMovement.normalize().scale(0.5);
        Vec3 pulledMovement = movement.scale(0.6).add(pull);
        original.call(instance, pulledMovement);
    }
}
