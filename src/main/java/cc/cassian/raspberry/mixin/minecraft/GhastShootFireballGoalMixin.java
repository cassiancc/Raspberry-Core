package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public class GhastShootFireballGoalMixin {

    @Shadow
    @Final
    private Ghast ghast;

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    private boolean raspberry$swapGhastProjectile(Level level, Entity entity, Operation<Boolean> original) {
        if (ModConfig.get().ghastDragonFireball && entity instanceof LargeFireball largeFireball) {
            
            DragonFireball dragonFireball = new DragonFireball(
                level, 
                this.ghast, 
                largeFireball.xPower, 
                largeFireball.yPower, 
                largeFireball.zPower
            );
            
            dragonFireball.setPos(largeFireball.getX(), largeFireball.getY(), largeFireball.getZ());
            
            return original.call(level, dragonFireball);
        }
        
        return original.call(level, entity);
    }
}