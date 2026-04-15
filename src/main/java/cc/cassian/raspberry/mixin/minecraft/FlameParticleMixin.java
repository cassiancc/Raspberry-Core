package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.FlameParticleWithSprites;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SpriteSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlameParticle.class)
public class FlameParticleMixin implements FlameParticleWithSprites {
    @Unique
    private float raspberryCore$quadSize;

    @Unique
    private SpriteSet raspberryCore$sprites;

    @Unique
    public void raspberryCore$setSprites(SpriteSet sprites) {
        this.raspberryCore$sprites = sprites;
    }

    @Unique
    public SpriteSet raspberryCore$getSprites() {
        return raspberryCore$sprites;
    }

    @Unique
    public void raspberryCore$setQuadSize(float size) {
        this.raspberryCore$quadSize = size;
    }

    @Unique
    public float raspberryCore$getQuadSize() {
        return this.raspberryCore$quadSize;
    }

    @Inject(method = "getQuadSize", at = @At(value = "HEAD"), cancellable = true)
    public void fixedQuadSize(float scaleFactor, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(this.raspberryCore$getQuadSize());
    }
}