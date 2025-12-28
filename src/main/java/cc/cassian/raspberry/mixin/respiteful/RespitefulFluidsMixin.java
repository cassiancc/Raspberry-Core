package cc.cassian.raspberry.mixin.respiteful;

import com.tterrag.registrate.builders.FluidBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import plus.dragons.respiteful.entries.RespitefulFluids;

@Mixin(RespitefulFluids.class)
public class RespitefulFluidsMixin {

    @Redirect(method = "tea", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/builders/FluidBuilder;noBucket()Lcom/tterrag/registrate/builders/FluidBuilder;", remap = false), remap = false)
    private static FluidBuilder<?, ?> preventNoBucket(FluidBuilder<?, ?> instance) {
        return instance;
    }

    @Redirect(method = "tea", at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/builders/FluidBuilder;noBlock()Lcom/tterrag/registrate/builders/FluidBuilder;", remap = false), remap = false)
    private static FluidBuilder<?, ?> preventNoBlock(FluidBuilder<?, ?> instance) {
        return instance;
    }
}