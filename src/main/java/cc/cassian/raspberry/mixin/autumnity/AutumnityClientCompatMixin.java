package cc.cassian.raspberry.mixin.autumnity;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.teamabnormals.autumnity.core.other.AutumnityClientCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(AutumnityClientCompat.class)
public class AutumnityClientCompatMixin {
    @WrapMethod(method = "lambda$registerBlockColors$1", remap = false)
    private static int red(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIndex, Operation<Integer> original) {
        if (ModConfig.get().disableMapleLeafTinting) {
            return -1;
        }
        return original.call(state, world, pos, tintIndex);

    }
    @WrapMethod(method = "lambda$registerBlockColors$2", remap = false)
    private static int orange(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIndex, Operation<Integer> original) {
        if (ModConfig.get().disableMapleLeafTinting) {
            return -1;
        }
        return original.call(state, world, pos, tintIndex);
    }
    @WrapMethod(method = "lambda$registerBlockColors$3", remap = false)
    private static int yellow(BlockState state, BlockAndTintGetter world, BlockPos pos, int tintIndex, Operation<Integer> original) {
        if (ModConfig.get().disableMapleLeafTinting) {
            return -1;
        }
        return original.call(state, world, pos, tintIndex);
    }
}
