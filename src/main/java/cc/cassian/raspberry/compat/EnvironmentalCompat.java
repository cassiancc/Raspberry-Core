package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.RaspberryData;
import cc.cassian.raspberry.entity.ai.goal.HuntWormGoal;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.teamabnormals.blueprint.common.world.storage.tracking.IDataManager;
import com.teamabnormals.environmental.core.other.EnvironmentalProperties;
import com.teamabnormals.environmental.core.registry.EnvironmentalParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

public class EnvironmentalCompat {

    public static BlockBehaviour.Properties getTruffleProperties() {
        return EnvironmentalProperties.BURIED_TRUFFLE;
    }

    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();
        Entity target = event.getTarget();
        Level level = event.getLevel();
        RandomSource random = level.getRandom();

        if (target.getType().is(RaspberryTags.WORM_SEEKERS) && target.isAlive() && target instanceof Animal seeker) {
            IDataManager data = ((IDataManager) target);
            if (!seeker.isLeashed() && stack.is(RaspberryTags.WORM_SEEKER_ITEMS) && !seeker.isBaby()) {
                if (data.getValue(RaspberryData.WORM_HUNTING_TIME) == 0) {
                    if (level.dimensionType().natural()) {
                        data.setValue(RaspberryData.WORM_HUNTING_TIME, 4800);
                        if (!event.getEntity().isCreative()) stack.shrink(1);

                        if (level.isClientSide()) {
                            for (int i = 0; i < 7; ++i) {
                                double d0 = random.nextGaussian() * 0.02D;
                                double d1 = random.nextGaussian() * 0.02D;
                                double d2 = random.nextGaussian() * 0.02D;
                                // TODO: replace particles because these look piggy ofc
                                level.addParticle(EnvironmentalParticleTypes.PIG_FINDS_TRUFFLE.get(), target.getRandomX(1.0D), target.getRandomY() + 0.5D, target.getRandomZ(1.0D), d0, d1, d2);
                            }
                        }
                    } else if (level.isClientSide()) {
                        for (int i = 0; i < 7; ++i) {
                            double d0 = random.nextGaussian() * 0.02D;
                            double d1 = random.nextGaussian() * 0.02D;
                            double d2 = random.nextGaussian() * 0.02D;
                            level.addParticle(ParticleTypes.SMOKE, target.getRandomX(1.0D), target.getRandomY() + 0.5D, target.getRandomZ(1.0D), d0, d1, d2);
                        }
                    }

                    event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().is(RaspberryTags.WORM_SEEKERS) && entity instanceof Animal seeker) {
            Set<WrappedGoal> goals = seeker.goalSelector.getAvailableGoals();
            if (goals.stream().noneMatch((goal) -> goal.getGoal() instanceof HuntWormGoal))
                seeker.goalSelector.addGoal(2, new HuntWormGoal(seeker));
        }
    }

    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        Entity entity = event.getEntity();
        Level level = entity.getCommandSenderWorld();
        RandomSource random = level.getRandom();

        if (entity.getType().is(RaspberryTags.WORM_SEEKERS) && entity.isAlive()) {
            IDataManager data = ((IDataManager) entity);
            int huntingtime = data.getValue(RaspberryData.WORM_HUNTING_TIME);
            BlockPos wormpos = data.getValue(RaspberryData.WORM_POS);

            if (huntingtime == 0 || (data.getValue(RaspberryData.HAS_WORM_TARGET) && level.getBlockState(wormpos).getBlock() != RaspberryBlocks.WORMY_DIRT.getA().get())) {
                data.setValue(RaspberryData.HAS_WORM_TARGET, false);
                if (huntingtime > 0) data.setValue(RaspberryData.WORM_HUNTING_TIME, Math.max(-400, -huntingtime));
            } else {
                if (huntingtime > 0) {
                    data.setValue(RaspberryData.WORM_HUNTING_TIME, huntingtime - 1);
                } else {
                    data.setValue(RaspberryData.WORM_HUNTING_TIME, huntingtime + 1);
                    if (level.isClientSide() && data.getValue(RaspberryData.HAS_WORM_TARGET) && huntingtime % 10 == 0) {
                        double d0 = random.nextGaussian() * 0.02D;
                        double d1 = random.nextGaussian() * 0.02D;
                        double d2 = random.nextGaussian() * 0.02D;
                        level.addParticle(EnvironmentalParticleTypes.PIG_FINDS_TRUFFLE.get(), entity.getRandomX(1.0D), entity.getRandomY() + 0.5D, entity.getRandomZ(1.0D), d0, d1, d2);
                    }
                }
            }

            int sniffsoundtime = data.getValue(RaspberryData.SNIFF_SOUND_TIME);
            data.setValue(RaspberryData.SNIFF_SOUND_TIME, sniffsoundtime + 1);
            if (!level.isClientSide() && data.getValue(RaspberryData.LOOKING_FOR_WORM) && random.nextInt(90) < sniffsoundtime) {
                ((Mob)entity).playAmbientSound();
                data.setValue(RaspberryData.SNIFF_SOUND_TIME, -90);
            }
        }
    }

}
