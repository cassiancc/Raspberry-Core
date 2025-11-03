package cc.cassian.raspberry.mixin.caverns_and_chasms;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.caverns_and_chasms.common.entity.projectile.BluntArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Projectile.class)
public class BowItemMixin {
    @WrapMethod(
            method = "shootFromRotation")
    public void raspberry$setVelocity(Entity shooter, float x, float y, float z, float velocity, float inaccuracy, Operation<Void> original) {
        var arrow = (Projectile) (Object) this;
        if (arrow instanceof BluntArrow) velocity = (float) (velocity* ModConfig.get().rose_gold_arrow_velocity);
        original.call(shooter, x, y, z, velocity, inaccuracy);
    }
}
