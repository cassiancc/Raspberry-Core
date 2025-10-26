package cc.cassian.raspberry.mixin.caverns_and_chasms;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class BluntArrowMixin {

    @Inject(
            method = "onHitEntity", at = @At(value = "HEAD"))
    public void raspberry$setKnockback(EntityHitResult result, CallbackInfo ci) {
        var arrow = (AbstractArrow) (Object) this;
        arrow.setKnockback(ModConfig.get().rose_gold_arrow_knockback);
    }
}
