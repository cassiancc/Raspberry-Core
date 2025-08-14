package com.simibubi.create.compat.emi.recipes;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.emi.CreateEmiAnimations;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class MechanicalCraftingEmiRecipe extends CreateEmiRecipe<CraftingRecipe> {
	private static final int MAX_SIZE = 100;
	private int recipeAmount = 0;

	public MechanicalCraftingEmiRecipe(EmiRecipeCategory category, CraftingRecipe recipe) {
		super(category, recipe, 177, 109, c -> {});
		this.input = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
		this.output = List.of(EmiStack.of(recipe.getResultItem()));
		for (Ingredient ingredient : recipe.getIngredients()) {
			if (!ingredient.isEmpty()) {
				recipeAmount++;
			}
		}
	}

	public float getScale() {
		int w = getWidth();
		int h = getHeight();
		return Math.min(1, MAX_SIZE / (19f * Math.max(w, h)));
	}

	public int getYPadding() {
		return 3 + 50 - (int) (getScale() * getHeight() * 19 * .5);
	}

	public int getXPadding() {
		return 3 + 50 - (int) (getScale() * getWidth() * 19 * .5);
	}

	private int getWidth() {
		return recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getWidth() : 1;
	}

	private int getHeight() {
		return recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getHeight() : 1;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		int x = getXPadding();
		int y = getYPadding();
		float scale = getScale();

		addTexture(widgets, AllGuiTextures.JEI_DOWN_ARROW, 128, 59);
		addTexture(widgets, AllGuiTextures.JEI_SHADOW, 113, 38);


		for (int i = 0; i < input.size(); i++) {
			EmiIngredient ingredient = input.get(i);
			if (ingredient.isEmpty())
				continue;
			float f = 19 * scale;
			int xPosition = (int) (x + 1 + (i % getWidth()) * f);
			int yPosition = (int) (y + 1 + (i / getWidth()) * f);
			widgets.add(new CrafterSlotWidget(ingredient, xPosition, yPosition))
					.backgroundTexture(
							AllGuiTextures.JEI_SLOT.location, AllGuiTextures.JEI_SLOT.startX, AllGuiTextures.JEI_SLOT.startY
					);
		}

		addSlot(widgets, output.get(0), 134, 81).recipeContext(this);

		CreateEmiAnimations.addCrafter(widgets, 132, 38);

		widgets.addText(Component.literal("" + recipeAmount).getVisualOrderText(), 142, 39, -1, true);
	}

	public class CrafterSlotWidget extends SlotWidget {
		private boolean hideStack;

		public CrafterSlotWidget(EmiIngredient stack, int x, int y) {
			super(stack, x, y);
			int w = Mth.ceil(18 * getScale());
			this.bounds = new Bounds(x, y, w, w);
		}

		@Override
		public Bounds getBounds() {
			return bounds;
		}

		@Override
		public EmiIngredient getStack() {
			return hideStack ? EmiStack.EMPTY : super.getStack();
		}

		@Override
		public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
			matrices.pushPose();
			hideStack = true;
			super.render(matrices, mouseX, mouseY, delta);
			hideStack = false;

			float scale = getScale();
			matrices.translate(x, y, 0);
			matrices.scale(scale, scale, scale);
			matrices.translate(-x, -y, 0);

			getStack().render(matrices, bounds.x() + 1, bounds.y() + 1, delta);
			matrices.popPose();
		}
	}
}
