package com.simibubi.create.compat.emi;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Vector3f;
import com.simibubi.create.foundation.gui.element.GuiGameElement;

import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.DrawableWidget.DrawableWidgetConsumer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.List;

public record RenderedBlock(BlockState state) implements EmiRenderable, DrawableWidgetConsumer {
	@Nullable
	public static RenderedBlock of(EmiIngredient ingredient) {
		List<EmiStack> stacks = ingredient.getEmiStacks();
		if (stacks.size() == 0)
			return null;
		return of(stacks.get(0));
	}

	@Nullable
	public static RenderedBlock of(EmiStack stack) {
		ItemStack item = stack.getItemStack();
		if (item.isEmpty())
			return null;
		if (!(item.getItem() instanceof BlockItem block))
			return null;
		return new RenderedBlock(block.getBlock().defaultBlockState());
	}
	@Override
	public void render(PoseStack matrixStack, int x, int y, float delta) {
		matrixStack.pushPose();
		matrixStack.translate(74, 51, 100);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(-15.5f));
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(22.5f));
		int scale = 20;

		GuiGameElement.of(state)
				.lighting(CreateEmiAnimations.DEFAULT_LIGHTING)
				.scale(scale)
				.render(matrixStack);

		matrixStack.popPose();
	}
}
