package cc.cassian.raspberry.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.StoveBlock;

import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static RegistryObject<Block> SILT_STOVE = registerBlock("silt_stove", ()-> new StoveBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)), FarmersDelight.CREATIVE_TAB);
    public static RegistryObject<Block> ASH_STOVE = registerBlock("ash_stove", ()-> new StoveBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)), FarmersDelight.CREATIVE_TAB);


    public static RegistryObject<Block> registerBlock(String blockID, Supplier<Block> blockSupplier, CreativeModeTab tab) {
        final var block = BLOCKS.register(blockID, blockSupplier);
        final var item = ModItems.ITEMS.register(blockID, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
        return block;

    }
}
