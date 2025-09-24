package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class HasteHandMixin {
    @Inject(
            method = "getCurrentSwingDuration",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void mixin(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() <= 1)
            cir.setReturnValue(2);
    }
}
