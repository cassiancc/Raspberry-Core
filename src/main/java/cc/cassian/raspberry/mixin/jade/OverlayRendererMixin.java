package cc.cassian.raspberry.mixin.jade;


import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.jade.overlay.OverlayRenderer;

@Mixin(OverlayRenderer.class)
public class OverlayRendererMixin {
    @Inject(method = "shouldShow", remap = false, at = @At(value = "HEAD"), cancellable = true)
    private static void jadeRequiresScoping(CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.get().jadeRequiresScoping && Minecraft.getInstance().player != null) {
            if (!Minecraft.getInstance().player.isScoping()) {
                cir.setReturnValue(false);
            }
        }
    }
}