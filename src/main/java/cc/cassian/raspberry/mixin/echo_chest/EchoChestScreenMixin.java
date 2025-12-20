package cc.cassian.raspberry.mixin.echo_chest;

import fuzs.echochest.client.gui.screens.inventory.EchoChestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EchoChestScreen.class})
public class EchoChestScreenMixin {
    @Inject(
            method = {"render"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lfuzs/echochest/world/inventory/EchoChestMenu;getExperience()F"
            )},
            cancellable = true
    )
    private void injected(CallbackInfo ci) {
        ci.cancel();
    }
}
