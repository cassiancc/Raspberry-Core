package cc.cassian.raspberry.mixin.supplementaries;

import cc.cassian.raspberry.config.ModConfig;
import net.mehvahdjukaar.supplementaries.common.block.blocks.FaucetBlock;
import net.mehvahdjukaar.supplementaries.common.block.tiles.FaucetBlockTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FaucetBlockTile.class)
class FaucetBlockTileMixin {
    @Inject(method = "tryExtract", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void tryExtract(Level level, BlockPos pos, BlockState state, boolean doTransfer, CallbackInfoReturnable<Integer> cir) {
        if (ModConfig.get().disableFaucetSourceBlocks && raspberryCore$blockBehindIsFluidSource(level, pos, state)) {
            cir.setReturnValue(0);
            cir.cancel();
        }
    }

    @Inject(method = "updateContainedFluidVisuals", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void updateContainedFluidVisuals(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.get().disableFaucetSourceBlocks && raspberryCore$blockBehindIsFluidSource(level, pos, state)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Unique
    private boolean raspberryCore$blockBehindIsFluidSource(Level level, BlockPos pos, BlockState state) {
        Direction dir = state.getValue(FaucetBlock.FACING);
        BlockPos behind = pos.relative(dir.getOpposite());
        FluidState fluidState = level.getFluidState(behind);
        return !fluidState.isEmpty() && fluidState.isSource();
    }
}
