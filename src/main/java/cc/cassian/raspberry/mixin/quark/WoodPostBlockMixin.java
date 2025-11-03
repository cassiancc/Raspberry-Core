package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.ModHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.building.block.WoodPostBlock;

import static vazkii.quark.content.building.block.WoodPostBlock.CHAINED;

@Pseudo
@Mixin(WoodPostBlock.class)
public class WoodPostBlockMixin {
    @Inject(method = "getState", at = @At(value = "RETURN"), remap = false, cancellable = true)
    private void moreThingsCanHang(Level world, BlockPos pos, Direction.Axis axis, CallbackInfoReturnable<BlockState> cir) {
        if(axis != Direction.Axis.Y) {
            BlockState downState = world.getBlockState(pos.relative(Direction.DOWN));
            BooleanProperty chainDownProp = CHAINED[Direction.DOWN.ordinal()];

            if (ModHelpers.shouldWoodPostChainConnect(downState)) {
                cir.setReturnValue(cir.getReturnValue().setValue(chainDownProp, true));
            }
        }

    }
}
