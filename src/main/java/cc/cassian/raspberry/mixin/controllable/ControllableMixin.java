package cc.cassian.raspberry.mixin.controllable;

import cc.cassian.raspberry.ModCompat;
import com.mrcrayfish.controllable.Controllable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Controllable.class)
public class ControllableMixin {
    @Inject(
            method = "isJeiLoaded",
            at = @At(value = "RETURN"), remap = false,
            cancellable = true)
    private static void backportJEMIFix(CallbackInfoReturnable<Boolean> cir) {
        if (ModCompat.EMI) {
            cir.setReturnValue(false);
        }
    }
}
