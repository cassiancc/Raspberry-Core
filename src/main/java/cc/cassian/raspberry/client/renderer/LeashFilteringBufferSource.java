package cc.cassian.raspberry.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class LeashFilteringBufferSource implements MultiBufferSource {
    private final MultiBufferSource delegate;

    public LeashFilteringBufferSource(MultiBufferSource delegate) {
        this.delegate = delegate;
    }

    public MultiBufferSource getDelegate() {
        return delegate;
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        if (type == RenderType.leash()) {
            return new VertexConsumer() {
                public VertexConsumer vertex(double x, double y, double z) { return this; }
                public VertexConsumer color(int r, int g, int b, int a) { return this; }
                public VertexConsumer uv(float u, float v) { return this; }
                public VertexConsumer overlayCoords(int u, int v) { return this; }
                public VertexConsumer uv2(int u, int v) { return this; }
                public VertexConsumer normal(float x, float y, float z) { return this; }
                public void endVertex() { }
                public void defaultColor(int r, int g, int b, int a) { }
                public void unsetDefaultColor() { }
            };
        }
        return delegate.getBuffer(type);
    }
}