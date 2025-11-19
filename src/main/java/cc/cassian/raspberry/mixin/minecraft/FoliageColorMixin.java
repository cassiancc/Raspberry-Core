package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.level.FoliageColor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FoliageColor.class)
public class FoliageColorMixin {
    @WrapMethod(method = "getBirchColor")
    private static int mixin(Operation<Integer> original) {
        if (ModConfig.get().disableBirchLeafTinting)
            return -1;
        return original.call();
    }
}
