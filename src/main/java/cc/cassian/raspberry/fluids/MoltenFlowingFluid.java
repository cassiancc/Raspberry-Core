package cc.cassian.raspberry.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class MoltenFlowingFluid extends ForgeFlowingFluid {
	public static final float MIN_LEVEL_CUTOFF = 0.44444445F;

	public MoltenFlowingFluid(Properties moltenFluidProperties) {
		super(moltenFluidProperties);
	}

	@Override
	public void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		BlockPos blockpos = pos.above();
		if (level.getBlockState(blockpos).isAir() && !level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
			if (random.nextInt(100) == 0) {
				double d0 = (double)pos.getX() + random.nextDouble();
				double d1 = (double)pos.getY() + 1.0;
				double d2 = (double)pos.getZ() + random.nextDouble();
				level.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0);
				level.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			if (random.nextInt(200) == 0) {
				level.playLocalSound(
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						SoundEvents.LAVA_AMBIENT,
						SoundSource.BLOCKS,
						0.2F + random.nextFloat() * 0.2F,
						0.9F + random.nextFloat() * 0.15F,
						false
				);
			}
		}
	}

	@Override
	public void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
		if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
			int i = random.nextInt(3);
			if (i > 0) {
				BlockPos blockpos = pos;

				for (int j = 0; j < i; j++) {
					blockpos = blockpos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
					if (!level.isLoaded(blockpos)) {
						return;
					}

					BlockState blockstate = level.getBlockState(blockpos);
					if (blockstate.isAir()) {
						if (this.hasFlammableNeighbours(level, blockpos)) {
							level.setBlockAndUpdate(blockpos, ForgeEventFactory.fireFluidPlaceBlockEvent(level, blockpos, pos, Blocks.FIRE.defaultBlockState()));
							return;
						}
					} else if (blockstate.getMaterial().blocksMotion()) {
						return;
					}
				}
			} else {
				for (int k = 0; k < 3; k++) {
					BlockPos blockpos1 = pos.offset(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
					if (!level.isLoaded(blockpos1)) {
						return;
					}

					if (level.isEmptyBlock(blockpos1.above()) && this.isFlammable(level, blockpos1, Direction.UP)) {
						level.setBlockAndUpdate(blockpos1.above(), ForgeEventFactory.fireFluidPlaceBlockEvent(level, blockpos1.above(), pos, Blocks.FIRE.defaultBlockState()));
					}
				}
			}
		}
	}

	private boolean hasFlammableNeighbours(LevelReader level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (this.isFlammable(level, pos.relative(direction), direction.getOpposite())) {
				return true;
			}
		}

		return false;
	}

	private boolean isFlammable(LevelReader level, BlockPos pos, Direction face) {
		return (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight() || level.hasChunkAt(pos)) && level.getBlockState(pos).isFlammable(level, pos, face);
	}

	@Nullable
	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.DRIPPING_LAVA;
	}

	@Override
	protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		this.fizz(level, pos);
	}

	@Override
	public int getDropOff(LevelReader level) {
		return level.dimensionType().ultraWarm() ? 1 : 2;
	}

	@Override
	public boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
		return state.getHeight(level, pos) >= MIN_LEVEL_CUTOFF && fluid.is(FluidTags.WATER);
	}

	@Override
	public int getTickDelay(LevelReader level) {
		return level.dimensionType().ultraWarm() ? 10 : 30;
	}

	@Override
	public int getSpreadDelay(Level level, BlockPos pos, FluidState arg3, FluidState arg4) {
		int i = this.getTickDelay(level);
		if (!arg3.isEmpty()
				&& !arg4.isEmpty()
				&& !(Boolean)arg3.getValue(FALLING)
				&& !(Boolean)arg4.getValue(FALLING)
				&& arg4.getHeight(level, pos) > arg3.getHeight(level, pos)
				&& level.getRandom().nextInt(4) != 0) {
			i *= 4;
		}

		return i;
	}

	private void fizz(LevelAccessor level, BlockPos pos) {
		level.levelEvent(1501, pos, 0);
	}

	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}

	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
	}

	public static class Flowing extends MoltenFlowingFluid {
		public Flowing(Properties moltenLeadProperties) {
			super(moltenLeadProperties);
		}

		@Override
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		@Override
		public boolean isSource(FluidState state) {
			return false;
		}
	}

	public static class Source extends MoltenFlowingFluid {
		public Source(Properties moltenFluidProperties) {
			super(moltenFluidProperties);
		}

		@Override
		public int getAmount(FluidState state) {
			return 8;
		}

		@Override
		public boolean isSource(FluidState state) {
			return true;
		}
	}
}
