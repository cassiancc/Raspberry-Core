package cc.cassian.raspberry.mixin.profiler;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.util.RaspberryProfiler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.emi.emi.runtime.EmiReloadManager$ReloadWorker", remap = false)
public class EmiRegistryMixin {

    @WrapOperation(method = "run", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/EmiPlugin;register(Ldev/emi/emi/api/EmiRegistry;)V"))
    private void raspberry$profilePlugin(EmiPlugin plugin, EmiRegistry registry, Operation<Void> original) {
        if (!ModConfig.get().enableProfiler) {
            original.call(plugin, registry);
            return;
        }

        long start = System.nanoTime();
        original.call(plugin, registry);
        long end = System.nanoTime();

        RaspberryProfiler.recordEmiPlugin(plugin.getClass().getSimpleName(), end - start);
    }

    @Inject(method = "run", at = @At("RETURN"))
    private void raspberry$onFinish(CallbackInfo ci) {
        if (ModConfig.get().enableProfiler) {
            RaspberryProfiler.dumpLogs();
        }
    }
}