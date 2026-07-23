package cc.cassian.raspberry.events;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class DripstoneEvent {

	/**
	 * @param level - The level this conversion is taking place in.
	 * @param blockPos - The position of the dripstone.
	 * @param fluidInfo - The fluid being tested for conversion.
	 * @param fluid - The fluid being tested for conversion.
	 * @param targetBlock - The block we want to convert to.
	 * @param convertsToTarget - The blocks that are possible to convert from.
	 * @param requiredFluid - The fluid required for conversion to take place.
	 * @param requiredBlock - The block required below the fluid for conversion to take place.
	 * <br><br>
	 * In the world, this would look like:
	 * <pre>{@code
	 * requiredFluid/fluid/fluidInfo (usually water)
	 * requiredBlock (rock salt)
	 * blockPos (pointed dripstone block)
	 * convertsToTarget->targetBlock (dripstone, converted to rock salt)
	 * }</pre>
	 */
	public static void convertBlockViaDripstone(ServerLevel level, BlockPos blockPos, PointedDripstoneBlock.FluidInfo fluidInfo, Fluid fluid, Block targetBlock, TagKey<Block> convertsToTarget, FlowingFluid requiredFluid, Block requiredBlock) {
		if (level.getBlockState(fluidInfo.pos().below()).is(requiredBlock) && fluid == requiredFluid) {
			BlockPos blockPos2 = findConvertible(level, blockPos, convertsToTarget);
			if (blockPos2 != null) {
				level.levelEvent(LevelEvent.DRIPSTONE_DRIP, blockPos, 0);
				BlockState blockState = targetBlock.defaultBlockState();
				level.setBlockAndUpdate(blockPos2, blockState);
			}
		}
	}


	@Nullable
	private static BlockPos findConvertible(Level level, BlockPos pos, TagKey<Block> convertsToTarget) {
		Predicate<BlockState> statePredicate = (arg2) -> {
			return arg2.is(convertsToTarget);
		};
		BiPredicate<BlockPos, BlockState> positionalStatePredicate = (arg2, arg3) -> {
			return PointedDripstoneBlock.canDripThrough(level, arg2, arg3) || arg3.is(Blocks.POINTED_DRIPSTONE);
		};

		return findBlockVertical(level, pos, Direction.DOWN.getAxisDirection(), positionalStatePredicate, statePredicate).orElse(null);
	}

	private static Optional<BlockPos> findBlockVertical(LevelAccessor level, BlockPos pos, Direction.AxisDirection axis,
														BiPredicate<BlockPos, BlockState> positionalStatePredicate, Predicate<BlockState> statePredicate) {
		Direction direction = Direction.get(axis, Direction.Axis.Y);
		BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
		for (int i = 1; i < 11; i++) {
			mutableBlockPos.move(direction);
			BlockState blockState = level.getBlockState(mutableBlockPos);
			if (statePredicate.test(blockState)) {
				return Optional.of(mutableBlockPos.immutable());
			}
			if (!positionalStatePredicate.test(mutableBlockPos, blockState)) {
				return Optional.empty();
			}
		}

		return Optional.empty();
	}
}
