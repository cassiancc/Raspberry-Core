package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SmithingTransformRecipe.class)
public abstract class NoTemplatesMixin {

    @WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/Ingredient;test(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0))
    private static boolean noTemplates(Ingredient instance, ItemStack stack, Operation<Boolean> original) {
        if (ModConfig.get().noTemplates) {
            return true;
        } else {
            return original.call(instance, stack);
        }
    }

}