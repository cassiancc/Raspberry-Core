package cc.cassian.raspberry.compat;

import elocindev.item_obliterator.forge.utils.Utils;
import net.minecraft.world.item.ItemStack;

public class ItemObliteratorCompat {

    public static boolean shouldHide(ItemStack stack) {
        return Utils.isDisabled(stack);
    }
}
