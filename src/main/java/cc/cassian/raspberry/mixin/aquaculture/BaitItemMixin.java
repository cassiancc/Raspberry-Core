package cc.cassian.raspberry.mixin.aquaculture;

import com.teammetallurgy.aquaculture.item.BaitItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(BaitItem.class)
public class BaitItemMixin {
    @Inject(method = "canBeDepleted", at = @At("HEAD"), cancellable = true)
    public void cannotBeDepleted(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
