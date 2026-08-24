package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HoneyBottleItem.class)
public class HoneyBottleMixin {
	@WrapOperation(
			method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeEffect(Lnet/minecraft/world/effect/MobEffect;)Z"))
	public boolean raspberry$disableCuring(LivingEntity instance, MobEffect mobeffectinstance, Operation<Boolean> original) {
		if (!ModConfig.get().disableCurativeItems)
			original.call(instance, mobeffectinstance);
		return false;
	}
}
