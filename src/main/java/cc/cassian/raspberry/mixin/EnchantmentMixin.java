package cc.cassian.raspberry.mixin;

import cc.cassian.raspberry.registry.RaspberryTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
    private void mixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var enchantment = (Enchantment) (Object) this;
        if (enchantment.category.equals(EnchantmentCategory.WEAPON) && stack.is(RaspberryTags.ENCHANTABLE_WEAPON))
            cir.setReturnValue(true);
    }
}
