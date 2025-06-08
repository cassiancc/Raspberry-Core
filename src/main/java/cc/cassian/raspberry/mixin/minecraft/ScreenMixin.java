package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.toms_storage.tooltips.TooltipCacheLoader;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "hasShiftDown()Z", at = @At("HEAD"), cancellable = true)
    private static void injectFakeShift(CallbackInfoReturnable<Boolean> cir) {
        if (TooltipCacheLoader.isFakeShifting()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
