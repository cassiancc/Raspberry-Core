//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cc.cassian.raspberry.blocks;

import cc.cassian.raspberry.worldgen.RaspberyFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MossBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RedMossBlock extends MossBlock{
    public RedMossBlock(BlockBehaviour.Properties arg) {
        super(arg);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap((p_258973_) -> p_258973_.getHolder(RaspberyFeatures.RED_MOSS_PATCH_BONEMEAL)).ifPresent((p_255669_) -> p_255669_.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
    }
}
