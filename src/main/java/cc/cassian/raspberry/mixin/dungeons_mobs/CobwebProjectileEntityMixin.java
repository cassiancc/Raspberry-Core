package cc.cassian.raspberry.mixin.dungeons_mobs;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.infamous.dungeons_mobs.entities.projectiles.CobwebProjectileEntity;
import com.infamous.dungeons_mobs.mod.ModSoundEvents;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CobwebProjectileEntity.class)
public class CobwebProjectileEntityMixin {

    @WrapMethod(method = "spawnTrap", remap = false)
    private void spawnTemporaryCobweb(double x, double y, double z, Operation<Void> original) {
        if (ModConfig.get().dungeons_mobs_revised_cobwebs) {
            CobwebProjectileEntity projectile = (CobwebProjectileEntity) (Object) this;
            if (!projectile.getLevel().isClientSide()) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState currentState = projectile.level.getBlockState(pos);
                if (currentState.isAir() || currentState.getMaterial().isReplaceable()) {
                    projectile.getLevel().setBlock(pos, RaspberryBlocks.TEMPORARY_COBWEB.get().defaultBlockState(), 3);
                }
                projectile.playSound(ModSoundEvents.SPIDER_WEB_IMPACT.get(), 1.0F, 1.0F);
            }
        } else {
            original.call(x, y, z);
        }
    }
}