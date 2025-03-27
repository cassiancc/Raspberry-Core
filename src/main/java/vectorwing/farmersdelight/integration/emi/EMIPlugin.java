/*
MIT License

Copyright (c) 2020 vectorwing

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package vectorwing.farmersdelight.integration.emi;

import cc.cassian.raspberry.registry.RaspberryBlocks;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.crafting.CuttingBoardRecipe;
import vectorwing.farmersdelight.common.registry.ModMenuTypes;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;
import vectorwing.farmersdelight.integration.emi.handler.CookingPotEmiRecipeHandler;
import vectorwing.farmersdelight.integration.emi.recipe.CookingPotEmiRecipe;
import vectorwing.farmersdelight.integration.emi.recipe.CuttingEmiRecipe;
import vectorwing.farmersdelight.integration.emi.recipe.DecompositionEmiRecipe;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(FDRecipeCategories.COOKING);
        registry.addCategory(FDRecipeCategories.CUTTING);
        registry.addCategory(FDRecipeCategories.DECOMPOSITION);

        registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING, EmiStack.of(RaspberryBlocks.ASH_STOVE.getB().get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING, EmiStack.of(RaspberryBlocks.SILT_STOVE.getB().get()));

        registry.addWorkstation(FDRecipeCategories.COOKING, FDRecipeWorkstations.COOKING_POT);
        registry.addWorkstation(FDRecipeCategories.CUTTING, FDRecipeWorkstations.CUTTING_BOARD);
        registry.addRecipeHandler(ModMenuTypes.COOKING_POT.get(), new CookingPotEmiRecipeHandler());

        for (CookingPotRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.COOKING.get())) {
            registry.addRecipe(new CookingPotEmiRecipe(recipe.getId(), recipe.getIngredients().stream().map(EmiIngredient::of).toList(),
                    EmiStack.of(recipe.getResultItem()), EmiStack.of(recipe.getOutputContainer()), recipe.getCookTime(), recipe.getExperience()));
        }

        for (CuttingBoardRecipe recipe : registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CUTTING.get())) {
            registry.addRecipe(new CuttingEmiRecipe(recipe.getId(), EmiIngredient.of(recipe.getTool()), EmiIngredient.of(recipe.getIngredients().get(0)),
                    recipe.getRollableResults().stream().map(chanceResult -> EmiStack.of(chanceResult.getStack()).setChance(chanceResult.getChance())).toList()));
        }
        registry.addRecipe(new DecompositionEmiRecipe());
    }
}
