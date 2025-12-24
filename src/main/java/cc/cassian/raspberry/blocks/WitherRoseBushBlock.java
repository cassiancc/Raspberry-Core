package cc.cassian.raspberry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBushBlock extends TallFlowerBlock {
	public WitherRoseBushBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return super.mayPlaceOn(state, level, pos) || state.is(Blocks.NETHERRACK) || state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		VoxelShape voxelShape = this.getShape(state, level, pos, CollisionContext.empty());
		Vec3 vec3 = voxelShape.bounds().getCenter();
		double d = (double)pos.getX() + vec3.x;
		double e = (double)pos.getZ() + vec3.z;

		for (int i = 0; i < 3; ++i) {
			if (random.nextBoolean()) {
				level.addParticle(ParticleTypes.SMOKE, d + random.nextDouble() / 5.0D, (double)pos.getY() + (0.5D - random.nextDouble()), e + random.nextDouble() / 5.0D, 0.0F, 0.0F, 0.0F);
			}
		}
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (!level.isClientSide && level.getDifficulty() != Difficulty.PEACEFUL) {
			if (entity instanceof LivingEntity livingEntity) {
				if (!livingEntity.isInvulnerableTo(level.damageSources().wither())) {
					livingEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
				}
			}
		}
	}
}
