package cc.cassian.raspberry.compat;

import cc.cassian.raspberry.compat.emi.EmiFermentingRecipe;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegRecipe;
import umpaz.brewinandchewin.common.registry.BCItems;
import umpaz.brewinandchewin.common.registry.BCRecipeTypes;

public class BrewinAndChewinCompat {
    public static final EmiRecipeCategory FERMENTING = new EmiRecipeCategory(new ResourceLocation(BrewinAndChewin.MODID, "fermenting"), EmiStack.of(BCItems.KEG.get()));

    public static void registerEmi(EmiRegistry emiRegistry) {
        for (KegRecipe recipe : emiRegistry.getRecipeManager().getAllRecipesFor(BCRecipeTypes.FERMENTING.get())) {
            emiRegistry.addRecipe(new EmiFermentingRecipe(recipe.getId(), recipe.getGroup(), recipe.getRecipeBookTab(), recipe.getIngredients(), recipe.getFluidItem(), recipe.getResultItem(), recipe.getOutputContainer(), recipe.getExperience(), recipe.getFermentTime(), recipe.getTemperature()));
        }
        emiRegistry.addCategory(FERMENTING);
        emiRegistry.addWorkstation(FERMENTING, EmiStack.of(BCItems.KEG.get()));
    }
}
