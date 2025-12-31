package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.google.common.collect.ImmutableMultimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import cc.cassian.raspberry.compat.NaturalistCompat;
import cc.cassian.raspberry.compat.SpelunkeryCompat;
import com.starfish_studios.naturalist.Naturalist;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isSameItemSameTags", at = @At(value = "RETURN"), cancellable = true)
    private static void mixin(ItemStack arg, ItemStack arg2, CallbackInfoReturnable<Boolean> cir) {
        // TODO causes adding item twice crash on 1.20
//        if (!arg.hasTag() && !arg2.hasTag()) {
//            cir.setReturnValue(true);
//        } else
        if (ModCompat.hasSpelunkery()) {
            if (SpelunkeryCompat.checkDimensionalTears(arg, arg2))
                cir.setReturnValue(true);
        }
//        if (ModCompat.NATURALIST) {
//            if (NaturalistCompat.match(arg, arg2)) {
//                cir.setReturnValue(true);
//            }
//        }

    }

    @Inject(method = "enchant", at = @At("HEAD"), cancellable = true)
    private void enchant(Enchantment enchantment, int level, CallbackInfo info) {
        String id = Objects.requireNonNull(ForgeRegistries.ENCHANTMENTS.getKey(enchantment)).toString();
        if (ModConfig.get().hiddenEnchantments.contains("*") || ModConfig.get().hiddenEnchantments.contains(id)) {
            info.cancel();
        }
    }

    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAttributeModifiers(Lnet/minecraft/world/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
    private Multimap<Attribute, AttributeModifier> raspberry$hideAttributeTooltips(ItemStack instance, EquipmentSlot slot) {
        String id = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(instance.getItem())).toString();

        if (ModConfig.get().hiddenTooltipItems.contains(id)) {
            return ImmutableMultimap.of();
        }

        return instance.getAttributeModifiers(slot);
    }

}
