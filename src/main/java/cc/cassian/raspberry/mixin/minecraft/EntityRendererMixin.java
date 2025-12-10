package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.common.api.leash.LeashRenderer;
import cc.cassian.raspberry.common.api.leash.Leashable;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    
    @Unique 
    private LeashRenderer<T> leashRenderer;
    
    @Shadow @Final 
    protected EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void raspberry$init(EntityRendererProvider.Context context, CallbackInfo ci) {
        this.leashRenderer = new LeashRenderer<>(this.entityRenderDispatcher);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void raspberry$renderLeash(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (entity instanceof Leashable) {
            this.leashRenderer.render(entity, partialTick, poseStack, buffer);
        }
    }

    @Inject(method = "shouldRender", at = @At("TAIL"), cancellable = true)
    private void raspberry$shouldRenderLeash(T entity, Frustum camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Leashable) {
            cir.setReturnValue(this.leashRenderer.shouldRender(entity, camera, cir.getReturnValue()));
        }
    }
}