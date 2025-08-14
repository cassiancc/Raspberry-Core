package com.simibubi.create.compat.emi.recipes;

import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.compat.emi.CreateEmiPlugin;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import dev.emi.emi.api.widget.WidgetHolder;

public class PressingEmiRecipe extends CreateEmiRecipe<PressingRecipe> {

	public PressingEmiRecipe(PressingRecipe recipe) {
		super(CreateEmiPlugin.PRESSING, recipe, 134, 90);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		addTexture(widgets, AllGuiTextures.JEI_SHADOW, 40, 59);
		addTexture(widgets, AllGuiTextures.JEI_LONG_ARROW, 31, 69);

		addSlot(widgets, input.get(0), 6, 66);

		for (int i = 0; i < output.size(); i++) {
			addSlot(widgets, output.get(i), 110 + i * 19, 65).recipeContext(this);
		}

		CreateEmiAnimations.addPress(widgets, 50, 40, false);
	}
}
