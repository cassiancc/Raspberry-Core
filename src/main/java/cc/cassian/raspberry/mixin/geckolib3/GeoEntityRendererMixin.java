package cc.cassian.raspberry.mixin.geckolib3;

import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public class GeoEntityRendererMixin<M extends LivingEntity & GeoAnimatable> {

    @Inject(
        method = "renderLeash",
        at = @At("HEAD"),
        cancellable = true,
        remap = false 
    )
    private <E extends Entity> void raspberry$cancelGeckoLeash(
            M mob, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, E leashHolder, CallbackInfo ci
    ) {
        if (ModConfig.get().backportLeash) {
            ci.cancel();
        }
    }
}