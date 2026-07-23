package cc.cassian.raspberry.mixin.spelunkery;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.events.DripstoneEvent;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @Inject(method = "maybeTransferFluid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", shift = At.Shift.BEFORE))
    private static void soSalty(BlockState state, ServerLevel level, BlockPos pos, float randChance, CallbackInfo ci, @Local Optional<PointedDripstoneBlock.FluidInfo> optional, @Local Fluid fluid, @Local(ordinal = 0) BlockPos blockPos) {
        if (ModCompat.SPELUNKERY) {
            DripstoneEvent.convertBlockViaDripstone(level, pos, optional.orElseThrow(), fluid, SpelunkeryCompat.rockSalt, RaspberryTags.CONVERTS_TO_SALT, Fluids.WATER, SpelunkeryCompat.rockSalt);
        }
    }

}
