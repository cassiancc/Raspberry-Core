package cc.cassian.raspberry.mixin.naturalist;

import cc.cassian.raspberry.config.ModConfig;
import com.starfish_studios.naturalist.platform.forge.CommonPlatformHelperImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CommonPlatformHelperImpl.class)
public class StackableMobItemsMixin {
    @ModifyArg(method = "lambda$registerCaughtMobItem$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;"))
    private static int stackableButterflies(int maxStackSize) {
        if (ModConfig.get().naturalist_stackableItems)
            return 64;
        return maxStackSize;
    }

    @ModifyArg(method = "lambda$registerCaughtMobItem$5", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;stacksTo(I)Lnet/minecraft/world/item/Item$Properties;"))
    private static int stackableButterflies2(int maxStackSize) {
        if (ModConfig.get().naturalist_stackableItems)
            return 64;
        return maxStackSize;
    }
}
