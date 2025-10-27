package cc.cassian.raspberry.mixin.dungeons_mobs;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.infamous.dungeons_mobs.entities.projectiles.CobwebProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CobwebProjectileEntity.class)
public class CobwebProjectileEntityMixin {

    @Inject(method = "spawnTrap", at = @At("HEAD"), cancellable = true, remap = false)
    private void spawnTemporaryCobweb(double x, double y, double z, CallbackInfo ci) {
        CobwebProjectileEntity projectile = (CobwebProjectileEntity) (Object) this;

        if (!projectile.level.isClientSide) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState currentState = projectile.level.getBlockState(pos);
            if (currentState.isAir() || currentState.getMaterial().isReplaceable()) {
                projectile.level.setBlock(pos, RaspberryBlocks.TEMPORARY_COBWEB.get().defaultBlockState(), 3);
            }
            projectile.playSound(com.infamous.dungeons_mobs.mod.ModSoundEvents.SPIDER_WEB_IMPACT.get(), 1.0F, 1.0F);
        }
        ci.cancel();
    }
}