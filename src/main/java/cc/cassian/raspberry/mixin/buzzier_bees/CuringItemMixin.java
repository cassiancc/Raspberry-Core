package cc.cassian.raspberry.mixin.buzzier_bees;

import cc.cassian.raspberry.config.ModConfig;
import com.teamabnormals.blueprint.common.advancement.EmptyTrigger;
import com.teamabnormals.buzzier_bees.common.item.CuringItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CuringItem.class)
public class CuringItemMixin {
	@Inject(
			method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/stats/Stat;)V"), cancellable = true)
	public void raspberry$disableCuring(ItemStack stack, Level worldIn, LivingEntity entityLiving, CallbackInfoReturnable<ItemStack> cir) {
		if (ModConfig.get().disableCurativeItems)
			cir.setReturnValue(stack);
	}
}
