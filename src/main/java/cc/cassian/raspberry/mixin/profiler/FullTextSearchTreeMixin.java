package cc.cassian.raspberry.mixin.profiler;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.util.RaspberryProfiler;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(FullTextSearchTree.class)
public class FullTextSearchTreeMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static Function<Object, Stream<String>> raspberry$profileTextIndexing(Function<Object, Stream<String>> original) {
        if (!ModConfig.get().enableProfiler) return original;

        return (element) -> {
            long start = System.nanoTime();
            Stream<String> result = original.apply(element);
            long end = System.nanoTime();

            raspberry$recordTime(element, end - start);
            return result;
        };
    }

    @Unique
    private static void raspberry$recordTime(Object element, long nanos) {
        String modid = "unknown";
        if (element instanceof ItemStack stack) {
            try {
                modid = stack.getItem().getCreatorModId(stack);
                if (modid == null) {
                    ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
                    if (key != null) modid = key.getNamespace();
                }
            } catch (Exception ignored) {}
        }
        RaspberryProfiler.recordSearchIndex(modid, nanos);
    }
}