package cc.cassian.raspberry.compat;

import net.minecraft.world.item.ItemStack;
import net.venturecraft.gliders.common.item.GliderItem;

public class GlidersCompat {
    public static boolean isGlider(ItemStack stack) {
        return stack.getItem() instanceof GliderItem;
    }
}
