package cc.cassian.raspberry.mixin.profiler;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.util.RaspberryProfiler;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SearchRegistry.class)
public class SearchRegistryMixin {

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void raspberry$onReloadComplete(ResourceManager resourceManager, CallbackInfo ci) {
        if (ModConfig.get().enableProfiler) {
            RaspberryProfiler.dumpLogs();
        }
    }
}