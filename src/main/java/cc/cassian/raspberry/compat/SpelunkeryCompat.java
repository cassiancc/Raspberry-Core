package cc.cassian.raspberry.compat;

import com.ordana.spelunkery.reg.ModItems;
import net.minecraft.world.item.ItemStack;

public class SpelunkeryCompat {
    public static boolean checkDimensionalTears(ItemStack stack, ItemStack stack2) {
        return stack.is(ModItems.PORTAL_FLUID_BOTTLE.get()) && stack2.is(ModItems.PORTAL_FLUID_BOTTLE.get());
    }
}
