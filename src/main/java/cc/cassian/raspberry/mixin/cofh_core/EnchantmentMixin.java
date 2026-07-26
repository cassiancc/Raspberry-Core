package cc.cassian.raspberry.mixin.cofh_core;

import cc.cassian.raspberry.registry.RaspberryTags;
import cofh.ensorcellation.enchantment.TrueshotEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.TridentImpalerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
    private void mixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var enchantment = (Enchantment) (Object) this;
        if (enchantment instanceof TrueshotEnchantment && stack.is(RaspberryTags.ENCHANTABLE_TRUESHOT))
            cir.setReturnValue(true);
    }
}
