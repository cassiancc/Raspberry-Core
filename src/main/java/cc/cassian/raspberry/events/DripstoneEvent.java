package cc.cassian.raspberry.events;

import cc.cassian.raspberry.mixin.minecraft.PointedDripstoneBlockAccessor;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.ordana.spelunkery.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class DripstoneEvent {


	public static final DripstoneEvent.DripstoneGenerator REDSTONE = DripstoneEvent.DripstoneGenerator.corundumGenerator("red", Tags.Blocks.ORES_REDSTONE, ModBlocks.ROUGH_CINNABAR_BLOCK.get());
	public static final DripstoneEvent.DripstoneGenerator QUARTZ = DripstoneEvent.DripstoneGenerator.corundumGenerator("white", Tags.Blocks.ORES_QUARTZ, ModBlocks.ROUGH_QUARTZ_BLOCK.get());
	public static final DripstoneEvent.DripstoneGenerator AMETHYST = DripstoneEvent.DripstoneGenerator.corundumGenerator("indigo", BlockTags.EMERALD_ORES, ModBlocks.ROUGH_EMERALD_BLOCK.get());
	public static final DripstoneEvent.DripstoneGenerator DIAMOND = DripstoneEvent.DripstoneGenerator.corundumGenerator("blue", BlockTags.DIAMOND_ORES, ModBlocks.ROUGH_DIAMOND_BLOCK.get());
	public static final DripstoneEvent.DripstoneGenerator SALT = new DripstoneEvent.DripstoneGenerator(getDefaultState("spelunkery:rock_salt_block"), RaspberryTags.CONVERTS_TO_SALT, Fluids.WATER, get("spelunkery:rock_salt_block"), false);

	/**
	 * @param targetBlockState The block we want to convert to.
	 * @param convertsToTarget The blocks that are possible to convert from.
	 * @param requiredFluid The fluid required for conversion to take place.
	 * @param requiredBlock The block required below the fluid for conversion to take place.
	 * @param generateAsCrystal Whether to place the requiredBlock above the block being converted.
	 * <br><br>
	 * In the world, this would look like:
	 * <pre>{@code
	 * requiredFluid/fluid/fluidInfo (usually water)
	 * requiredBlock (rock salt)
	 * blockPos (pointed dripstone block)
	 * (air) - (with generateAsCrystal, targetBlock is placed here)
	 * convertsToTarget->targetBlock (dripstone, converted to rock salt)
	 * }</pre>
	 */
	public record DripstoneGenerator(BlockState targetBlockState, TagKey<Block> convertsToTarget, Fluid requiredFluid, Block requiredBlock, boolean generateAsCrystal) {

		public static DripstoneGenerator corundumGenerator(String colour, TagKey<Block> convertsToTarget, Block targetBlock) {
			return new DripstoneGenerator(getCluster(colour), convertsToTarget, Fluids.LAVA, targetBlock, true);
		}

		public boolean invalid() {
			return requiredBlock == null || targetBlockState == null;
		}
	}

	/**
	 * @param level The level this conversion is taking place in.
	 * @param blockPos The position of the dripstone.
	 * @param fluidInfo The fluid being tested for conversion.
	 * @param fluid The fluid being tested for conversion.
	 * @param dripstoneGenerator The generator we're attempting to check.
	 *
	 */
	public static void convertBlockViaDripstone(ServerLevel level, @Nullable BlockPos blockPos, PointedDripstoneBlock.FluidInfo fluidInfo, Fluid fluid, DripstoneGenerator dripstoneGenerator) {
		if (blockPos == null || dripstoneGenerator.invalid()) return;
		if (level.getBlockState(fluidInfo.pos().below()).is(dripstoneGenerator.requiredBlock) && fluid == dripstoneGenerator.requiredFluid) {
			BlockPos targetPos = findConvertible(level, blockPos, dripstoneGenerator.convertsToTarget);
			if (targetPos != null) {
				level.levelEvent(LevelEvent.DRIPSTONE_DRIP, blockPos, 0);
				BlockPos above = targetPos.above();
				if (dripstoneGenerator.generateAsCrystal & level.getBlockState(above).isAir()) {
					level.setBlockAndUpdate(above, dripstoneGenerator.targetBlockState);
				} else if (!dripstoneGenerator.generateAsCrystal) {
					level.setBlockAndUpdate(targetPos, dripstoneGenerator.targetBlockState);
				}
			}
		}
	}


	@Nullable
	private static BlockPos findConvertible(Level level, BlockPos pos, TagKey<Block> convertsToTarget) {
		Predicate<BlockState> statePredicate = (arg2) -> {
			return arg2.is(convertsToTarget);
		};
		BiPredicate<BlockPos, BlockState> positionalStatePredicate = (arg2, arg3) -> {
			return PointedDripstoneBlockAccessor.callCanDripThrough(level, arg2, arg3) || arg3.is(Blocks.POINTED_DRIPSTONE);
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

	@Unique
	private static @Nullable BlockState getCluster(String colour) {
		Block cluster = ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("quark", colour + "_corundum_cluster"));
		if (cluster == null) return null;
		return cluster.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP);
	}

	private static @Nullable Block get(String s) {
		return ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(s));
	}

	private static @Nullable BlockState getDefaultState(String s) {
		Block cluster = ForgeRegistries.BLOCKS.getValue(ResourceLocation.parse(s));
		if (cluster == null) return null;
		return cluster.defaultBlockState();
	}
}
