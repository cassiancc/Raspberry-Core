package cc.cassian.raspberry.mixin.windswept;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.rosemods.windswept.common.levelgen.feature.PineTreeFeature;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PineTreeFeature.class)
public class PineTreeFeatureMixin {

    @WrapMethod(method = "addPinecones", remap = false)
    private void raspberry$skipPineconePlacement(BlockPos pos, int amount, boolean isFairy, Operation<Void> original) {
        if (!ModConfig.get().disableWindsweptPineconePlacement) {
            original.call(pos, amount, isFairy);
        }
    }
}