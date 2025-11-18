package cc.cassian.raspberry.client.entity.renderer;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.entity.SwapArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class SwapArrowRenderer extends ArrowRenderer<SwapArrowEntity> {
    private static final ResourceLocation SWAP_ARROW = RaspberryMod.locate("textures/entity/projectiles/swap_arrow.png");

    public SwapArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SwapArrowEntity entity) {
        return SWAP_ARROW;
    }
}
