package cc.cassian.raspberry.mixin.farmersdelight;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.tag.ForgeTags;

@Mixin(BambooSaplingBlock.class)
public class BambooSaplingMixin {
    @Inject(method = "getDestroyProgress", at = @At(value = "HEAD"), cancellable = true)
    private void mixin(BlockState state, Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (player.getMainHandItem().is(ForgeTags.TOOLS_KNIVES)) cir.setReturnValue(1.0F);
    }
}
