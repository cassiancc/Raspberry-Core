package cc.cassian.raspberry.mixin.echo_chest;

import fuzs.echochest.world.inventory.EchoChestMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EchoChestMenu.class})
public class EchoChestMenuMixin {
    @Inject(
            method = {"validBottleItem"},
            at = {@At("HEAD")},
            cancellable = true,
            remap = false
    )
    private static void injected(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
