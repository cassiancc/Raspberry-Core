package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.teamabnormals.buzzier_bees.common.item.CuringItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBottleItem.class)
public class HoneyBottleMixin {
	@Inject(
			method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/stats/Stat;)V"), cancellable = true)
	public void raspberry$disableCuring(ItemStack stack, Level worldIn, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
		if (ModConfig.get().disableCurativeItems)
			cir.setReturnValue(stack);
	}
}
