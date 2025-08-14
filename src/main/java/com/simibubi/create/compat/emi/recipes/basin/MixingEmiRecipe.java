package com.simibubi.create.compat.emi.recipes.basin;

import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.compat.emi.CreateEmiPlugin;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;

public class MixingEmiRecipe extends BasinEmiRecipe {

	public MixingEmiRecipe(EmiRecipeCategory category, BasinRecipe recipe) {
		super(category, recipe, category != CreateEmiPlugin.AUTOMATIC_SHAPELESS);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		super.addWidgets(widgets);

		HeatCondition requiredHeat = recipe.getRequiredHeat();
		if (requiredHeat != HeatCondition.NONE) {
			CreateEmiAnimations.addBlazeBurner(widgets, widgets.getWidth() / 2 + 3, 55, requiredHeat.visualizeAsBlazeBurner());
		}
		CreateEmiAnimations.addMixer(widgets, widgets.getWidth() / 2 + 3, 40);
	}
}
