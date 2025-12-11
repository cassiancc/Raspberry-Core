package cc.cassian.raspberry.mixin.emi;

import cc.cassian.raspberry.RaspberryMod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiRecipeManager;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.screen.RecipeScreen;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RecipeScreen.class)
public class RecipeScreenMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/recipe/EmiRecipeManager;getWorkstations(Ldev/emi/emi/api/recipe/EmiRecipeCategory;)Ljava/util/List;"), remap = false)
    private static List<EmiIngredient> hideFuelFromRender(EmiRecipeManager instance, EmiRecipeCategory emiRecipeCategory, Operation<List<EmiIngredient>> original) {
       if (emiRecipeCategory != VanillaEmiRecipeCategories.FUEL) {
           return original.call(instance, emiRecipeCategory);
       }
       return List.of();
    }

    @WrapOperation(method = "setPage", at = @At(value = "INVOKE", target = "Ldev/emi/emi/api/recipe/EmiRecipeManager;getWorkstations(Ldev/emi/emi/api/recipe/EmiRecipeCategory;)Ljava/util/List;"), remap = false)
    private static List<EmiIngredient> hideFuelFromNewPage(EmiRecipeManager instance, EmiRecipeCategory emiRecipeCategory, Operation<List<EmiIngredient>> original) {
        if (emiRecipeCategory != VanillaEmiRecipeCategories.FUEL) {
            return original.call(instance, emiRecipeCategory);
        }
        return List.of();
    }
}
