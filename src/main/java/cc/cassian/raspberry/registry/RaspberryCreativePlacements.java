package cc.cassian.raspberry.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;

public class RaspberryCreativePlacements {
    public static void set(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ModCreativeTabs.TAB_FARMERS_DELIGHT.getKey())) {
            event.accept(RaspberryBlocks.ASH_STOVE.getBlock());
            event.accept(RaspberryBlocks.SILT_STOVE.getBlock());
        }
        else if (event.getTabKey().equals(CreativeModeTabs.NATURAL_BLOCKS)) {
            event.accept(RaspberryBlocks.WORMY_DIRT.getBlock());
            event.accept(RaspberryBlocks.CLOVERS.getBlock());
            event.accept(RaspberryBlocks.CHEERFUL_WILDFLOWERS.getBlock());
            event.accept(RaspberryBlocks.MOODY_WILDFLOWERS.getBlock());
            event.accept(RaspberryBlocks.PINK_PETALS.getBlock());
        }
        else if (event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
            event.accept(RaspberryBlocks.ASH_BLOCK.getBlock());
            event.accept(RaspberryBlocks.LEAD_GRATE.getBlock());
            event.accept(RaspberryBlocks.BLACKSTONE_GRAVEL.getBlock());
            event.accept(RaspberryBlocks.DEEPSLATE_GRAVEL.getBlock());
            event.accept(RaspberryBlocks.RAKED_BLACKSTONE_GRAVEL.getBlock());
            event.accept(RaspberryBlocks.RAKED_DEEPSLATE_GRAVEL.getBlock());
        }
        else if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            event.accept(RaspberryItems.ASHBALL);
        }

    }
}
