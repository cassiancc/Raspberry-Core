package com.simibubi.create.compat.emi.recipes;

import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.compat.emi.CreateEmiPlugin;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import dev.emi.emi.api.widget.WidgetHolder;

public class SawingEmiRecipe extends CreateEmiRecipe<CuttingRecipe> {

	public SawingEmiRecipe(CuttingRecipe recipe) {
		super(CreateEmiPlugin.SAWING, recipe, 134, 80);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 48, 10);
		addTexture(widgets, AllGuiTextures.JEI_SHADOW, 33, 55);

		addSlot(widgets, input.get(0), 21, 8);

		for (int i = 0; i < output.size(); i++) {
			int x = i % 2 == 0 ? 0 : 19;
			int y = (i / 2) * -19;
			addSlot(widgets, output.get(i), 95 + x, 47 + y).recipeContext(this);
		}

		CreateEmiAnimations.addSaw(widgets, 50, 42);
	}
}
