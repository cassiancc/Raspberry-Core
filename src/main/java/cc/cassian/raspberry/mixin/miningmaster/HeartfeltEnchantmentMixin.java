package cc.cassian.raspberry.mixin.miningmaster;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.GlidersCompat;
import cc.cassian.raspberry.registry.RaspberryTags;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.infernalstudios.miningmaster.enchantments.HeartfeltEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeartfeltEnchantment.class)
public class HeartfeltEnchantmentMixin {
    @WrapMethod(method = "canEnchant")
    private boolean disableDoubleJumpInWater(ItemStack stack, Operation<Boolean> original) {
       if (stack.is(RaspberryTags.ENCHANTABLE_HEARTFELT)) return true;
       return original.call(stack);
    }

    @WrapOperation(method = "onItemAttributeModifierCalculate", remap = false, at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/ItemAttributeModifierEvent;getItemStack()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack glidersAreArmor(ItemAttributeModifierEvent instance, Operation<ItemStack> original) {
        var stack = original.call(instance);
        if (ModCompat.GLIDERS && GlidersCompat.isGlider(stack) && stack.is(RaspberryTags.ENCHANTABLE_HEARTFELT)) {
            ItemStack defaultInstance = Items.LEATHER_CHESTPLATE.getDefaultInstance();
            defaultInstance.setTag(stack.getTag());
            return defaultInstance;
        } else {
            return stack;
        }
    }
}