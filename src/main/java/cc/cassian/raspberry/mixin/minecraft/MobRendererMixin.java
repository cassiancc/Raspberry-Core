package cc.cassian.raspberry.mixin.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobRenderer.class)
public abstract class MobRendererMixin<T extends Mob, M extends EntityModel<T>> {

    @Inject(
        method = "renderLeash",
        at = @At("HEAD"),
        cancellable = true
    )
    private void raspberry$cancelVanillaLeash(T mob, float partialTick, PoseStack poseStack, MultiBufferSource buffer, Entity holder, CallbackInfo ci) {
        ci.cancel();
    }
}