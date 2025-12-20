package cc.cassian.raspberry.registry;

import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vectorwing.farmersdelight.common.registry.ModCreativeTabs;

import java.util.Iterator;
import java.util.Map;

public class RaspberryCreativePlacements {
    public static void set(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.SEARCH) ||
                event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES) ||
                event.getTabKey().equals(CreativeModeTabs.COMBAT) ||
                event.getTabKey().equals(CreativeModeTabs.FOOD_AND_DRINKS) ||
                event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {

            Iterator<Map.Entry<ItemStack, CreativeModeTab.TabVisibility>> iterator = event.getEntries().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemStack, CreativeModeTab.TabVisibility> entry = iterator.next();
                ItemStack stack = entry.getKey();
                boolean shouldRemove = false;

                if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof SplashPotionItem ||
                        stack.getItem() instanceof LingeringPotionItem || stack.getItem() instanceof TippedArrowItem) {

                    ResourceLocation potionId = BuiltInRegistries.POTION.getKey(PotionUtils.getPotion(stack));
                    if (ModConfig.get().hiddenPotions.contains(potionId.toString())) {
                        shouldRemove = true;
                    }
                }

                if (!shouldRemove && stack.is(Items.ENCHANTED_BOOK)) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                    for (Enchantment enchantment : enchantments.keySet()) {
                        ResourceLocation enchId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment);
                        if (enchId != null && (ModConfig.get().hiddenEnchantments.contains("*") ||
                                ModConfig.get().hiddenEnchantments.contains(enchId.toString()))) {
                            shouldRemove = true;
                            break;
                        }
                    }
                }

                if (shouldRemove) {
                    iterator.remove();
                }
            }
        }

        if (event.getTabKey().equals(ModCreativeTabs.TAB_FARMERS_DELIGHT.getKey())) {
            event.accept(RaspberryBlocks.ASH_STOVE.getBlock());
            event.accept(RaspberryBlocks.SILT_STOVE.getBlock());
        }
        else if (event.getTabKey().equals(CreativeModeTabs.NATURAL_BLOCKS)) {
            event.accept(RaspberryBlocks.WORMY_DIRT.getBlock());
            event.accept(RaspberryBlocks.CLOVERS.getBlock());
            event.accept(RaspberryBlocks.CHEERFUL_WILDFLOWERS.getBlock());
            event.accept(RaspberryBlocks.MOODY_WILDFLOWERS.getBlock());
            event.accept(RaspberryBlocks.PINK_PETALS.getBlock());
        }
        else if (event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
            event.accept(RaspberryBlocks.ASH_BLOCK.getBlock());
            event.accept(RaspberryBlocks.LEAD_GRATE.getBlock());
            event.accept(RaspberryBlocks.BLACKSTONE_GRAVEL.getBlock());
            event.accept(RaspberryBlocks.DEEPSLATE_GRAVEL.getBlock());
            event.accept(RaspberryBlocks.RAKED_BLACKSTONE_GRAVEL.getBlock());
            event.accept(RaspberryBlocks.RAKED_DEEPSLATE_GRAVEL.getBlock());
        }
        else if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
            event.accept(RaspberryItems.ASHBALL);
        }
    }
}