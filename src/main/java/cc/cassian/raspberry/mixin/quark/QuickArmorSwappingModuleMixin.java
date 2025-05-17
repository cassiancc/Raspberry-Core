package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.compat.GlidersCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.management.module.QuickArmorSwappingModule;

@Mixin(QuickArmorSwappingModule.class)
public class QuickArmorSwappingModuleMixin {
    @WrapOperation(method = "swapSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;hasBindingCurse(Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean doNotTakeGliders(ItemStack stack, Operation<Boolean> original) {
        if (GlidersCompat.isGlider(stack)) {
            return true;
        }
        return original.call(stack);
    }
}
