package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.tools.item.TrowelItem;

@Mixin(TrowelItem.class)
public class TrowelItemMixin {

    @Inject(method = "isValidTarget", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void mixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(RaspberryTags.TROWEL_BLACKLIST)) {
            cir.setReturnValue(false);
        }
    }
}
