package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {
    @Inject(
            method = "getStateForPlacement",
            at = @At(value = "RETURN"),
            cancellable = true)
    private void startCampfiresLit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (!ModConfig.get().campfiresStartLit && state != null)
            cir.setReturnValue(state.setValue(CampfireBlock.LIT, false));
    }
}
