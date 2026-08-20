package cc.cassian.raspberry.mixin.cookscollection;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.baisylia.cookscollection.block.ModBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public abstract class OvenMenuMixin {

	@WrapOperation(method = "method_17696", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
	private static boolean forceAllowOvens(BlockState instance, Block block, Operation<Boolean> original) {
		if (block.equals(ModBlocks.OVEN.get())) {
			return original.call(instance, block) || instance.is(RaspberryBlocks.SILT_OVEN.getBlock()) || instance.is(RaspberryBlocks.ASH_OVEN.getBlock());
		}
		return original.call(instance, block);
	}
}
