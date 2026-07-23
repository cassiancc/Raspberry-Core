package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.events.DripstoneEvent;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiDripstoneRecipe implements EmiRecipe {

	public static final EmiStack POINTED_DRIPSTONE = EmiStack.of(Items.POINTED_DRIPSTONE);
	private final EmiStack requiredFluid;
	private final EmiStack requiredBlock;
	private final EmiIngredient convertsToTarget;
	private final EmiStack targetBlock;
	private final ResourceLocation id;
	private final boolean generateAsCrystal;
	private final Fluid fluid;

	public EmiDripstoneRecipe(DripstoneEvent.DripstoneGenerator dripstoneGenerator) {
		ResourceLocation location = dripstoneGenerator.targetBlockState().getBlockHolder().unwrapKey().orElseThrow().location();
		this.id = new ResourceLocation("raspberry", "/dripping/"+ location.getNamespace() +"_"+ location.getPath());
		this.fluid = dripstoneGenerator.requiredFluid();
		this.requiredFluid = EmiStack.of(dripstoneGenerator.requiredFluid());
		this.requiredBlock = EmiStack.of(dripstoneGenerator.requiredBlock());
		this.convertsToTarget = EmiIngredient.of(dripstoneGenerator.convertsToTarget());
		this.targetBlock = EmiStack.of(dripstoneGenerator.targetBlockState().getBlock());
		this.generateAsCrystal = dripstoneGenerator.generateAsCrystal();
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EmiCompat.DRIPPING;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(requiredFluid, requiredBlock, convertsToTarget);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(targetBlock);
	}

	@Override
	public int getDisplayWidth() {
		return 80;
	}

	@Override
	public int getDisplayHeight() {
		return 100;
	}

	@Override
	public void addWidgets(WidgetHolder widgetHolder) {
		widgetHolder.addSlot(requiredFluid, 0, 0);
		widgetHolder.addSlot(requiredBlock, 0, 20);
		widgetHolder.addSlot(POINTED_DRIPSTONE, 0, 40);
		widgetHolder.addSlot(0, 60);
		widgetHolder.addSlot(convertsToTarget, 0, 80);

		if (fluid.isSame(Fluids.LAVA)) {
			widgetHolder.addFillingArrow(27, 50, 18000);
		} else {
			widgetHolder.addFillingArrow(27, 50, 6000);
		}

		int column1 = 60;

		widgetHolder.addSlot(requiredFluid, column1, 0);
		widgetHolder.addSlot(requiredBlock, column1, 20);
		widgetHolder.addSlot(POINTED_DRIPSTONE, column1, 40);
		if (generateAsCrystal) {
			widgetHolder.addSlot(targetBlock, column1, 60).recipeContext(this);
			widgetHolder.addSlot(convertsToTarget, column1, 80).recipeContext(this);
		} else {
			widgetHolder.addSlot(column1, 60).recipeContext(this); // air
			widgetHolder.addSlot(targetBlock, column1, 80).recipeContext(this);
		}
	}
}
