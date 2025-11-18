package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.blocks.*;
import cc.cassian.raspberry.compat.CopperBackportCompat;
import cc.cassian.raspberry.compat.EnvironmentalCompat;
import net.mehvahdjukaar.supplementaries.common.block.blocks.AshLayerBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.PancakeBlock;
import net.mehvahdjukaar.supplementaries.common.block.blocks.RakedGravelBlock;
import net.mehvahdjukaar.supplementaries.reg.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberryBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final Map<ResourceLocation, RegistryObject<Block>> POTTED_PLANTS = new HashMap<>();

    public static final Supplier<Block> TEMPORARY_COBWEB = registerBlock("temporary_cobweb",
            ()-> new TemporaryCobwebBlock(BlockBehaviour.Properties.copy(Blocks.COBWEB)));

    public static BlockSupplier
            SILT_STOVE = registerBlock("silt_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())));

    public static BlockSupplier
            ASH_STOVE = registerBlock("ash_stove",
            ()-> new StoveBlock(BlockBehaviour.Properties.copy(ModBlocks.STOVE.get())));

    public static BlockSupplier
            LEAD_GRATE = registerLeadGrate();

    public static BlockSupplier
            WORMY_DIRT = registerBlock("wormy_dirt",
            ()-> new Block(getTruffleProperties()));

    public static BlockSupplier
            RAKED_BLACKSTONE_GRAVEL = registerBlock("raked_blackstone_gravel",
            ()-> new RakedGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)));

    public static BlockSupplier
            RAKED_DEEPSLATE_GRAVEL = registerBlock("raked_deepslate_gravel",
            ()-> new RakedGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)));

    public static BlockSupplier
            BLACKSTONE_GRAVEL = registerBlock("blackstone_gravel",
            ()-> new RaspberryGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 986379));

    public static BlockSupplier
            DEEPSLATE_GRAVEL = registerBlock("deepslate_gravel",
            ()-> new RaspberryGravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL), 2039584));

    public static BlockSupplier
            CHEERFUL_WILDFLOWERS = registerBlock("cheery_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties(false)));

    public static BlockSupplier
            PINK_PETALS = registerBlock("pink_petals",
            ()-> new FlowerBedBlock(flowerBedProperties(false)));

    public static BlockSupplier
            CLOVERS = registerBlock("clovers",
            ()-> new FlowerBedBlock(flowerBedProperties(true)));

    public static BlockSupplier
            MOODY_WILDFLOWERS = registerBlock("moody_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties(false)));

    public static BlockSupplier
            HOPEFUL_WILDFLOWERS = registerBlock("hopeful_wildflowers",
            ()-> new FlowerBedBlock(flowerBedProperties(false)));

    public static final RegistryObject<Block> POTTED_CHEERY_WILDFLOWERS = registerPottedPlant(CHEERFUL_WILDFLOWERS);
    public static final RegistryObject<Block> POTTED_HOPEFUL_WILDFLOWERS = registerPottedPlant(HOPEFUL_WILDFLOWERS);
    public static final RegistryObject<Block> POTTED_PLAYFUL_WILDFLOWERS = registerPottedPlant(PINK_PETALS);
    public static final RegistryObject<Block> POTTED_MOODY_WILDFLOWERS = registerPottedPlant(MOODY_WILDFLOWERS);
    public static final RegistryObject<Block> POTTED_CLOVERS = registerPottedPlant(CLOVERS);

    public static  RegistryObject<Block>
            CHEERY_WILDFLOWER_GARLAND = RaspberryBlocks.BLOCKS.register("cheery_wildflower_garland", ()-> new FlowerGarlandBlock(flowerBedProperties(false)));

    public static  RegistryObject<Block>
            HOPEFUL_WILDFLOWER_GARLAND = RaspberryBlocks.BLOCKS.register("hopeful_wildflower_garland", ()-> new FlowerGarlandBlock(flowerBedProperties(false)));

    public static  RegistryObject<Block>
            PLAYFUL_WILDFLOWER_GARLAND = RaspberryBlocks.BLOCKS.register("playful_wildflower_garland", ()-> new FlowerGarlandBlock(flowerBedProperties(false)));

    public static  RegistryObject<Block>
            MOODY_WILDFLOWER_GARLAND = RaspberryBlocks.BLOCKS.register("moody_wildflower_garland", ()-> new FlowerGarlandBlock(flowerBedProperties(false)));

    public static BlockSupplier
            CAKE = registerBlock("cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "farmersdelight:cake_slice"));

    public static BlockSupplier
            CHOCOLATE_CAKE = registerBlock("chocolate_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:chocolate_cake_slice"));

    public static BlockSupplier
            STRAWBERRY_CAKE = registerBlock("strawberry_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:strawberry_cake_slice"));

    public static BlockSupplier
            BANANA_CAKE = registerBlock("banana_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:banana_cake_slice"));

    public static BlockSupplier
            MINT_CAKE = registerBlock("mint_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:mint_cake_slice"));

    public static BlockSupplier
            VANILLA_CAKE = registerBlock("vanilla_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:vanilla_cake_slice"));

    public static BlockSupplier
            CHERRY_CAKE = registerBlock("cherry_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "abnormals_delight:adzuki_cake_slice"));

    public static BlockSupplier
            MAGMA_CAKE = registerBlock("magma_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "mynethersdelight:magma_cake_slice"));

    public static BlockSupplier
            GREEN_TEA_CAKE = registerBlock("green_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "respiteful:green_tea_cake_slice"));

    public static BlockSupplier
            YELLOW_TEA_CAKE = registerBlock("yellow_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "respiteful:yellow_tea_cake_slice"));

    public static BlockSupplier
            BLACK_TEA_CAKE = registerBlock("black_tea_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "respiteful:black_tea_cake_slice"));

    public static BlockSupplier
            COFFEE_CAKE = registerBlock("coffee_cake",
            ()-> new RaspberryCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE), "farmersrespite:coffee_cake_slice"));
            
    public static BlockSupplier
            SNOW_LAYER = registerBlock("snow",
            ()-> new GravitationalSnowLayerBlock(BlockBehaviour.Properties.copy(Blocks.SNOW)));

    public static BlockSupplier
            ASH_BLOCK = registerBlock("ash_block",
            ()-> new AshBlock(BlockBehaviour.Properties.copy(ModRegistry.ASH_BLOCK.get())));

    public static BlockSupplier
            RED_MOSS = registerBlock("red_moss_block",
            ()-> new RedMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK).mapColor(MapColor.COLOR_RED)));

    public static BlockSupplier
            BRICK_COUNTER = registerBlock("brick_counter",
            ()-> new BrickCounterBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));

    public static BlockSupplier
            SILT_BRICK_COUNTER = registerBlock("silt_brick_counter",
            ()-> new BrickCounterBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));

    public static BlockSupplier
            ASH_BRICK_COUNTER = registerBlock("ash_brick_counter",
            ()-> new BrickCounterBlock(BlockBehaviour.Properties.copy(Blocks.BRICKS)));

    public static BlockSupplier
            RED_MOSS_CARPET = registerBlock("red_moss_carpet",
            ()-> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK).mapColor(MapColor.COLOR_RED)));

    public static Supplier<Block>
            LEMON_PANCAKE = registerBlock("lemon_pancake",
            ()-> new LemonPancakeBlock(BlockBehaviour.Properties.copy(ModRegistry.PANCAKE.get())));

    private static BlockBehaviour.Properties flowerBedProperties(boolean replaceable) {
        var material = BlockBehaviour.Properties.of();
        if (replaceable) material = material.replaceable();
        return material.noCollission().sound(SoundType.FLOWERING_AZALEA);
    }

    public static final ArrayList<BlockSupplier> FOLIAGE_BLOCKS = new ArrayList<>();

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

    public static BlockSupplier registerLeadGrate() {
        BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().noOcclusion().strength(5.0F, 6.0F).requiresCorrectToolForDrops().sound(SoundType.METAL);
        if (ModCompat.COPPER_BACKPORT) {
            return CopperBackportCompat.registerGrateBlock(properties);
        }
        else return registerBlock("lead_grate",
                ()-> new Block(properties));
    }

    public static BlockSupplier registerBlock(String blockID, Supplier<Block> blockSupplier) {
        final var block = BLOCKS.register(blockID, blockSupplier);
        final var item = RaspberryItems.ITEMS.register(blockID, () -> new BlockItem(block.get(), new Item.Properties()));
        return new BlockSupplier(blockID, block, item);
    }

    public static Supplier<Block> registerBlock(String blockID, Supplier<Block> blockSupplier) {
        return BLOCKS.register(blockID, blockSupplier);
    }


    public static void addPottedPlants() {
        RaspberryBlocks.POTTED_PLANTS.forEach(((FlowerPotBlock)Blocks.FLOWER_POT)::addPlant);
    }

    public static RegistryObject<Block> registerPottedPlant(BlockSupplier block) {
        RegistryObject<Block> pottedBlock = RaspberryBlocks.BLOCKS.register("potted_" + block.getID(), () -> new FlowerPotBlock(
            () -> (FlowerPotBlock) Blocks.FLOWER_POT,
            block.getBlockSupplier(),
            BlockBehaviour.Properties.copy(Blocks.FLOWER_POT)
        ));
        RaspberryBlocks.POTTED_PLANTS.put(block.getItemSupplier().getId(), pottedBlock);
        return pottedBlock;
    }
}
