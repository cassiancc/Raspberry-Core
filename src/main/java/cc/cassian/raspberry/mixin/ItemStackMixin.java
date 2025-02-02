package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.compat.SpelunkeryCompat;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "tagMatches", at = @At(value = "RETURN"), cancellable = true)
    private static void mixin(ItemStack arg, ItemStack arg2, CallbackInfoReturnable<Boolean> cir) {
        if (!arg.hasTag() && !arg2.hasTag()) {
            cir.setReturnValue(true);
        }
        else if (ModList.get().isLoaded("spelunkery")) {
            if (SpelunkeryCompat.checkDimensionalTears(arg, arg2))
                cir.setReturnValue(true);
        }

    }
}
