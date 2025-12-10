package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net.minecraft.world.entity.monster.Ghast$GhastShootFireballGoal")
public class GhastShootFireballGoalMixin {

    @Shadow
    @Final
    private Ghast ghast;

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    private boolean raspberry$swapGhastProjectile(Level level, Entity entity) {
        if (ModConfig.get().ghastDragonFireball && entity instanceof LargeFireball largeFireball) {
            DragonFireball dragonFireball = new DragonFireball(
                level, 
                this.ghast, 
                largeFireball.xPower, 
                largeFireball.yPower, 
                largeFireball.zPower
            );
            
            dragonFireball.setPos(largeFireball.getX(), largeFireball.getY(), largeFireball.getZ());
            
            return level.addFreshEntity(dragonFireball);
        }
        
        return level.addFreshEntity(entity);
    }
}