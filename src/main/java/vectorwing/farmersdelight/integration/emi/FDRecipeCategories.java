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

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import vectorwing.farmersdelight.FarmersDelight;

import static cc.cassian.raspberry.RaspberryMod.identifier;

public class FDRecipeCategories {
    private static final ResourceLocation SIMPLIFIED_TEXTURES = identifier(FarmersDelight.MODID, "textures/gui/emi/simplified.png");

    public static final EmiRecipeCategory COOKING = new EmiRecipeCategory(identifier(FarmersDelight.MODID, "cooking"), FDRecipeWorkstations.COOKING_POT, simplifiedRenderer(0, 0));
    public static final EmiRecipeCategory CUTTING = new EmiRecipeCategory(identifier(FarmersDelight.MODID, "cutting"), FDRecipeWorkstations.CUTTING_BOARD, simplifiedRenderer(16, 0));
    public static final EmiRecipeCategory DECOMPOSITION = new EmiRecipeCategory(identifier(FarmersDelight.MODID, "decomposition"), FDRecipeWorkstations.ORGANIC_COMPOST, simplifiedRenderer(32, 0));

    private static EmiRenderable simplifiedRenderer(int u, int v) {
        return (draw, x, y, delta) -> {
            RenderSystem.setShaderTexture(0, SIMPLIFIED_TEXTURES);
            GuiComponent.blit(draw, x, y, u, v, 16, 16, 48, 16);
        };
    }
}
