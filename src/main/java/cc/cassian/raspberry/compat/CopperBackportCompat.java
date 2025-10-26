package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.registry.BlockSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import oshi.util.tuples.Pair;
import xanthian.copperandtuffbackport.blocks.custom.GrateBlock;

import static cc.cassian.raspberry.registry.RaspberryBlocks.registerBlock;

public class CopperBackportCompat {
    public static BlockSupplier registerGrateBlock(BlockBehaviour.Properties properties) {
        return registerBlock("lead_grate",
                ()-> new GrateBlock(properties), CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

}
