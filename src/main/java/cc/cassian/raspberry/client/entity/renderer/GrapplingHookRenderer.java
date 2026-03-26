package cc.cassian.raspberry.client.entity.renderer;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.entity.GrapplingHookEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class GrapplingHookRenderer extends EntityRenderer<GrapplingHookEntity> {
    private static final ResourceLocation HOOK = RaspberryMod.locate("textures/entity/projectiles/grappling_hook.png");
    private static final RenderType HOOK_RENDER;

    public GrapplingHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public void render(@Nonnull GrapplingHookEntity entity, float entityYaw, float partialTicks, @Nonnull PoseStack matrixStack, @Nonnull MultiBufferSource buffer, int packedLight) {
        Player player = entity.getPlayerOwner();
        if (player == null) return;

        boolean isAttached = entity.isAttached();

        // Line Colors
        ItemStack line = entity.getFishingLine();
        List<Integer> colors = List.of(
            0x704b2a,
            0x634225
        );

        if (!line.isEmpty()) {
            DyeableLeatherItem lineItem = (DyeableLeatherItem)line.getItem();
            if (lineItem.hasCustomColor(line)) {
                int colorInt = lineItem.getColor(line);

                // Darken secondary color
                int r = (colorInt >> 16) & 0xFF;
                int g = (colorInt >> 8) & 0xFF;
                int b = colorInt & 0xFF;

                r = (int)(r * 0.95f);
                g = (int)(g * 0.95f);
                b = (int)(b * 0.95f);

                int darkened = (r << 16) | (g << 8) | b;

                colors = List.of(colorInt, darkened);
            }

            if (line.hasCustomHoverName()) {
                String name = line.getHoverName().getString();
                for (Map.Entry<String, List<String>> entry : ModConfig.get().fishing_line_patterns.entrySet()) {
                    if (name.equalsIgnoreCase(entry.getKey())) {
                        colors = ModConfig.get().fishing_line_patterns.get(entry.getKey()).stream().map(colorString -> {
                            if (colorString.startsWith("#")) {
                                colorString = colorString.substring(1);
                            }
                            return Integer.parseInt(colorString, 16);
                        }).toList();
                        break;
                    }
                }
            }
        }

        matrixStack.pushPose();

        // Code from FishingHookRenderer. All of this just finds the position of the tip of the fishing rod
        int armSide = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemstack = player.getMainHandItem();
        if (!itemstack.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
            armSide = -armSide;
        }
        float attackAnim = player.getAttackAnim(partialTicks);
        float swingAngle = Mth.sin(Mth.sqrt(attackAnim) * (float)Math.PI);
        float bodyRotationRad = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
        double sinBodyRot = Mth.sin(bodyRotationRad);
        double cosBodyRot = Mth.cos(bodyRotationRad);
        double armSideOffset = (double)armSide * 0.35;
        double rodTipX;
        double rodTipY;
        double rodTipZ;
        float eyeHeightOffset;
        // Rod tip position in first person
        if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson()) && player == Minecraft.getInstance().player) {
            double cameraFovScale = (double)960.0F / (double)this.entityRenderDispatcher.options.fov().get();
            Vec3 cameraRodTip = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane((float)armSide * 0.525F, -0.1F);
            cameraRodTip = cameraRodTip.scale(cameraFovScale);
            cameraRodTip = cameraRodTip.yRot(swingAngle * 0.5F);
            cameraRodTip = cameraRodTip.xRot(-swingAngle * 0.7F);
            rodTipX = Mth.lerp(partialTicks, player.xo, player.getX()) + cameraRodTip.x;
            rodTipY = Mth.lerp(partialTicks, player.yo, player.getY()) + cameraRodTip.y;
            rodTipZ = Mth.lerp(partialTicks, player.zo, player.getZ()) + cameraRodTip.z;
            eyeHeightOffset = player.getEyeHeight();
        }
        // Rod position in third person
        else {
            rodTipX = Mth.lerp(partialTicks, player.xo, player.getX()) - cosBodyRot * armSideOffset - sinBodyRot * 0.8;
            rodTipY = player.yo + (double)player.getEyeHeight() + (player.getY() - player.yo) * (double)partialTicks - 0.45;
            rodTipZ = Mth.lerp(partialTicks, player.zo, player.getZ()) - sinBodyRot * armSideOffset + cosBodyRot * 0.8;
            eyeHeightOffset = player.isCrouching() ? -0.1875F : 0.0F;
        }
        double offsetY = 0.05;
        double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX());
        double entityY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + offsetY;
        double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

        float rodOffsetX = (float)(rodTipX - entityX);
        float rodOffsetY = (float)(rodTipY - entityY) + eyeHeightOffset;
        float rodOffsetZ = (float)(rodTipZ - entityZ);

        Vec3 rodTipPosition = new Vec3(rodTipX, rodTipY + eyeHeightOffset, rodTipZ);
        Vec3 entityPosition = new Vec3(entityX, entityY, entityZ);

        // Rope Rendering
        VertexConsumer vertexconsumer1 = buffer.getBuffer(RenderType.leash());
        PoseStack.Pose posestack$pose1 = matrixStack.last();

        // Offset so the leash appear at the right thickness
        float scaleFactor = (1.0F / Mth.sqrt(rodOffsetX * rodOffsetX + rodOffsetZ * rodOffsetZ)) * 0.05F / 2.0F;
        float offsetZ = rodOffsetZ * scaleFactor;
        float offsetX = rodOffsetX * scaleFactor;

        float LEASH_THICKNESS = 0.035F;
        int segments = 24;

        double hookRelativeVelocity = getRelativeVelocity(rodTipPosition, player.getDeltaMovement(), entityPosition, entity.getDeltaMovement());
        double negativeRelativeVelocity = Math.min(hookRelativeVelocity, 0);

        for (int segment = 0; segment <= segments; segment++) {
            addVertexPair(vertexconsumer1, posestack$pose1, rodOffsetX, rodOffsetY, rodOffsetZ, LEASH_THICKNESS, LEASH_THICKNESS,
                    offsetZ, offsetX, segment, segments,false, packedLight, colors, isAttached, entity.shakeTime - partialTicks, negativeRelativeVelocity);
        }
        for (int segment = segments; segment >= 0; segment--) {
            addVertexPair(vertexconsumer1, posestack$pose1, rodOffsetX, rodOffsetY, rodOffsetZ, LEASH_THICKNESS, 0.0F,
                    offsetZ, offsetX, segment, segments,true, packedLight, colors, isAttached, entity.shakeTime - partialTicks, negativeRelativeVelocity);
        }

        matrixStack.popPose();

        // Hook rendering
        matrixStack.pushPose();
        matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
        matrixStack.translate(0.0, 1, 0.0);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));

        VertexConsumer vertexConsumer = buffer.getBuffer(HOOK_RENDER);
        PoseStack.Pose pose = matrixStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        // Texture
        float size = 32;
        int width = 8;
        float offset = 8;
        float height = 7;
        int hookOffset = 8;

        // Front plane
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, -3, -3, offset/size, 0, -1, 0, 0, packedLight);
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, -3, 3, (offset+height)/size, 0, -1, 0, 0, packedLight);
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, 3, 3, (offset+height)/size, height/size, -1, 0, 0, packedLight);
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, 3, -3, offset/size, height/size, -1, 0, 0, packedLight);

        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, 3, -3, offset/size, 0, 1, 0, 0, packedLight);
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, 3, 3, (offset+height)/size, 0, 1, 0, 0, packedLight);
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, -3, 3, (offset+height)/size, height/size, 1, 0, 0, packedLight);
        this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-2, -3, -3, offset/size, height/size, 1, 0, 0, packedLight);

        // Side cross
        for(int r = 0; r < 4; ++r) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-width, -3, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset, -3, 0, width/size, 0.0F, 0, 1, 0, packedLight);
            this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset, 3, 0, width/size, height/size, 0, 1, 0, packedLight);
            this.vertex(matrix4f, matrix3f, vertexConsumer, hookOffset-width, 3, 0, 0.0F, height/size, 0, 1, 0, packedLight);
        }

        matrixStack.popPose();
    }

    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull GrapplingHookEntity entity) {
        return HOOK;
    }

    public void vertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, int offsetX, int offsetY, int offsetZ, float textureX, float textureY, int normalX, int m, int n, int packedLight) {
        vertexBuilder.vertex(matrix, (float)offsetX, (float)offsetY, (float)offsetZ).color(255, 255, 255, 255).uv(textureX, textureY).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normals, (float)normalX, (float)n, (float)m).endVertex();
    }

    private static void addVertexPair(
            VertexConsumer vertices, PoseStack.Pose matrices,
            float deltaX, float deltaY, float deltaZ,
            float thickness1, float thickness2,
            float offsetZ, float offsetX,
            int segment, int totalSegments,
            boolean isInnerFace, int packedLight,
            List<Integer> colors, boolean attached, float shakeTime,
            double negativeRelativeHookVelocity
    ) {
        float progress = (float) segment / totalSegments;
        float offsetY = 0.05F;

        // Color
        int color = colors.get((isInnerFace ? segment : segment + colors.size() - 1) % colors.size());
        float red   = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue  = (float)(color & 255) / 255.0F;

        float posX = deltaX * progress;
        float posZ = deltaZ * progress;
        float posY = deltaY * progress;

        double lengthSqr = new Vec3(deltaX, deltaY, deltaZ).lengthSqr();
        int waveFrequency = 3;
        float waveAmplitude = (float) (Math.abs(negativeRelativeHookVelocity) * 0.3 + lengthSqr * 0.0001);
        float wavyness = (float) (waveAmplitude * Math.sin(2 * Math.PI * waveFrequency * progress));
        float midpoint = progress * (1.0f - progress);
        float curve = waveAmplitude * 10 * midpoint;

        if (attached) {
            float shakeProgress = shakeTime / 7;
            if (shakeTime > 0.0F) {
                wavyness = wavyness * Mth.sin(shakeTime * 3) * shakeProgress;
                curve = 0;
            } else {
                wavyness = 0;
                curve = 0;
            }
        }

        posY = posY + offsetY + wavyness + curve;;

        vertices.vertex(matrices.pose(), posX - offsetZ, posY + thickness2, posZ + offsetX)
                .color(red, green, blue, 1.0f).uv2(packedLight).endVertex();
        vertices.vertex(matrices.pose(), posX + offsetZ, posY + thickness1 - thickness2, posZ - offsetX)
                .color(red, green, blue, 1.0f).uv2(packedLight).endVertex();
    }

    private static double getRelativeVelocity(Vec3 pos1, Vec3 vel1, Vec3 pos2, Vec3 vel2) {
        Vec3 displacement = pos2.subtract(pos1);
        Vec3 relativeVelocity = vel2.subtract(vel1);
        return relativeVelocity.dot(displacement.normalize());
    }

    static {
        HOOK_RENDER = RenderType.entityCutout(HOOK);
    }
}
