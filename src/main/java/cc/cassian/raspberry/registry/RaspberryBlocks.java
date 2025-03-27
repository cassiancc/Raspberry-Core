package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.blocks.RaspberryGravelBlock;
import cc.cassian.raspberry.compat.CopperBackportCompat;
import cc.cassian.raspberry.compat.EnvironmentalCompat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;
import xanthian.copperandtuffbackport.blocks.custom.GrateBlock;

import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            SILT_STOVE = registerBlock("silt_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())));

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            ASH_STOVE = registerBlock("ash_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())));

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            LEAD_GRATE = registerLeadGrate();

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            WORMY_DIRT = registerBlock("wormy_dirt",
            ()-> new Block(getTruffleProperties()));

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            RAKED_BLACKSTONE_GRAVEL = registerBlock("raked_blackstone_gravel",
            ()-> new RakedGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            RAKED_DEEPSLATE_GRAVEL = registerBlock("raked_deepslate_gravel",
            ()-> new RakedGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BLACKSTONE_GRAVEL = registerBlock("blackstone_gravel",
            ()-> new RaspberryGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 986379), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            DEEPSLATE_GRAVEL = registerBlock("deepslate_gravel",
            ()-> new RaspberryGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 2039584), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static BlockBehaviour.Properties getTruffleProperties() {
        if (ModCompat.ENVIRONMENTAL)
            return EnvironmentalCompat.getTruffleProperties();
        else return BlockBehaviour.Properties.copy(Blocks.DIRT);
    }

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>> registerLeadGrate() {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of(Material.HEAVY_METAL).noOcclusion().strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL);
        if (ModCompat.COPPER_BACKPORT) {
            return CopperBackportCompat.registerGrateBlock(properties);
        }
        else return registerBlock("lead_grate",
                ()-> new Block(properties));
    }

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>> registerBlock(String blockID, Supplier<Block> blockSupplier) {
        final var block = BLOCKS.register(blockID, blockSupplier);
        final var item = RaspberryItems.ITEMS.register(blockID, () -> new BlockItem(block.get(), new Item.Properties()));
        return new Pair<>(block, item);
    }

    /**
	 * Get a block from the registry
	 */
    public static @NotNull Block getBlock(Pair<RegistryObject<Block>, RegistryObject<BlockItem>> block) {
        return block.getA().get();
    }

    /**
     * Get a blockitem from the registry
     */
    public static @NotNull Item getItem(Pair<RegistryObject<Block>, RegistryObject<BlockItem>> block) {
        return block.getB().get();
    }
}
