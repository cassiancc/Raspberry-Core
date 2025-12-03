package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryItems;
import com.starfish_studios.naturalist.Naturalist;
import com.starfish_studios.naturalist.core.registry.NaturalistEntityTypes;
import com.starfish_studios.naturalist.core.registry.NaturalistSoundEvents;
import com.starfish_studios.naturalist.item.forge.CaughtMobItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;

import java.util.function.Supplier;

public class NaturalistCompat {
    public static boolean match(ItemStack arg, ItemStack arg2) {
        if (arg.getItem() instanceof CaughtMobItem && ModConfig.get().naturalist_stackableItems && ItemStack.matches(arg, arg2)) {
            return true;
        }
        return false;
    }

    public static Supplier<Item> registerFireflyItem() {
        return RaspberryItems.registerItem("firefly", () -> new CaughtMobItem(NaturalistEntityTypes.FIREFLY, ()-> Fluids.EMPTY, NaturalistSoundEvents.SNAIL_FORWARD, new Item.Properties()));
    }
}
