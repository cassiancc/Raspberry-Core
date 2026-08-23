package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StatsScreen.GeneralStatisticsList.class)
public class StatsScreenMixin {
	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/achievement/StatsScreen$GeneralStatisticsList;addEntry(Lnet/minecraft/client/gui/components/AbstractSelectionList$Entry;)I"))
	private int hideStats(StatsScreen.GeneralStatisticsList instance, AbstractSelectionList.Entry entry, Operation<Integer> original, @Local Stat<ResourceLocation> stat) {
		if (ModConfig.get().removedStatistics.contains(stat.getValue().toString())) {
			return 0;
		};
		return original.call(instance, entry);
	}
}
