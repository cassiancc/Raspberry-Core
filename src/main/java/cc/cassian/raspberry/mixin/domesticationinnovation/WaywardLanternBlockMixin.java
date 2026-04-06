package cc.cassian.raspberry.mixin.domesticationinnovation;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import com.github.alexthe668.domesticationinnovation.server.block.WaywardLanternBlock;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(WaywardLanternBlock.class)
public class WaywardLanternBlockMixin {
    @Inject(method = "getShape", at = @At(value = "RETURN"), cancellable = true)
    private void getShape(BlockState p_49038_, BlockGetter p_49039_, BlockPos p_49040_, CollisionContext p_49041_, CallbackInfoReturnable<VoxelShape> cir) {
        cir.setReturnValue(Stream.of(
                Block.box(7, 12, 7, 9, 16, 9),
                Block.box(6, 11, 6, 10, 12, 10),
                Block.box(2, 9, 2, 14, 11, 14),
                Block.box(4, 2, 4, 12, 9, 12),
                Block.box(6, 0, 6, 10, 2, 10)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
    }

    @Inject(method = "animateTick", at = @At(value = "HEAD"), cancellable = true)
    private void noFlameParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        ci.cancel();
    }

}
