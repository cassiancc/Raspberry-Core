package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(CreativeModeTabs.class)
public class CreativeModeTabsMixin {

    @Inject(method = "generatePotionEffectTypes", at = @At("HEAD"), cancellable = true)
    private static void raspberry$filterPotions(CreativeModeTab.Output output, HolderLookup<Potion> holders, Item item, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (ModConfig.get().hiddenPotions.isEmpty()) {
            ModConfig.load();
        }

        ci.cancel();

        for (Potion potion : BuiltInRegistries.POTION) {
            if (potion != Potions.EMPTY) {
                ResourceLocation id = BuiltInRegistries.POTION.getKey(potion);

                if (ModConfig.get().hiddenPotions.contains(id.toString())) {
                    continue;
                }

                output.accept(PotionUtils.setPotion(new ItemStack(item), potion), visibility);
            }
        }
    }

    @Inject(method = "generateEnchantmentBookTypesOnlyMaxLevel", at = @At("HEAD"), cancellable = true)
    private static void raspberry$filterEnchantmentsMaxLevel(CreativeModeTab.Output output, HolderLookup<Enchantment> holders, Set<EnchantmentCategory> categories, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        ci.cancel();

        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);

            if (id != null && ModConfig.get().hiddenEnchantments.contains(id.toString())) {
                continue;
            }

            if (categories.contains(enchantment.category)) {
                output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel())), visibility);
            }
        }
    }

    @Inject(method = "generateEnchantmentBookTypesAllLevels", at = @At("HEAD"), cancellable = true)
    private static void raspberry$filterEnchantmentsAllLevels(CreativeModeTab.Output output, HolderLookup<Enchantment> holders, Set<EnchantmentCategory> categories, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        ci.cancel();

        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);

            if (id != null && ModConfig.get().hiddenEnchantments.contains(id.toString())) {
                continue;
            }

            if (categories.contains(enchantment.category)) {
                for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); ++i) {
                    output.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i)), visibility);
                }
            }
        }
    }
}