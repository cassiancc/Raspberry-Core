package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.FlameParticleWithSprites;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SpriteSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.particle.FlameParticle$SmallFlameProvider")
public abstract class SmallFlameParticleProviderMixin {
    @WrapOperation(
            method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At(value = "INVOKE", target = "net/minecraft/client/particle/FlameParticle.pickSprite (Lnet/minecraft/client/particle/SpriteSet;)V")
    )
    public void setSprite(FlameParticle instance, SpriteSet spriteSet, Operation<Void> original) {
        original.call(instance, spriteSet);
        ((FlameParticleWithSprites)instance).raspberryCore$setSprites(spriteSet);
        ((FlameParticleWithSprites)instance).raspberryCore$setQuadSize(0.1F);
    }
}