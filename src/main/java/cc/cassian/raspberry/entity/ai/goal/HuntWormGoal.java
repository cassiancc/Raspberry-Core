package cc.cassian.raspberry.entity.ai.goal;

import cc.cassian.raspberry.RaspberryData;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.google.common.collect.Lists;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.environmental.core.other.tags.EnvironmentalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class HuntWormGoal extends Goal {
    private final Animal seeker;
    private final IDataManager data;
    private int runDelay;
    private int lookTimer;
    private Vec3 lookVector;

    public HuntWormGoal(Animal seekerIn) {
        this.seeker = seekerIn;
        this.data = (IDataManager) this.seeker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.runDelay > 0) {
            --this.runDelay;
            return false;
        } else {
            this.runDelay = this.adjustedTickDelay(20);
            if (this.data.getValue(RaspberryData.HAS_WORM_TARGET)) {
                return true;
            } else {
                return this.data.getValue(RaspberryData.WORM_HUNTING_TIME) > 0 && this.findWorm();
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.data.getValue(RaspberryData.HAS_WORM_TARGET) && this.data.getValue(RaspberryData.WORM_HUNTING_TIME) != 0;
    }

    @Override
    public void start() {
        this.lookVector = new Vec3(1.0D, 0.0D, 1.0D);
        this.moveToWorm();
        this.data.setValue(RaspberryData.LOOKING_FOR_WORM, true);
    }

    @Override
    public void stop() {
        this.data.setValue(RaspberryData.LOOKING_FOR_WORM, false);
    }

    @Override
    public void tick() {
        int wormhuntingtime = this.data.getValue(RaspberryData.WORM_HUNTING_TIME);
        BlockPos blockpos = this.data.getValue(RaspberryData.WORM_POS);
        Vec3 seekerpos = this.seeker.position();

        Vec3 vector3d = new Vec3((blockpos.getX() + 0.5D) - seekerpos.x(), 0.0D, (blockpos.getZ() + 0.5D) - seekerpos.z()).normalize();

        this.seeker.getLookControl().setLookAt(seekerpos.x() + vector3d.x() * this.lookVector.x(), this.seeker.getY() - 0.6D + this.lookVector.y(), seekerpos.z() + vector3d.z() * this.lookVector.z(), (float) (this.seeker.getMaxHeadYRot() + 20), (float) this.seeker.getMaxHeadXRot());

        if (blockpos.closerThan(this.seeker.blockPosition(), 4.0D)) {
            if (wormhuntingtime > 0)
                this.data.setValue(RaspberryData.WORM_HUNTING_TIME, -800);
        } else {
            if (this.lookTimer-- <= 0) {
                this.lookTimer = this.adjustedTickDelay(18 + this.seeker.getRandom().nextInt(9));
                this.lookVector = new Vec3((double) this.seeker.getRandom().nextFloat() * 1.2D, (double) this.seeker.getRandom().nextFloat() * 0.4D, (double) this.seeker.getRandom().nextFloat() * 1.2D);
            }

            this.moveToWorm();
        }
    }

    private void moveToWorm() {
        BlockPos blockpos = this.data.getValue(RaspberryData.WORM_POS);
        this.seeker.getNavigation().moveTo((double) ((float) blockpos.getX()) + 0.5D, blockpos.getY() + 1, (double) ((float) blockpos.getZ()) + 0.5D, 1.1D);
    }

    private boolean findWorm() {
        if (!this.seeker.level.dimensionType().natural())
            return false;

        int range = ModConfig.get().aquaculture_wormDiscoveryRange;
        int height = 16;
        BlockPos blockpos = this.seeker.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

        List<BlockPos> wormblocks = Lists.newArrayList();

        for (int i = 0; i < range; ++i) {
            boolean flag = false;

            for (int y = -height; y < height; ++y) {
                for (int x = 0; x <= i; x = x > 0 ? -x : 1 - x) {
                    for (int z = x < i && x > -i ? i : 0; z <= i; z = z > 0 ? -z : 1 - z) {
                        blockpos$mutable.setWithOffset(blockpos, x, y - 1, z);

                        if (this.seeker.isWithinRestriction(blockpos$mutable)) {
                            if (this.isWorm(this.seeker.level, blockpos$mutable)) {
                                this.data.setValue(RaspberryData.HAS_WORM_TARGET, true);
                                this.data.setValue(RaspberryData.WORM_POS, blockpos$mutable);
                                return true;
                            } else if (this.isSuitableForWorm(this.seeker.level, blockpos$mutable)) {
                                if (i <= 48 && !flag) {
                                    flag = true;
                                    wormblocks.clear();
                                }

                                wormblocks.add(blockpos$mutable.immutable());
                            }
                        }
                    }
                }
            }
        }

        if (wormblocks.size() > 0) {
            BlockPos wormpos = wormblocks.get(this.seeker.getRandom().nextInt(wormblocks.size()));

            this.seeker.level.setBlock(wormpos, RaspberryBlocks.WORMY_DIRT.getA().get().defaultBlockState(), 3);
            this.data.setValue(RaspberryData.HAS_WORM_TARGET, true);
            this.data.setValue(RaspberryData.WORM_POS, wormpos);

            return true;
        }

        return false;
    }

    private boolean isWorm(Level worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() == RaspberryBlocks.WORMY_DIRT.getA().get();
    }

    private boolean isSuitableForWorm(Level worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() != Blocks.DIRT)
            return false;

        for (Direction direction : Direction.values()) {
            BlockState blockstate = worldIn.getBlockState(pos.relative(direction));

            if (direction == Direction.UP) {
                if (!blockstate.is(EnvironmentalBlockTags.GRASS_LIKE)) {
                    if (!blockstate.is(BlockTags.DIRT) || !worldIn.getBlockState(pos.above()).is(EnvironmentalBlockTags.GRASS_LIKE)) {
                        return false;
                    }
                }
            } else if (!blockstate.is(BlockTags.DIRT)) {
                return false;
            }
        }

        return true;
    }
}