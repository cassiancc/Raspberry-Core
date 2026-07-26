package cc.cassian.raspberry.mixin.spelunkery;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.events.DripstoneEvent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static cc.cassian.raspberry.events.DripstoneEvent.*;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/PointedDripstoneBlock;findTip(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;IZ)Lnet/minecraft/core/BlockPos;"))
    private static void soSalty(BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo ci, @Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 0) BlockPos blockPos) {
        if (ModCompat.SPELUNKERY && ModConfig.get().saltGenerator) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, SALT);
        }
        if (ModCompat.QUARK && ModConfig.get().corundumGenerators) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, DIAMOND);
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, AMETHYST);
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, QUARTZ);
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, REDSTONE);
        }
    }
}