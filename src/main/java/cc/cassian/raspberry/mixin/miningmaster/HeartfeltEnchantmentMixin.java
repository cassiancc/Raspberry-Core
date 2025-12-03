package cc.cassian.raspberry.mixin.miningmaster;

import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.ItemStack;
import org.infernalstudios.miningmaster.enchantments.HeartfeltEnchantment;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HeartfeltEnchantment.class)
public class HeartfeltEnchantmentMixin {
    @WrapMethod(method = "canEnchant")
    private boolean disableDoubleJumpInWater(ItemStack stack, Operation<Boolean> original) {
       if (stack.is(RaspberryTags.ENCHANTABLE_HEARTFELT)) return true;
       return original.call(stack);
    }
}