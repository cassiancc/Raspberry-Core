package cc.cassian.raspberry.events;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.TickEvent;

public class ChangeWeatherEvent {
	public static boolean cycleWeather(final Level level, final BlockPos pos, final Player player, final InteractionHand hand) {
		if (!ModConfig.get().weatherChanging) return false;
		ItemStack itemInHand = player.getItemInHand(hand);
		BlockState state = level.getBlockState(pos);
		final int DAY_TIME = 24000;
		if (state.is(ModRegistry.WIND_VANE.get())) {
			if (level instanceof ServerLevel serverLevel) {
				boolean raining = level.isRaining();
				boolean storming = level.isThundering();
				if (!raining && !storming) {
					serverLevel.setWeatherParameters(0, DAY_TIME, true, false);
					player.sendSystemMessage(Component.translatable("commands.weather.set.rain"));
				}
				else if (raining && !storming) {
					serverLevel.setWeatherParameters(0, DAY_TIME, true, true);
					player.sendSystemMessage(Component.translatable("commands.weather.set.thunder"));
				}
				else {
					serverLevel.setWeatherParameters(DAY_TIME, 0, false, false);
					player.sendSystemMessage(Component.translatable("commands.weather.set.clear"));
				}
			} else {
				ChangeWeatherEvent.ticksUntilStopSpinning = 60;
			}
			itemInHand.hurtAndBreak(100, player, player1 -> player1.broadcastBreakEvent(hand));
			level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), RaspberrySoundEvents.WEATHER_CYCLE.get(), SoundSource.PLAYERS,
					(float) ModConfig.get().mirrorVolumeModifier, 1.0F + (float) (player.getRandom().nextGaussian() * 0.35));
			player.getCooldowns().addCooldown(itemInHand.getItem(), 120);
			playParticle(level, pos);
			return true;
		}
		return false;
	}

	public static void playParticle(Level level, BlockPos pos) {
		ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.Y, level, pos, 5, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(25, 50));
		ParticleUtils.spawnParticlesAlongAxis(Direction.Axis.X, level, pos, 1, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(10, 50));
	}

	public static boolean cycleWeather(Level level, Player player, InteractionHand hand) {
		if (player.pick(player.getReachDistance(), 0, false) instanceof BlockHitResult blockHitResult) {
			return cycleWeather(level, blockHitResult.getBlockPos(), player, hand);
		}
		return false;
	}

	public static void tick(TickEvent.ClientTickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			if (ticksUntilStopSpinning > 0) {
				ticksUntilStopSpinning--;
			}
		}
	}

	private static int ticksUntilStopSpinning = 0;

	public static boolean shouldSpin() {
		return ChangeWeatherEvent.ticksUntilStopSpinning>1;
	}
}
