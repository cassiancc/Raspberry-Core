package cc.cassian.raspberry.mixin.emi;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.registry.EmiRecipes;
import dev.emi.emi.registry.EmiTags;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EmiRecipes.class)
public class EmiRecipesMixin {
    @WrapMethod(method = "addWorkstation", remap = false)
    private static void mixin(EmiRecipeCategory category, EmiIngredient workstation, Operation<Void> original) {
        if (workstation.getEmiStacks().stream().noneMatch(e->e.getItemStack().is(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), EmiTags.HIDDEN_FROM_RECIPE_VIEWERS)))) {
            original.call(category, workstation);
        }
    }
}
