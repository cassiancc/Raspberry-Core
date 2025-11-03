package cc.cassian.raspberry.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.block.blocks.CandleHolderBlock;
import net.mehvahdjukaar.supplementaries.reg.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(CandleHolderBlock.class)
public class CandleHolderBlockMixin {
    @Inject(method = "canSurvive", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void canHangFromPosts(BlockState state, LevelReader worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (worldIn.getBlockState(pos.relative(Direction.UP)).is(ModTags.POSTS)) {
            cir.setReturnValue(true);
        }
    }
}
