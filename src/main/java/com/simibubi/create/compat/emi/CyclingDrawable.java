package com.simibubi.create.compat.emi;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.emi.emi.api.widget.DrawableWidget.DrawableWidgetConsumer;

import java.util.List;

public record CyclingDrawable(List<? extends DrawableWidgetConsumer> children) implements DrawableWidgetConsumer {
	@Override
	public void render(PoseStack matrices, int x, int y, float delta) {
		this.choose().render(matrices, x, y, delta);
	}

	private DrawableWidgetConsumer choose() {
		// ticks are not available, use system time.
		long millis = System.currentTimeMillis();
		long seconds = millis / 1000;
		int index = (int) (seconds % this.children.size());
		return this.children.get(index);
	}
}
