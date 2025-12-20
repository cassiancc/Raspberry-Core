package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // Hides enchantments from the enchanting table UI and generated loot
    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"), cancellable = true)
    private static void getAvailableEnchantmentResults(int level, ItemStack stack, boolean treasure, CallbackInfoReturnable<List<EnchantmentInstance>> info) {
        if (ModConfig.get().hiddenEnchantments.contains("*")) {
            info.setReturnValue(Lists.newArrayList());
        } else {
            List<EnchantmentInstance> filtered = info.getReturnValue().stream()
                    .filter(instance -> !ModConfig.get().hiddenEnchantments.contains(
                            Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(instance.enchantment)).toString()))
                    .collect(Collectors.toList());
            info.setReturnValue(filtered);
        }
    }

    @Inject(method = "enchantItem", at = @At("RETURN"), cancellable = true)
    private static void enchantItem(net.minecraft.util.RandomSource random, ItemStack stack, int level, boolean treasure, CallbackInfoReturnable<ItemStack> info) {
        ItemStack result = info.getReturnValue();
        if (result.is(Items.ENCHANTED_BOOK) || !result.isEnchanted()) {
            info.setReturnValue(new ItemStack(Items.BOOK));
        }
    }
}