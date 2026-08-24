package cc.cassian.raspberry.mixin.cookscollection;

import com.baisylia.cookscollection.block.custom.OvenBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {
    @Inject(method = "isValid", at = @At(value = "RETURN"), cancellable = true)
    private void forceAllowOvens(BlockState arg, CallbackInfoReturnable<Boolean> cir) {
        if (arg.getBlock() instanceof OvenBlock)
            cir.setReturnValue(true);
    }
}
