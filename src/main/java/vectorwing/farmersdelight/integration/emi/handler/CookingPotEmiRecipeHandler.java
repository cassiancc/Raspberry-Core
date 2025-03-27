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
package vectorwing.farmersdelight.integration.emi.handler;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.integration.emi.FDRecipeCategories;

import java.util.ArrayList;
import java.util.List;

public class CookingPotEmiRecipeHandler implements StandardRecipeHandler<CookingPotMenu> {
    @Override
    public List<Slot> getInputSources(CookingPotMenu handler) {
        List<Slot> slots = new ArrayList<>();

        for (int i = 0; i < 7; ++i) {
            slots.add(handler.getSlot(i));
        }

        for (int i = 9; i < 9 + 36; ++i) {
            slots.add(handler.getSlot(i));
        }

        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(CookingPotMenu handler) {
        List<Slot> slots = new ArrayList<>();

        for (int i = 0; i < 7; ++i) {
            slots.add(handler.getSlot(i));
        }

        return slots;
    }

    @Override
    public @Nullable Slot getOutputSlot(CookingPotMenu handler) {
        return handler.slots.get(8);
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == FDRecipeCategories.COOKING && recipe.supportsRecipeTree();
    }
}
