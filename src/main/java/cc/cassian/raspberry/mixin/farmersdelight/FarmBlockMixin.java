package cc.cassian.raspberry.mixin.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {

    // Prevent Rich Soil Farmland from being converted to Dirt,
    // adapted from a never-upstreamed mixin by @SarahIsWeird.
    @Inject(at = @At(value = "HEAD"), method = "turnToDirt", cancellable = true)
    private static void turnToDirt(BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (state.getBlock() instanceof RichSoilFarmlandBlock) ci.cancel();
    }
}
