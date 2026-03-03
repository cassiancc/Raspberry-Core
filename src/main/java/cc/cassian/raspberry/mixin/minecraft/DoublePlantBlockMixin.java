package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DoublePlantBlock.class)
public class DoublePlantBlockMixin {
	@Inject(method = "placeAt", at = @At("HEAD"), cancellable = true)
	private static void raspberry$place(LevelAccessor level, BlockState state, BlockPos pos, int flags, CallbackInfo ci) {
		if (!level.getBlockState(pos.above()).getMaterial().isReplaceable()) {
			ci.cancel();
		}
	}
}
