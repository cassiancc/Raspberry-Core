package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.mixin.copperagebackport.CopperGolemEntityAccessor;
import cc.cassian.raspberry.registry.RaspberryMobEffects;
import com.github.smallinger.copperagebackport.ModMemoryTypes;
import com.github.smallinger.copperagebackport.entity.CopperGolemEntity;
import com.github.smallinger.copperagebackport.registry.ModEntities;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamabnormals.caverns_and_chasms.core.other.tags.CCBlockTags;
import com.teamabnormals.caverns_and_chasms.core.registry.CCBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.eventbus.api.Event;

public class CopperAgeBackportCompat {
	public static void spawnCopperGolem(Level level, BlockPos pos, LocalRef<LivingEntity> golem, float yRot, BlockState abovestate) {
		CopperGolemEntity copperGolem = ModEntities.COPPER_GOLEM.get().create(level);
		if (copperGolem == null) {return;}

		if (abovestate.is(Blocks.LIGHTNING_ROD) || abovestate.is(CCBlocks.WAXED_LIGHTNING_ROD.get())) {
			copperGolem.spawn(WeatheringCopper.WeatherState.UNAFFECTED);
		} else if (abovestate.is(CCBlocks.EXPOSED_LIGHTNING_ROD.get()) || abovestate.is(CCBlocks.WAXED_EXPOSED_LIGHTNING_ROD.get())) {
			copperGolem.spawn(WeatheringCopper.WeatherState.EXPOSED);
		} else if (abovestate.is(CCBlocks.WEATHERED_LIGHTNING_ROD.get()) || abovestate.is(CCBlocks.WAXED_WEATHERED_LIGHTNING_ROD.get())) {
			copperGolem.spawn(WeatheringCopper.WeatherState.WEATHERED);
		} else if (abovestate.is(CCBlocks.OXIDIZED_LIGHTNING_ROD.get()) || abovestate.is(CCBlocks.WAXED_OXIDIZED_LIGHTNING_ROD.get())) {
			copperGolem.spawn(WeatheringCopper.WeatherState.OXIDIZED);
		}
		copperGolem.getBrain().setMemory(ModMemoryTypes.TRANSPORT_ITEMS_COOLDOWN_TICKS.get(), 140);

		if (abovestate.is(CCBlockTags.WAXED_COPPER_BLOCKS)) {
			((CopperGolemEntityAccessor) copperGolem).setNextWeatheringTick(-2L);
		}

		golem.set(copperGolem);
		copperGolem.moveTo((double)pos.getX() + (double)0.5F, (double)pos.getY() + 0.05, (double)pos.getZ() + (double)0.5F, yRot, 0.0F);
	}

	public static void saveCopperGolem(EntityStruckByLightningEvent event) {
		if (event.getEntity() instanceof CopperGolemEntity) {
			event.setCanceled(true);
		}
	}

	public static boolean isCopperGolem(LivingEntity entity) {
		return entity instanceof CopperGolemEntity;
	}
}
