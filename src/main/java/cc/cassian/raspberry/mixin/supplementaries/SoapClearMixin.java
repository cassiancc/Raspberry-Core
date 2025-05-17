package cc.cassian.raspberry.mixin.supplementaries;

import net.mehvahdjukaar.supplementaries.common.items.crafting.SoapClearRecipe;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoapClearRecipe.class)
public class SoapClearMixin {
    @Inject(
            method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "RETURN"),
            cancellable = true, remap = false)
    private void turnToCorrectGravel(CraftingContainer craftingContainer, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack returnValue = cir.getReturnValue();
        returnValue.setCount(1);
        cir.setReturnValue(returnValue);
    }
}
