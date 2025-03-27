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
package vectorwing.farmersdelight.integration.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;

import java.util.List;

import static cc.cassian.raspberry.RaspberryMod.identifier;

public class CuttingEmiRecipe implements EmiRecipe {
    private static final ResourceLocation BACKGROUND = identifier(FarmersDelight.MODID, "textures/gui/jei/cutting_board.png");
    public static final int OUTPUT_GRID_X = 69;
    public static final int OUTPUT_GRID_Y = 3;

    private final ResourceLocation id;
    private final EmiIngredient tool;
    private final EmiIngredient input;
    private final List<EmiStack> outputs;

    public CuttingEmiRecipe(ResourceLocation id, EmiIngredient tool, EmiIngredient input, List<EmiStack> outputs) {
        this.id = id;
        this.tool = tool;
        this.input = input;
        this.outputs = outputs;

    }

    @Override
    public EmiRecipeCategory getCategory() {
        return FDRecipeCategories.CUTTING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(tool);
    }

    @Override
    public int getDisplayWidth() {
        return 111;
    }

    @Override
    public int getDisplayHeight() {
        return 44;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, 111, 44, 4, 7);

        widgets.addSlot(tool, 11, 0).drawBack(false);
        widgets.addSlot(input, 11, 19).drawBack(false);

        int size = outputs.size();
        int centerX = size > 1 ? 1 : 10;
        int centerY = size > 2 ? 1 : 10;

        for (int i = 0; i < size; i++) {
            int xOffset = centerX + (i % 2 == 0 ? 0 : 19);
            int yOffset = centerY + ((i / 2) * 19);

            EmiIngredient output = outputs.get(i);
            widgets.addSlot(output, OUTPUT_GRID_X + xOffset, OUTPUT_GRID_Y + yOffset).backgroundTexture(BACKGROUND, output.getChance() < 1 ?  18 : 0, 58).recipeContext(this);
        }
    }
}
