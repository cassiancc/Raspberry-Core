package cc.cassian.raspberry.mixin.minecraft;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PointedDripstoneBlock.class)
public interface PointedDripstoneBlockAccessor {
	@Invoker
	static boolean callCanDripThrough(BlockGetter level, BlockPos pos, BlockState state) {
		throw new UnsupportedOperationException();
	}
}
