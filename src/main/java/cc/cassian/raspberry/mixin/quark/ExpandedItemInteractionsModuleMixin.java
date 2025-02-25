package cc.cassian.raspberry.mixin.quark;

import cc.cassian.raspberry.compat.GlidersCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import vazkii.quark.content.management.module.ExpandedItemInteractionsModule;

@Pseudo
@Mixin(ExpandedItemInteractionsModule.class)
public class ExpandedItemInteractionsModuleMixin {
    @WrapOperation(
            method = "armorOverride",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private static Item glidersAreElytrasBasically(ItemStack stack, Operation<Item> original) {
        if (ModList.get().isLoaded("vc_gliders") && GlidersCompat.isGlider(stack)) {
            return Items.ELYTRA;
        }
        else return original.call(stack);
    }
}
