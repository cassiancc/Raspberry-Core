package cc.cassian.raspberry.mixin.architects_palette;

import architectspalette.content.worldgen.features.TwistedTree;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Mixin(TwistedTree.class)
public abstract class TwistedTreeMixin extends AbstractTreeGrower {

    /**
     * @author evanbones
     * @reason Load configured feature from registry instead of hardcoding it
     */
    @Override
    public boolean growTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random) {
        
        ResourceLocation featureId = new ResourceLocation("architects_palette", "twisted_tree");
        ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, featureId);

        Optional<Holder<ConfiguredFeature<?, ?>>> featureHolder = level.registryAccess()
            .registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY)
            .getHolder(featureKey);

        if (featureHolder.isEmpty()) {
            return false;
        }

        ConfiguredFeature<?, ?> configuredFeature = featureHolder.get().value();

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);

        if (configuredFeature.place(level, generator, random, pos)) {
            return true;
        } else {
            level.setBlock(pos, state, 4);
            return false;
        }
    }
}