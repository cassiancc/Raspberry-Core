package cc.cassian.raspberry.compat.item_obliterator;

import dev.emi.emi.api.EmiRegistry;
import elocindev.item_obliterator.forge.utils.Utils;

public class ItemObliteratorCompat {

	public static void hideItems(EmiRegistry registry) {
		registry.removeEmiStacks(o->Utils.isDisabled(o.getItemStack()));
	}
}
