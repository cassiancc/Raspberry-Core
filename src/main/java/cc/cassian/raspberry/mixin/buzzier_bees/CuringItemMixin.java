package cc.cassian.raspberry.mixin.buzzier_bees;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamabnormals.blueprint.common.advancement.EmptyTrigger;
import com.teamabnormals.buzzier_bees.common.item.CuringItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CuringItem.class)
public class CuringItemMixin {
	@WrapOperation(
			method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
	public boolean raspberry$disableCuring(LivingEntity instance, MobEffect mobeffectinstance, Operation<Boolean> original) {
		if (!ModConfig.get().disableCurativeItems)
			original.call(instance, mobeffectinstance);
		return false;
	}
}
