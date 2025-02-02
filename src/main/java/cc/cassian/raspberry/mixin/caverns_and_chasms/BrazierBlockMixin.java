package cc.cassian.raspberry.mixin.caverns_and_chasms;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.teamabnormals.caverns_and_chasms.common.block.BrazierBlock;

import static vectorwing.farmersdelight.common.block.StoveBlock.LIT;

@Pseudo
@Mixin(BrazierBlock.class)
public abstract class BrazierBlockMixin {

    @Inject(
            method = "getStateForPlacement",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void startStovesLit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!ModConfig.get().braziersStartLit)
            cir.setReturnValue(cir.getReturnValue().setValue(LIT, false));
    }

    @Inject(method = "animateTick",
    at = @At("HEAD"),
    cancellable = true)
    private void hush(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!state.getValue(LIT)) {
            ci.cancel();
        }
    }
}
