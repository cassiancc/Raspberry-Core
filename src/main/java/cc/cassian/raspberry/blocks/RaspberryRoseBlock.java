package cc.cassian.raspberry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RaspberryRoseBlock extends FlowerBlock implements BonemealableBlock {
    private final Supplier<Block> tallFlowerSupplier;

    public RaspberryRoseBlock(Supplier<Block> tallFlowerSupplier, Supplier<MobEffect> stewEffect, int duration, Properties properties) {
        super(stewEffect, duration, properties);
        this.tallFlowerSupplier = tallFlowerSupplier;
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return random.nextInt(2) == 0;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        Block tallFlower = this.tallFlowerSupplier.get();
        if (tallFlower instanceof DoublePlantBlock) {
            if (tallFlower.defaultBlockState().canSurvive(level, pos) && level.isEmptyBlock(pos.above())) {
                DoublePlantBlock.placeAt(level, tallFlower.defaultBlockState(), pos, 2);
            }
        }
    }
}