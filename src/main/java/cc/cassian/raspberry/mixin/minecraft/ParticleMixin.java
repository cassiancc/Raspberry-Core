package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.FlameParticleWithSprites;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Particle.class)
public class ParticleMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        if ((Object) this instanceof FlameParticle) {
            SpriteSet sprites = ((FlameParticleWithSprites) this).raspberryCore$getSprites();
            if (sprites != null) {
                ((TextureSheetParticle)(Object) this).setSpriteFromAge(sprites);
            }
        }
    }
}
