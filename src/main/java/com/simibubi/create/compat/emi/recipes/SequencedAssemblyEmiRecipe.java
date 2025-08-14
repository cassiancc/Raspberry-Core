package com.simibubi.create.compat.emi.recipes;

import java.util.List;

import com.simibubi.create.compat.emi.CreateEmiPlugin;
import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class SequencedAssemblyEmiRecipe extends CreateEmiRecipe<SequencedAssemblyRecipe> {
	public static final String[] ROMAN = {
		"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX",
		"X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX",
		"XX", "XXI", "XXII", "XXIII", "XXIV", "XXV", "XXVI", "XXVII", "XXVIII", "XXIX", "-" };
	private final int margin = 3;
	private int width;

	public SequencedAssemblyEmiRecipe(SequencedAssemblyRecipe recipe) {
		super(CreateEmiPlugin.SEQUENCED_ASSEMBLY, recipe, 180, 120);
		for (SequencedRecipe<?> r : recipe.getSequence()) {
			width += getSubCategory(r).getWidth() + margin;
		}
		width -= margin;
	}

	@Override
	public int getDisplayWidth() {
		return Math.max(150, width);
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		int xOff = recipe.getOutputChance() == 1 ? 0 : -7;
		int mid = widgets.getWidth() / 2;

		addTexture(widgets, AllGuiTextures.JEI_LONG_ARROW, mid - 38 + xOff, 94);

		widgets.addDrawable(mid - 38 + xOff, 94, AllGuiTextures.JEI_LONG_ARROW.width, 20, (matrices, mx, my, delta) -> {})
			.tooltip((mouseX, mouseY) -> List.of(ClientTooltipComponent.create(
				Lang.translateDirect("recipe.assembly.repeat", recipe.getLoops()).getVisualOrderText())));

		if (recipe.getOutputChance() != 1) {
			float chance = recipe.getOutputChance();
			addTexture(widgets, AllGuiTextures.JEI_CHANCE_SLOT, mid + 60 + xOff, 90)
				.tooltip((mouseX, mouseY) -> List.of(
					ClientTooltipComponent.create(Lang.translateDirect("recipe.assembly.junk").getVisualOrderText()),
					ClientTooltipComponent.create(Components.translatable("tooltip.emi.chance.produce", chance > 0.99 ? "<1" : 100 - (int) (chance * 100))
						.withStyle(ChatFormatting.GOLD).getVisualOrderText())
				));
		}

		addSlot(widgets, EmiIngredient.of(recipe.getIngredient()), mid - 64 + xOff, 90);

		addSlot(widgets, output.get(0), mid + 41 + xOff, 90).recipeContext(this);

		int sx = width / -2 + mid;
		int x = sx;
		int index = 0;
		for (SequencedRecipe<?> recipe : recipe.getSequence()) {
			EmiSequencedAssemblySubCategory category = getSubCategory(recipe);
			category.addWidgets(widgets, x, 0, recipe, index++);
			x += category.getWidth() + margin;
		}

		widgets.addDrawable(0, 0, 0, 0, (matrices, mouseX, mouseY, delta) -> {
			Minecraft client = Minecraft.getInstance();
			matrices.pushPose();
			matrices.translate(0, 15, 0);
			if (recipe.getOutputChance() != 1) {
				client.font.drawShadow(matrices, "?", mid + 69 + xOff - client.font.width("?") / 2, 80, 0xefefef);
			}

			if (recipe.getLoops() > 1) {
				matrices.pushPose();
				matrices.translate(15, 9, 0);
				AllIcons.I_SEQ_REPEAT.render(matrices, mid - 40 + xOff, 75);
				client.font.drawShadow(matrices, "x" + recipe.getLoops(), mid - 24 + xOff, 80, 0x888888);
				matrices.popPose();
			}
			matrices.popPose();

			int cx = sx;
			for (int i = 0; i < recipe.getSequence().size(); i++) {
				String text = ROMAN[Math.min(i, ROMAN.length)];
				int w = getSubCategory(recipe.getSequence().get(i)).getWidth();
				int off = w / 2 - client.font.width(text) / 2;
				client.font.drawShadow(matrices, text, cx + off, 2, 0x888888);
				cx += w + margin;
			}
		});
	}

	public static EmiSequencedAssemblySubCategory getSubCategory(SequencedRecipe<?> recipe) {
		SequencedAssemblySubCategory sequencedAssemblySubCategory = recipe.getAsAssemblyRecipe()
				.getJEISubCategory().get().get();
		if (sequencedAssemblySubCategory instanceof SequencedAssemblySubCategory.AssemblyPressing assemblyPressing) {
			return new EmiSequencedAssemblySubCategory.AssemblyPressing();
		}
		else if (sequencedAssemblySubCategory instanceof SequencedAssemblySubCategory.AssemblySpouting assemblyPressing) {
			return new EmiSequencedAssemblySubCategory.AssemblySpouting();
		}
		else if (sequencedAssemblySubCategory instanceof SequencedAssemblySubCategory.AssemblyDeploying assemblyPressing) {
			return new EmiSequencedAssemblySubCategory.AssemblyDeploying();
		}
		else if (sequencedAssemblySubCategory instanceof SequencedAssemblySubCategory.AssemblyCutting assemblyPressing) {
			return new EmiSequencedAssemblySubCategory.AssemblyCutting();
		}
		return null;
	}
}
