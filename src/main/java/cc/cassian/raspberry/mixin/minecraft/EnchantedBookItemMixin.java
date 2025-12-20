package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {

    @Inject(method = "createForEnchantment", at = @At("HEAD"), cancellable = true)
    private static void raspberry$cancelHiddenEnchantments(EnchantmentInstance instance, CallbackInfoReturnable<ItemStack> cir) {
        if (instance != null) {
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(instance.enchantment);
            if (ModConfig.get().hiddenEnchantments.contains("*") || (id != null && ModConfig.get().hiddenEnchantments.contains(id.toString()))) {
                cir.setReturnValue(new ItemStack(Items.AIR));
            }
        }
    }

    @Inject(method = "addEnchantment", at = @At("HEAD"), cancellable = true)
    private static void raspberry$preventHiddenEnchantmentAddition(ItemStack stack, EnchantmentInstance instance, CallbackInfo ci) {
        ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(instance.enchantment);
        if (ModConfig.get().hiddenEnchantments.contains("*") || (id != null && ModConfig.get().hiddenEnchantments.contains(id.toString()))) {
            ci.cancel();
        }
    }
}