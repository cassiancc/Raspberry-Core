package cc.cassian.raspberry.events;

import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EquipEvent {
	public static boolean shouldModifyReturnValue(ItemStack stack) {
		return stack.is(RaspberryTags.EQUIPPABLE_HELMET) || stack.is(RaspberryTags.EQUIPPABLE_CHESTPLATE);
	}

	public static Item modifyReturnValue(ItemStack stack) {
		if (stack.is(RaspberryTags.EQUIPPABLE_HELMET)) {
			return Items.CHAINMAIL_HELMET;
		}
		if (stack.is(RaspberryTags.EQUIPPABLE_CHESTPLATE)) {
			return Items.CHAINMAIL_CHESTPLATE;
		}
		return stack.getItem();
	}
}
