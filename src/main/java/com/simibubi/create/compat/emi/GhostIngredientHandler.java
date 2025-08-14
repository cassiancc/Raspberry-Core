package com.simibubi.create.compat.emi;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.filter.AttributeFilterScreen;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.GhostItemSubmitPacket;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GhostIngredientHandler<T extends GhostItemMenu<?>>
		implements EmiDragDropHandler<AbstractSimiContainerScreen<T>> {

	public static final int INVENTORY_SIZE = 36;

	@Override
	public boolean dropStack(AbstractSimiContainerScreen<T> gui, EmiIngredient ingredient, int x, int y) {
		List<EmiStack> stacks = ingredient.getEmiStacks();
		if (stacks.size() != 1)
			return false;
		ItemStack stack = stacks.get(0).getItemStack();
		if (stack.isEmpty())
			return false;
		boolean isAttributeFilter = gui instanceof AttributeFilterScreen;

		for (int i = INVENTORY_SIZE; i < gui.getMenu().slots.size(); i++) {
			Slot slot = gui.getMenu().slots.get(i);
			if (slot.isActive()) {
				Rect2i slotArea = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
				if (slotArea.contains(x, y)) {
					acceptStack(gui, isAttributeFilter, i - INVENTORY_SIZE, stack);
					return true;
				}
			}
			// Only accept items in 1st slot. 2nd is used for functionality, don't wanna
			// override that one
			if (isAttributeFilter)
				break;
		}

		return false;
	}

	private void acceptStack(AbstractSimiContainerScreen<T> gui, boolean isAttributeFilter, int slotIndex, ItemStack stack) {
		stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
		gui.getMenu().ghostInventory.setStackInSlot(slotIndex, stack);

		if (isAttributeFilter)
			return;

		// sync new filter contents with server
		AllPackets.getChannel().sendToServer(new GhostItemSubmitPacket(stack, slotIndex));
	}
}
