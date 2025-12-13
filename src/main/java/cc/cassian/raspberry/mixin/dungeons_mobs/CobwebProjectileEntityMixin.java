package cc.cassian.raspberry.mixin.dungeons_mobs;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.infamous.dungeons_mobs.entities.projectiles.CobwebProjectileEntity;
import com.infamous.dungeons_mobs.interfaces.ITrapsTarget;
import com.infamous.dungeons_mobs.mod.ModSoundEvents;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.lang.reflect.Field;

@Mixin(CobwebProjectileEntity.class)
public class CobwebProjectileEntityMixin {

    @WrapMethod(method = "spawnTrap", remap = false)
    private void spawnTemporaryCobweb(double x, double y, double z, Operation<Void> original) {
        if (ModConfig.get().dungeons_mobs_revised_cobwebs) {
            CobwebProjectileEntity projectile = (CobwebProjectileEntity) (Object) this;

            if (!projectile.getLevel().isClientSide()) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState currentState = projectile.getLevel().getBlockState(pos);

                if (currentState.isAir() || currentState.getMaterial().isReplaceable()) {
                    projectile.getLevel().setBlock(pos, RaspberryBlocks.TEMPORARY_COBWEB.get().defaultBlockState(), 3);
                }

                projectile.playSound(ModSoundEvents.SPIDER_WEB_IMPACT.get(), 1.0F, 1.0F);

                Entity owner = projectile.getOwner();
                
                if (owner instanceof Mob mobOwner && owner instanceof ITrapsTarget trapsTarget) {
                    LivingEntity target = mobOwner.getTarget();
                    
                    if (target != null) {
                        double distSqr = target.distanceToSqr(x, y, z);
                        if (distSqr < 4.0) {
                            trapsTarget.setTargetTrapped(true, true);
                            setTrappedCounter(owner, 100);
                        }
                    }
                }
            }
        } else {
            original.call(x, y, z);
        }
    }

    private void setTrappedCounter(Entity entity, int value) {
        try {
            Field field = entity.getClass().getField("targetTrappedCounter");
            field.setInt(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            RaspberryMod.LOGGER.error("Failed to set targetTrappedCounter", e);
        }
    }
}