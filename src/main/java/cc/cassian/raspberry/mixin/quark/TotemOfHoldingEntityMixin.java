package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.events.EquipEvent;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.addons.oddities.entity.TotemOfHoldingEntity;

@Mixin(TotemOfHoldingEntity.class)
public class TotemOfHoldingEntityMixin {
	@WrapOperation(
			method = "skipAttackInteraction",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", ordinal = 0)
	)
	private static Item reEquipGlidersAndGoggles(ItemStack stack, Operation<Item> original) {
		if (EquipEvent.shouldModifyReturnValue(stack)) {
			return EquipEvent.modifyReturnValue(stack);
		}
		else return original.call(stack);
	}

}
