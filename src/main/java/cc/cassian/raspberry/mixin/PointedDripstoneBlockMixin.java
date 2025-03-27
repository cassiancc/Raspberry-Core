package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {
    @Shadow
    private static boolean canDripThrough(BlockGetter level, BlockPos pos, BlockState state) {
        return false;
    }

    @Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", shift = At.Shift.BEFORE))
    private static void soSalty(BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo ci, @Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 0) BlockPos blockPos) {
        if (ModList.get().isLoaded("spelunkery") &&
                level.getBlockState(optional.get().pos().below()).is(SpelunkeryCompat.rockSalt) && fluid == Fluids.WATER) {
            BlockPos blockPos2 = raspberryCore$findSaltable(level, blockPos);
            if (blockPos2 != null) {
                level.levelEvent(1504, blockPos, 0);
                BlockState blockState = SpelunkeryCompat.rockSalt.defaultBlockState();
                level.setBlockAndUpdate(blockPos2, blockState);
            }
        }
    }

    @Unique
    @Nullable
    private static BlockPos raspberryCore$findSaltable(Level level, BlockPos pos) {
        Predicate<BlockState> predicate = (arg2) -> {
            return arg2.is(RaspberryTags.CONVERTS_TO_SALT);
        };
        BiPredicate<BlockPos, BlockState> biPredicate = (arg2, arg3) -> {
            return canDripThrough(level, arg2, arg3) || arg3.is(Blocks.POINTED_DRIPSTONE);
        };

        return findBlockVertical(level, pos, Direction.DOWN.getAxisDirection(), biPredicate, predicate).orElse(null);
    }

    private static Optional<BlockPos> findBlockVertical(LevelAccessor level, BlockPos pos, Direction.AxisDirection axis,
                                                        BiPredicate<BlockPos, BlockState> positionalStatePredicate, Predicate<BlockState> statePredicate) {
        Direction direction = Direction.get(axis, Direction.Axis.Y);
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
        for (int i = 1; i < 11; i++) {
            mutableBlockPos.move(direction);
            BlockState blockState = level.getBlockState(mutableBlockPos);
            if (statePredicate.test(blockState)) {
                return Optional.of(mutableBlockPos.immutable());
            }
            if (!positionalStatePredicate.test(mutableBlockPos, blockState)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

}
