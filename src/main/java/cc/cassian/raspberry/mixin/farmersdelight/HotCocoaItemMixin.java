package cc.cassian.raspberry.mixin.farmersdelight;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.item.HotCocoaItem;

@Mixin(HotCocoaItem.class)
public class HotCocoaItemMixin {
	@Inject(
			method = "affectConsumer", remap = false, at = @At(value = "HEAD"), cancellable = true)
	public void raspberry$disableCuring(ItemStack stack, Level level, LivingEntity consumer, CallbackInfo ci) {
		if (ModConfig.get().disableCurativeItems)
			ci.cancel();
	}
}
