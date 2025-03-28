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
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;

import java.util.ArrayList;
import java.util.List;

import static cc.cassian.raspberry.RaspberryMod.identifier;

public class CookingPotEmiRecipe implements EmiRecipe {
    private static final ResourceLocation BACKGROUND = identifier("farmersdelight", "textures/gui/cooking_pot.png");

    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final EmiStack output;
    private final EmiStack container;
    private final int cookTime;
    private final float experience;
    private final List<ClientTooltipComponent> tooltipComponents;

    public CookingPotEmiRecipe(ResourceLocation id, List<EmiIngredient> inputs, EmiStack output,
                               EmiStack container, int cookTime, float experience) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.container = container;
        this.cookTime = cookTime;
        this.experience = experience;
        this.tooltipComponents = createTooltipComponents();
    }

    private List<ClientTooltipComponent> createTooltipComponents() {
        List<ClientTooltipComponent> tooltipStrings = new ArrayList<>();

        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            tooltipStrings.add(ClientTooltipComponent.create(Component.translatable("emi.cooking.time", cookTimeSeconds).getVisualOrderText()));
        }
        if (experience > 0) {
            tooltipStrings.add(ClientTooltipComponent.create(Component.translatable("emi.cooking.experience", experience).getVisualOrderText()));
        }

        return tooltipStrings;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return FDRecipeCategories.COOKING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public List<EmiIngredient> getCatalysts() {
        return List.of(container);
    }

    @Override
    public int getDisplayWidth() {
        return 117;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, 0, 0, 116, 56, 29, 16);

        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 3; ++column) {
                int inputIndex = row * 3 + column;
                if (inputIndex < inputs.size()) {
                    addSlot(widgets, inputs.get(inputIndex), (column * borderSlotSize), (row * borderSlotSize));
                }
            }
        }
        addSlot(widgets, output, 94, 9);
        addSlot(widgets, container, 62, 38);
        addSlot(widgets, output, 94, 38).recipeContext(this);

        // Arrow
        widgets.addAnimatedTexture(BACKGROUND, 60, 9, 24, 17, 176, 15, 1000 * 10, true, false, false);
        // Heat Indicator
        widgets.addTexture(BACKGROUND, 18, 39, 17, 15, 176, 0);
        // Time Icon
        widgets.addTexture(BACKGROUND, 64, 2, 8, 11, 176, 32);
        // Experience Icon
        if (experience > 0) {
            widgets.addTexture(BACKGROUND, 63,21, 9, 9, 176, 43);
        }

        widgets.addTooltip((mouseX, mouseY) -> {
            if (ClientRenderUtils.isCursorInsideBounds(60, 2, 22, 28, mouseX, mouseY)) {
                return tooltipComponents;
            }
            return List.of();
        }, 0, 0, widgets.getWidth(), widgets.getHeight());
    }

    private SlotWidget addSlot(WidgetHolder widgets, EmiIngredient ingredient, int x, int y) {
        return widgets.addSlot(ingredient, x, y).drawBack(false);
    }
}
