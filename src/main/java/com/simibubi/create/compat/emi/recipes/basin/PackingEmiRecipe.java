package com.simibubi.create.compat.emi.recipes.basin;

import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.compat.emi.CreateEmiPlugin;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import dev.emi.emi.api.widget.WidgetHolder;

public class PackingEmiRecipe extends BasinEmiRecipe {

	public PackingEmiRecipe(BasinRecipe recipe) {
		super(CreateEmiPlugin.PACKING, recipe, false);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		super.addWidgets(widgets);

		HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (requiredHeat != HeatCondition.NONE) {
			CreateEmiAnimations.addBlazeBurner(widgets, widgets.getWidth() / 2 + 3, 55, requiredHeat.visualizeAsBlazeBurner());
		}
		CreateEmiAnimations.addPress(widgets, widgets.getWidth() / 2 + 3, 40, true);
	}
}
