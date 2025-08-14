package com.simibubi.create.compat.emi;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import com.simibubi.create.foundation.gui.element.GuiGameElement;

import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

// based on ItemEmiStack
public final class DoubleItemIcon implements EmiRenderable {
	private final ItemStack primaryStack;
	private final ItemStack secondaryStack;
	private boolean unbatchable;

	public DoubleItemIcon(ItemStack primaryStack, ItemStack secondaryStack) {
		this.primaryStack = primaryStack;
		this.secondaryStack = secondaryStack;
	}

	public static DoubleItemIcon of(ItemLike first, ItemLike second) {
		return of(first.asItem().getDefaultInstance(), second.asItem().getDefaultInstance());
	}

	public static DoubleItemIcon of(ItemStack first, ItemStack second) {
		return new DoubleItemIcon(first, second);
	}

	@Override
	public void render(PoseStack matrixStack, int xOffset, int yOffset, float delta) {
		RenderSystem.enableDepthTest();
		matrixStack.pushPose(); // note: this -1 is specific to EMI
		matrixStack.translate(xOffset - 1, yOffset, 0);

		matrixStack.pushPose();
		matrixStack.translate(1, 1, 0);
		GuiGameElement.of(primaryStack)
				.render(matrixStack);
		matrixStack.popPose();

		matrixStack.pushPose();
		matrixStack.translate(10, 10, 100);
		matrixStack.scale(.5f, .5f, .5f);
		GuiGameElement.of(secondaryStack)
				.render(matrixStack);
		matrixStack.popPose();

		matrixStack.popPose();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (DoubleItemIcon) obj;
		return Objects.equals(this.primaryStack, that.primaryStack) &&
				Objects.equals(this.secondaryStack, that.secondaryStack) &&
				this.unbatchable == that.unbatchable;
	}

	@Override
	public int hashCode() {
		return Objects.hash(primaryStack, secondaryStack, unbatchable);
	}

	@Override
	public String toString() {
		return "DoubleItemIcon[" +
				"firstStack=" + primaryStack + ", " +
				"secondStack=" + secondaryStack + ", " +
				"unbatchable=" + unbatchable + ']';
	}


	// TODO look into batching more
	/* implements Batchable
	@Override
	public boolean isSideLit() {
		return usesBlockLight(primaryStack) || usesBlockLight(secondaryStack);
	}

	public static boolean usesBlockLight(ItemStack stack) {
		return Minecraft.getInstance().getItemRenderer()
				.getModel(stack, null, null, 0).usesBlockLight();
	}

	@Override
	public boolean isUnbatchable() {
		return unbatchable || isUnbatchable(primaryStack) || isUnbatchable(secondaryStack);
	}

	public static boolean isUnbatchable(ItemStack stack) {
		return stack.hasFoil() ||
				ColorProviderRegistry.ITEM.get(stack.getItem()) != null ||
				Minecraft.getInstance().getItemRenderer().getModel(stack, null, null, 0).isCustomRenderer();
	}

	@Override
	public void setUnbatchable() {
		this.unbatchable = true;
	}

	@Override
	public void renderForBatch(MultiBufferSource vcp, PoseStack matrices, int x, int y, int z, float delta) {
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
		BakedModel firstModel = renderer.getModel(primaryStack, null, null, 0);
		matrices.pushPose();
		try {
			matrices.translate(x, y, 100.0f + z + (firstModel.isGui3d() ? 50 : 0));
			matrices.translate(8.0, 8.0, 0.0);
			matrices.scale(16.0f, 16.0f, 16.0f);
			renderer.render(primaryStack, TransformType.GUI, false, matrices, vcp,
					LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, firstModel);

			matrices.scale(.5f, .5f, .5f);
			BakedModel secondModel = renderer.getModel(secondaryStack, null, null, 0);
			renderer.render(secondaryStack, TransformType.GUI, false, matrices, vcp,
					LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, secondModel);
		} finally {
			matrices.popPose();
		}
	}
	 */
}
