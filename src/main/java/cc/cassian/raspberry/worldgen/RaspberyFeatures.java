package cc.cassian.raspberry.worldgen;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
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

public class RaspberyFeatures {

    public static final SimpleWeightedRandomList.Builder<?> list = SimpleWeightedRandomList.builder().add(Blocks.FLOWERING_AZALEA.defaultBlockState(), 4).add(Blocks.AZALEA.defaultBlockState(), 7).add(RaspberryBlocks.RED_MOSS_CARPET.getA().get().defaultBlockState(), 25).add(Blocks.GRASS.defaultBlockState(), 50).add(Blocks.TALL_GRASS.defaultBlockState(), 10);

    public static final Holder<ConfiguredFeature<SimpleBlockConfiguration, ?>> RED_MOSS_VEGETATION = FeatureUtils.register("red_moss_vegetation", Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider((SimpleWeightedRandomList.Builder<BlockState>) list)));

    public static final Holder<ConfiguredFeature<VegetationPatchConfiguration, ?>> RED_MOSS_PATCH_BONEMEAL = register("red_moss_patch_bonemeal", Feature.VEGETATION_PATCH, new VegetationPatchConfiguration(BlockTags.MOSS_REPLACEABLE, BlockStateProvider.simple(RaspberryBlocks.RED_MOSS.getA().get()), PlacementUtils.inlinePlaced(RED_MOSS_VEGETATION), CaveSurface.FLOOR, ConstantInt.of(1), 0.0F, 5, 0.6F, UniformInt.of(1, 2), 0.75F));


    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, ?>> register(String name, F feature, FC config) {
        return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, "raspberry:"+ name, new ConfiguredFeature(feature, config));
    }

    public static void touch() {

    }
}
