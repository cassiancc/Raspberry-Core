package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.CreateCompat;
import cc.cassian.raspberry.compat.GlidersCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
		if (ModCompat.GLIDERS && GlidersCompat.isGlider(stack)) {
			return Items.CHAINMAIL_CHESTPLATE;
		}
		if (ModCompat.CREATE && CreateCompat.isGoggles(stack)) {
			return Items.CHAINMAIL_HELMET;
		}
		else return original.call(stack);
	}

}
