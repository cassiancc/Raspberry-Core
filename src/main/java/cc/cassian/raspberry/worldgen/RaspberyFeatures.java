package cc.cassian.raspberry.worldgen;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import static cc.cassian.raspberry.RaspberryMod.MOD_ID;

public class RaspberyFeatures {


    public static final ResourceKey<ConfiguredFeature<?, ?>> RED_MOSS_PATCH_BONEMEAL = register("red_moss_patch_bonemeal");

    public static ResourceKey<ConfiguredFeature<?,?>> register(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,  new ResourceLocation(MOD_ID, name));
    }

    public static void touch() {

    }
}
