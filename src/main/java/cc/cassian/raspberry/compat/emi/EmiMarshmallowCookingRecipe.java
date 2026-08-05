package cc.cassian.raspberry.compat.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiCookingRecipe;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;

import java.util.List;

public class EmiMarshmallowCookingRecipe extends EmiCookingRecipe {
	private final int cookingTime;
	private final Item input;
	private final Item output;

	public EmiMarshmallowCookingRecipe(CampfireCookingRecipe fakeRecipe, int cookingTime, Item input, Item output) {
		super(fakeRecipe, VanillaEmiRecipeCategories.CAMPFIRE_COOKING, 0, true);
		this.cookingTime = cookingTime;
		this.input = input;
		this.output = output;
	}

	public void addWidgets(WidgetHolder widgets) {
		widgets.addFillingArrow(24, 5, 50 * cookingTime).tooltip((mx, my) -> List.of(ClientTooltipComponent.create(EmiPort.ordered(EmiPort.translatable("emi.cooking.time", (float) cookingTime / 20.0F)))));
		widgets.addTexture(EmiTexture.FULL_FLAME, 1, 24);

		widgets.addSlot(EmiStack.of(input), 0, 4);
		widgets.addSlot(EmiStack.of(output), 56, 0).large(true).recipeContext(this).appendTooltip(Component.translatable("emi.raspberry.marshmallow_on_a_stick.help"));
	}
}