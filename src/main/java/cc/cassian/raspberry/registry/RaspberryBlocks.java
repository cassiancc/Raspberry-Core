package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.blocks.*;
import cc.cassian.raspberry.compat.CopperBackportCompat;
import cc.cassian.raspberry.compat.EnvironmentalCompat;
import net.mehvahdjukaar.supplementaries.common.block.blocks.AshLayerBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RakedGravelBlock;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.ArrayList;
import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            SILT_STOVE = registerBlock("silt_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())), FarmersDelight.CREATIVE_TAB);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            ASH_STOVE = registerBlock("ash_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())), FarmersDelight.CREATIVE_TAB);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            LEAD_GRATE = registerLeadGrate();

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            WORMY_DIRT = registerBlock("wormy_dirt",
            ()-> new Block(getTruffleProperties()), CreativeModeTab.TAB_BUILDING_BLOCKS);

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

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CHEERFUL_WILDFLOWERS = registerBlock("cheery_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties(false)), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            PINK_PETALS = registerBlock("pink_petals",
            ()-> new FlowerBedBlock(flowerBedProperties(false)), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CLOVERS = registerBlock("clovers",
            ()-> new FlowerBedBlock(flowerBedProperties(true)), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            MOODY_WILDFLOWERS = registerBlock("moody_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties(false)), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            HOPEFUL_WILDFLOWERS = registerBlock("hopeful_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties(false)), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CAKE = registerBlock("cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "farmersdelight:cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CHOCOLATE_CAKE = registerBlock("chocolate_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:chocolate_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            STRAWBERRY_CAKE = registerBlock("strawberry_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:strawberry_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BANANA_CAKE = registerBlock("banana_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:banana_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            MINT_CAKE = registerBlock("mint_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:mint_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            VANILLA_CAKE = registerBlock("vanilla_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:vanilla_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            CHERRY_CAKE = registerBlock("cherry_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:adzuki_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            MAGMA_CAKE = registerBlock("magma_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "mynethersdelight:magma_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            GREEN_TEA_CAKE = registerBlock("green_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "respiteful:green_tea_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            YELLOW_TEA_CAKE = registerBlock("yellow_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "respiteful:yellow_tea_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BLACK_TEA_CAKE = registerBlock("black_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "respiteful:black_tea_cake_slice"), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            COFFEE_CAKE = registerBlock("coffee_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "farmersrespite:coffee_cake_slice"), CreativeModeTab.TAB_DECORATIONS);
            
    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            SNOW_LAYER = registerBlock("snow",
            ()-> new GravitationalSnowLayerBlock(BlockBehaviour.Properties.copy(Blocks.SNOW)), CreativeModeTab.TAB_DECORATIONS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            ASH_BLOCK = registerBlock("ash_block",
            ()-> new AshBlock(BlockBehaviour.Properties.copy(ModRegistry.ASH_BLOCK.get())), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            RED_MOSS = registerBlock("red_moss_block",
            ()-> new RedMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK).color(MaterialColor.COLOR_RED)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            BRICK_COUNTER = registerBlock("brick_counter",
            ()-> new BrickCounterBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            SILT_BRICK_COUNTER = registerBlock("silt_brick_counter",
            ()-> new BrickCounterBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            ASH_BRICK_COUNTER = registerBlock("ash_brick_counter",
            ()-> new BrickCounterBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>>
            RED_MOSS_CARPET = registerBlock("red_moss_carpet",
            ()-> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK).color(MaterialColor.COLOR_RED)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    private static BlockBehaviour.Properties flowerBedProperties(boolean replaceable) {
        var material = Material.PLANT;
        if (replaceable) material = Material.REPLACEABLE_PLANT;
        return BlockBehaviour.Properties.of(material).noCollission().sound(SoundType.FLOWERING_AZALEA);
    }

    public static final ArrayList<Pair<RegistryObject<Block>, RegistryObject<BlockItem>>> FOLIAGE_BLOCKS = new ArrayList<Pair<RegistryObject<Block>, RegistryObject<BlockItem>>>();

    public static void register(IEventBus eventBus) {
        RaspberryBlocks.BLOCKS.register(eventBus);
        FOLIAGE_BLOCKS.add(CHEERFUL_WILDFLOWERS);
        FOLIAGE_BLOCKS.add(PINK_PETALS);
        FOLIAGE_BLOCKS.add(MOODY_WILDFLOWERS);
        FOLIAGE_BLOCKS.add(HOPEFUL_WILDFLOWERS);
    }

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
                ()-> new Block(properties), CreativeModeTab.TAB_BUILDING_BLOCKS);
    }

    public static Pair<RegistryObject<Block>, RegistryObject<BlockItem>> registerBlock(String blockID, Supplier<Block> blockSupplier, CreativeModeTab tab) {
        final var block = BLOCKS.register(blockID, blockSupplier);
        final var item = RaspberryItems.ITEMS.register(blockID, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
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
