package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.mixin.copperagebackport.CopperGolemEntityAccessor;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamabnormals.caverns_and_chasms.core.other.tags.CCBlockTags;
import com.teamabnormals.caverns_and_chasms.core.registry.CCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class CopperAgeBackportCompat {
	public static void spawnCopperGolem(Level level, BlockPos pos, LocalRef<LivingEntity> golem, BlockState state) {
		BlockState abovestate = level.getBlockState(pos.above());
		CopperGolemEntity coppergolem = ModEntities.COPPER_GOLEM.get().create(level);
		if (coppergolem == null) {return;}

		if (abovestate.is(Blocks.LIGHTNING_ROD) || abovestate.is(CCBlocks.WAXED_LIGHTNING_ROD.get())) {
			coppergolem.setWeatherState(WeatheringCopper.WeatherState.UNAFFECTED);
		} else if (abovestate.is(CCBlocks.EXPOSED_LIGHTNING_ROD.get()) || abovestate.is(CCBlocks.WAXED_EXPOSED_LIGHTNING_ROD.get())) {
			coppergolem.setWeatherState(WeatheringCopper.WeatherState.EXPOSED);
		} else if (abovestate.is(CCBlocks.WEATHERED_LIGHTNING_ROD.get()) || abovestate.is(CCBlocks.WAXED_WEATHERED_LIGHTNING_ROD.get())) {
			coppergolem.setWeatherState(WeatheringCopper.WeatherState.WEATHERED);
		} else if (abovestate.is(CCBlocks.OXIDIZED_LIGHTNING_ROD.get()) || abovestate.is(CCBlocks.WAXED_OXIDIZED_LIGHTNING_ROD.get())) {
			coppergolem.setWeatherState(WeatheringCopper.WeatherState.OXIDIZED);
		}

		if (abovestate.is(CCBlockTags.WAXED_COPPER_BLOCKS)) {
			((CopperGolemEntityAccessor) coppergolem).setNextWeatheringTick(-2L);
		}

		golem.set(coppergolem);
		coppergolem.moveTo((double)pos.getX() + (double)0.5F, (double)pos.getY() + 0.05, (double)pos.getZ() + (double)0.5F, state.getValue(CarvedPumpkinBlock.FACING).toYRot(), 0.0F);
	}
}
