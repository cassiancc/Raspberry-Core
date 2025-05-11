package cc.cassian.raspberry.compat;

import com.simibubi.create.AllItems;
import net.minecraft.world.item.ItemStack;

public class CreateCompat {
    public static boolean isGoggles(ItemStack stack) {
        return stack.is(AllItems.GOGGLES.get());
    }
}
