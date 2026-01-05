package cc.cassian.raspberry.mixin.profiler;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.util.RaspberryProfiler;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabsMixin {

    @Inject(method = "buildContents", at = @At("HEAD"))
    private void raspberry$startProfile(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        if (ModConfig.get().enableProfiler) {
            RaspberryProfiler.TAB_START_TIME.set(System.nanoTime());
        }
    }

    @Inject(method = "buildContents", at = @At("RETURN"))
    private void raspberry$endProfile(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        if (ModConfig.get().enableProfiler) {
            long start = RaspberryProfiler.TAB_START_TIME.get();
            long end = System.nanoTime();

            CreativeModeTab tab = (CreativeModeTab) (Object) this;

            String name = tab.getDisplayName().getString();
            if (name.isEmpty()) name = "Unknown Tab";

            RaspberryProfiler.recordCreativeTab(name, end - start);

            if (tab.getType() == CreativeModeTab.Type.SEARCH) {
                RaspberryProfiler.dumpLogs();
            }
        }
    }
}