package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.config.ModConfig;
import com.starfish_studios.naturalist.item.forge.CaughtMobItem;
import net.minecraft.world.item.ItemStack;

public class NaturalistCompat {
    public static boolean match(ItemStack arg, ItemStack arg2) {
        if (arg.getItem() instanceof CaughtMobItem && ModConfig.get().naturalist_stackableItems && ItemStack.matches(arg, arg2)) {
            return true;
        }
        return false;
    }
}
