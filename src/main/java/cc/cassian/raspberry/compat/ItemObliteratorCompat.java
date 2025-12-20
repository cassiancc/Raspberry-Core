package cc.cassian.raspberry.compat;

import dev.emi.emi.api.EmiRegistry;
import elocindev.item_obliterator.forge.utils.Utils;

public class ItemObliteratorCompat {
    public static void hideStacks(EmiRegistry emiRegistry) {
        emiRegistry.removeEmiStacks(emiStack -> Utils.isDisabled(emiStack.getItemStack()));
    }
}
