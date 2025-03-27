package cc.cassian.raspberry.mixin.farmersdelight;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.StoveBlock;

import static vectorwing.farmersdelight.common.block.StoveBlock.LIT;

@Mixin(StoveBlock.class)
public abstract class StoveBlockMixin {
    @Inject(
            method = "getStateForPlacement",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void startStovesLit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (!ModConfig.get().stovesStartLit && state != null)
            cir.setReturnValue(state.setValue(LIT, false));
    }
}
