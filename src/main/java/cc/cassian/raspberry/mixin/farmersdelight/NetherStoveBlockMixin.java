package cc.cassian.raspberry.mixin.farmersdelight;

import cc.cassian.raspberry.config.ModConfig;
import com.soytutta.mynethersdelight.common.block.NetherStoveBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static vectorwing.farmersdelight.common.block.StoveBlock.LIT;

@Pseudo
@Mixin(NetherStoveBlock.class)
public abstract class NetherStoveBlockMixin {
    @Inject(
            method = "getStateForPlacement",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void startStovesLit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!ModConfig.get().stovesStartLit)
            cir.setReturnValue(cir.getReturnValue().setValue(LIT, false));
    }
}
