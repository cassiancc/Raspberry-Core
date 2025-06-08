package cc.cassian.raspberry.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;

public class RaspberryCreativePlacements {
    public static void set(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(ModCreativeTabs.TAB_FARMERS_DELIGHT.getKey())) {
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.ASH_STOVE));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.SILT_STOVE));
        }
        else if (event.getTabKey().equals(CreativeModeTabs.NATURAL_BLOCKS)) {
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.WORMY_DIRT));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.CLOVERS));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.CHEERFUL_WILDFLOWERS));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.MOODY_WILDFLOWERS));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.PINK_PETALS));
        }
        else if (event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.ASH_BLOCK));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.LEAD_GRATE));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.BLACKSTONE_GRAVEL));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.DEEPSLATE_GRAVEL));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.RAKED_BLACKSTONE_GRAVEL));
            event.accept(RaspberryBlocks.getItem(RaspberryBlocks.RAKED_DEEPSLATE_GRAVEL));
        }
        else if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            event.accept(RaspberryItems.ASHBALL);
        }

    }
}
