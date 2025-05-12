package cc.cassian.raspberry.mixin;


import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ThrowableProjectile.class)
public class ThrowableProjectileMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void mixin(CallbackInfo ci) {
        if (ModConfig.get().thrownItemParticles) {
            var projectile = (ThrowableProjectile) (Object) this;
            if (projectile instanceof Snowball snowball) {
                var random = new Random();
                if (random.nextBoolean()) {
                    snowball.level.addParticle(ParticleTypes.SNOWFLAKE, snowball.getX(), snowball.getY(), snowball.getZ(), 0.0, 0.0, 0.0);
                }
            }
        }
    }
}
