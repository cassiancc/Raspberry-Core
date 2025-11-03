package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.registry.RaspberryBlocks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpreadingSnowyDirtBlock.class)
public class SpreadingSnowyDirtBlockMixin {
    @WrapOperation(method = "canBeGrass", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean fakeSnow(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || instance.is(RaspberryBlocks.SNOW_LAYER.getBlock());
    }

    @WrapOperation(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 1))
    private boolean fakeSnowTick(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || instance.is(RaspberryBlocks.SNOW_LAYER.getBlock());
    }
}
