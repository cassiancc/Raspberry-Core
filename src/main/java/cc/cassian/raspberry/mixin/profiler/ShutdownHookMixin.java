package cc.cassian.raspberry.mixin.profiler;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.util.RaspberryProfiler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class ShutdownHookMixin {

    @Inject(method = "stop", at = @At("HEAD"))
    private void raspberry$dumpOnExit(CallbackInfo ci) {
        if (ModConfig.get().enableProfiler) {
            RaspberryProfiler.dumpLogs();
        }
    }
}