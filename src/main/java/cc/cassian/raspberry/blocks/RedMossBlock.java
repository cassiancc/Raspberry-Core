//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cc.cassian.raspberry.blocks;

import cc.cassian.raspberry.worldgen.RaspberyFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
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
    public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
        RaspberyFeatures.RED_MOSS_PATCH_BONEMEAL.value().place(world, world.getChunkSource().getGenerator(), random, pos.above());
    }
}
