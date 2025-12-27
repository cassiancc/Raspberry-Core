package cc.cassian.raspberry.compat;

import elocindev.item_obliterator.forge.utils.Utils;
import net.minecraft.world.item.ItemStack;

public class ItemObliteratorCompat {

    public static boolean shouldHide(ItemStack stack) {
        try {
            return !stack.isEmpty() && Utils.isDisabled(stack);
        } catch (Exception e) {
            return false;
        }
    }
}