package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.compat.CreateCompat;
import cc.cassian.raspberry.compat.GlidersCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.management.module.ExpandedItemInteractionsModule;

@Pseudo
@Mixin(ExpandedItemInteractionsModule.class)
public class ExpandedItemInteractionsModuleMixin {
    @WrapOperation(
            method = "armorOverride",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private static Item glidersAreElytrasBasically(ItemStack stack, Operation<Item> original) {
        if (ModCompat.hasGliders() && GlidersCompat.isGlider(stack)) {
            return Items.ELYTRA;
        }
        else return original.call(stack);
    }

    @WrapOperation(
            method = "armorOverride",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private static Item gogglesAreHelmetsBasically(ItemStack stack, Operation<Item> original) {
        if (ModCompat.hasCreate() && CreateCompat.isGoggles(stack)) {
            return Items.CHAINMAIL_HELMET;
        }
        else return original.call(stack);
    }
}
