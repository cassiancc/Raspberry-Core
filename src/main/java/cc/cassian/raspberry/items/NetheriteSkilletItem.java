package cc.cassian.raspberry.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import vectorwing.farmersdelight.common.item.SkilletItem;

public class NetheriteSkilletItem extends SkilletItem {

	public NetheriteSkilletItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		if (enchantment.equals(Enchantments.FIRE_ASPECT)) {
			return 1;
		}
		return super.getEnchantmentLevel(stack, enchantment);
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return Tiers.NETHERITE.getRepairIngredient().test(toRepair);
	}

	@Override
	public int getEnchantmentValue() {
		return SKILLET_TIER.getEnchantmentValue();
	}

}
