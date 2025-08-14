package com.simibubi.create.compat.emi;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.equipment.blueprint.BlueprintAssignCompleteRecipePacket;
import com.simibubi.create.content.equipment.blueprint.BlueprintMenu;

import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlueprintTransferHandler implements EmiRecipeHandler<BlueprintMenu> {
	private static final EmiPlayerInventory empty = new EmiPlayerInventory(List.of());

	@Override
	public EmiPlayerInventory getInventory(AbstractContainerScreen<BlueprintMenu> screen) {
		return empty;
	}

	@Override
	public boolean supportsRecipe(EmiRecipe recipe) {
		return recipe instanceof EmiCraftingRecipe e && e.getId() != null;
	}

	@Override
	public boolean canCraft(EmiRecipe recipe, EmiCraftContext<BlueprintMenu> context) {
		return true;
	}

	@Override
	public boolean craft(EmiRecipe recipe, EmiCraftContext<BlueprintMenu> context) {
		if (recipe instanceof EmiCraftingRecipe craftingRecipe) {
			Minecraft.getInstance().setScreen(context.getScreen());
			AllPackets.getChannel().sendToServer(new BlueprintAssignCompleteRecipePacket(craftingRecipe.getId()));
			return true;
		}
		return false;
	}
}
