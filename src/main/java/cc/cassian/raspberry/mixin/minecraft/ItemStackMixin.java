package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.NaturalistCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.starfish_studios.naturalist.Naturalist;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isSameItemSameTags", at = @At(value = "RETURN"), cancellable = true)
    private static void mixin(ItemStack arg, ItemStack arg2, CallbackInfoReturnable<Boolean> cir) {
        // TODO causes adding item twice crash on 1.20
//        if (!arg.hasTag() && !arg2.hasTag()) {
//            cir.setReturnValue(true);
//        } else
        if (ModCompat.SPELUNKERY) {
            if (SpelunkeryCompat.checkDimensionalTears(arg, arg2))
                cir.setReturnValue(true);
        }
        if (ModCompat.NATURALIST) {
            if (NaturalistCompat.match(arg, arg2)) {
                cir.setReturnValue(true);
            }
        }

    }
}
