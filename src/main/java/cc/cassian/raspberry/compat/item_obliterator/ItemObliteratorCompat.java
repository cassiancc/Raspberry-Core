package cc.cassian.raspberry.compat.item_obliterator;

import dev.emi.emi.api.EmiRegistry;
import elocindev.item_obliterator.forge.ItemObliterator;
import elocindev.item_obliterator.forge.utils.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;

public class ItemObliteratorCompat {

	public static void hideItems(EmiRegistry registry) {
		registry.removeEmiStacks(o->Utils.isDisabled(o.getItemStack()));
	}

	public static List<Item> getBlacklistedItems() {
		return ItemObliterator.blacklisted_items.stream().map(c-> Registry.ITEM.get(new ResourceLocation(c))).toList();
	}
}
