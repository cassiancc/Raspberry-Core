package cc.cassian.raspberry.compat;

import com.ordana.spelunkery.reg.ModBlocks;
import com.ordana.spelunkery.reg.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class SpelunkeryCompat {

    private static Block cachedRockSalt;

    public static Block getRockSalt() {
        if (cachedRockSalt == null) {
            Block retrieved = safeGet(ModBlocks.ROCK_SALT_BLOCK);
            cachedRockSalt = (retrieved != null) ? retrieved : Blocks.DRIPSTONE_BLOCK;
        }
        return cachedRockSalt;
    }

    public static boolean checkDimensionalTears(ItemStack stack, ItemStack stack2) {
        Item fluidBottle = safeGet(ModItems.PORTAL_FLUID_BOTTLE);

        if (fluidBottle != null && fluidBottle != Items.AIR) {
            return stack.is(fluidBottle) && stack2.is(fluidBottle);
        }
        return false;
    }

    public static Item getDepthGauge() {
        Item item = safeGet(ModItems.DEPTH_GAUGE);
        return item != null ? item : Items.AIR;
    }

    public static Item getMagneticCompass() {
        Item item = safeGet(ModItems.MAGNETIC_COMPASS);
        return item != null ? item : Items.AIR;
    }

    /**
     * Helper method to safely get an item/block from a Supplier.
     */
    private static <T> T safeGet(Supplier<T> supplier) {
        try {
            if (supplier != null) {
                return supplier.get();
            }
        } catch (NullPointerException | IllegalStateException e) {
        }
        return null;
    }
}