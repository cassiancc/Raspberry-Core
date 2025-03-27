package cc.cassian.raspberry.compat;

import com.ordana.spelunkery.reg.ModBlocks;
import com.ordana.spelunkery.reg.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fml.ModList;

public class SpelunkeryCompat {
    public static final Block rockSalt;

    public static boolean checkDimensionalTears(ItemStack stack, ItemStack stack2) {
        return stack.is(ModItems.PORTAL_FLUID_BOTTLE.get()) && stack2.is(ModItems.PORTAL_FLUID_BOTTLE.get());
    }

    public static Item getDepthGauge() {
        return ModItems.DEPTH_GAUGE.get();
    }

    static {
        rockSalt = ModList.get().isLoaded("spelunkery") ? ModBlocks.ROCK_SALT_BLOCK.get() : Blocks.DRIPSTONE_BLOCK;
    }
}
