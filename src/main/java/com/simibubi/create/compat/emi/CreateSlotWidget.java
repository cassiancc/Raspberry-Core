package com.simibubi.create.compat.emi;

import java.util.List;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class CreateSlotWidget extends SlotWidget {
	public CreateSlotWidget(EmiIngredient stack, int x, int y) {
		super(stack, x, y);
	}

	@Override
	public List<ClientTooltipComponent> getTooltip(int mouseX, int mouseY) {
		List<ClientTooltipComponent> tooltip = super.getTooltip(mouseX, mouseY);
		return tooltip;
	}
}
