package cc.cassian.raspberry.mixin.screenshot_viewer;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.lgatodu47.screenshot_viewer.ScreenshotViewer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraftforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(ScreenshotViewer.class)
public class ScreenshotViewerMixin {
    @Inject(method = "onScreenPostInit", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;", ordinal = 0), remap = false, cancellable = true)
    private void mixin(ScreenEvent.Init.Post event, CallbackInfo ci, @Local List<GuiEventListener> buttons) {
        if (buttons.isEmpty())
            ci.cancel();
    }
}
