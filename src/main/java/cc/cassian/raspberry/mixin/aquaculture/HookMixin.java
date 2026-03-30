package cc.cassian.raspberry.mixin.aquaculture;

import com.teammetallurgy.aquaculture.api.fishing.Hook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hook.class)
public abstract class HookMixin {
    @Shadow
    public abstract String getName();

    @Inject(method = "getDurabilityChance", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void adjustedDurabilityChance(CallbackInfoReturnable<Double> cir) {
        switch (this.getName()) {
            case "light", "note", "redstone":
                cir.setReturnValue(0.1);
                break;
            case "gold":
                cir.setReturnValue(0.15);
                break;
            case "double":
                cir.setReturnValue(0.25);
                break;
            case "heavy":
                cir.setReturnValue(0.4);
                break;
        }

    }
}
