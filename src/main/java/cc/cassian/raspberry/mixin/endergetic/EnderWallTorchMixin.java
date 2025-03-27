package cc.cassian.raspberry.mixin.endergetic;

import com.teamabnormals.endergetic.common.block.EnderWallTorchBlock;
import com.teamabnormals.endergetic.core.registry.EEParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallTorchBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WallTorchBlock.class)
public abstract class EnderWallTorchMixin {
    @Redirect(method = "animateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void mixin(Level level, ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        if ((Object) this instanceof EnderWallTorchBlock)
            level.addParticle(EEParticleTypes.ENDER_FLAME.get(), x, y, z, xSpeed, ySpeed, zSpeed);
        else level.addParticle(particleData, x, y,z, xSpeed, ySpeed, zSpeed);
    }
}
