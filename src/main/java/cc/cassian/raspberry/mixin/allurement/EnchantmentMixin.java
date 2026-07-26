package cc.cassian.raspberry.mixin.allurement;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.teamabnormals.allurement.common.enchantment.LaunchEnchantment;
import de.cadentem.additional_enchantments.enchantments.HomingEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {
    @Inject(method = "canEnchant", at = @At(value = "HEAD"), cancellable = true)
    private void mixin(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        var enchantment = (Enchantment) (Object) this;
        if (enchantment instanceof LaunchEnchantment && stack.is(RaspberryTags.ENCHANTABLE_LAUNCH))
            cir.setReturnValue(true);
    }
}
