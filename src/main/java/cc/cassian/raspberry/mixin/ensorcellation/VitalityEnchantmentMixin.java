package cc.cassian.raspberry.mixin.ensorcellation;

import cc.cassian.raspberry.registry.RaspberryTags;
import cofh.ensorcellation.enchantment.VitalityEnchantment;
import cofh.lib.enchantment.EnchantmentCoFH;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentCoFH.class)
public class VitalityEnchantmentMixin {
    @Inject(method = "canApplyAtEnchantingTable", remap = false, at = @At(value = "HEAD"), cancellable = true)
    private void mixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var enchantment = (EnchantmentCoFH) (Object) this;
        if (enchantment instanceof VitalityEnchantment && stack.is(RaspberryTags.ENCHANTABLE_VITALITY))
            cir.setReturnValue(true);
    }
}
