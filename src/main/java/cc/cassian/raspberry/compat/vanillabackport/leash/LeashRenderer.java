/* The MIT License (MIT)

Copyright (c) 2025 

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */


package cc.cassian.raspberry.compat.vanillabackport.leash;

import cc.cassian.raspberry.mixin.minecraft.EntityRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class LeashRenderer<T extends Entity> {
    // Visual constants
    private static final float LEASH_WIDTH = 0.05F; // Width of the leash strip
    private static final float LEASH_HEIGHT_THICKNESS = 0.075F; // Vertical thickness (Y-offset)
    private static final float DROP_FACTOR_MAX = 0.15F;

    // Color constants (R, G, B)
    private static final float RED_PRIM = 112f / 255f;
    private static final float GREEN_PRIM = 75f / 255f;
    private static final float BLUE_PRIM = 42f / 255f;

    private static final float RED_SEC = 79f / 255f;
    private static final float GREEN_SEC = 48f / 255f;
    private static final float BLUE_SEC = 26f / 255f;

    private final EntityRenderDispatcher dispatcher;
    @Nullable private List<LeashState> leashStates;

    public LeashRenderer(EntityRenderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public boolean shouldRender(T entity, Frustum camera, boolean isVisible) {
        if (!isVisible) {
            AABB entityBox = entity.getBoundingBoxForCulling().inflate(0.5);

            if (Double.isNaN(entityBox.minX) || entityBox.getSize() == 0.0) {
                entityBox = new AABB(
                        entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0,
                        entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0
                );
            }

            if (camera.isVisible(entityBox)) {
                return true;
            }

            if (entity instanceof Leashable leashable) {
                Entity holder = leashable.raspberry$getLeashHolder();
                if (holder != null) {
                    AABB holderBox = holder.getBoundingBoxForCulling();
                    if (camera.isVisible(holderBox) || camera.isVisible(entityBox.minmax(holderBox))) {
                        return true;
                    }
                }
            }

            if (entity instanceof KnotConnectionAccess access) {
                List<LeashFenceKnotEntity> connectedKnots = access.raspberry$getConnectionManager()
                        .getConnectedKnots((LeashFenceKnotEntity) entity);
                for (LeashFenceKnotEntity knot : connectedKnots) {
                    AABB knotBox = knot.getBoundingBoxForCulling();
                    if (camera.isVisible(knotBox) || camera.isVisible(entityBox.minmax(knotBox))) {
                        return true;
                    }
                }
            }
        }
        return isVisible;
    }

    public void render(T entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer) {
        this.setupLeashRendering(entity, partialTick);

        if (this.leashStates != null) {
            for (LeashState state : this.leashStates) {
                this.renderLeash(poseStack, buffer, state);
            }
        }
    }

    private void renderLeash(PoseStack stack, MultiBufferSource buffer, LeashState state) {
        float deltaX = (float) (state.end.x - state.start.x);
        float deltaY = (float) (state.end.y - state.start.y);
        float deltaZ = (float) (state.end.z - state.start.z);

        float horizontalSq = deltaX * deltaX + deltaZ * deltaZ;

        stack.pushPose();
        stack.translate(state.offset.x, state.offset.y, state.offset.z);

        VertexConsumer vertices = buffer.getBuffer(RenderType.leash());
        Matrix4f matrices = stack.last().pose();

        if (horizontalSq < 1.0E-4F) {
            // Vertical leashes
            float diagonalOffset = (LEASH_WIDTH / 2.0F) * 0.7071F;

            for (int segment = 0; segment <= 24; segment++) {
                addVertexPair(vertices, matrices, deltaX, deltaY, deltaZ, LEASH_HEIGHT_THICKNESS,
                        diagonalOffset, diagonalOffset, segment, false, state);
            }

            for (int segment = 0; segment <= 24; segment++) {
                addVertexPair(vertices, matrices, deltaX, deltaY, deltaZ, LEASH_HEIGHT_THICKNESS,
                        -diagonalOffset, diagonalOffset, segment, false, state);
            }

            stack.popPose();
            return;
        }

        double scaleFactor = Mth.fastInvSqrt(horizontalSq) * (LEASH_WIDTH / 2.0F);
        double offsetZ = deltaZ * scaleFactor;
        double offsetX = deltaX * scaleFactor;

        if (state.isKnotToKnot && deltaY == 0.0F) {
            float horizontalDistance = Mth.sqrt(horizontalSq);
            float distanceScale = 0.05F;
            state.droopAmount = DROP_FACTOR_MAX / (1.0F + horizontalDistance * distanceScale);
        }

        for (int segment = 0; segment <= 24; segment++) {
            addVertexPair(vertices, matrices, deltaX, deltaY, deltaZ, LEASH_HEIGHT_THICKNESS,
                    (float) offsetZ, (float) offsetX, segment, false, state);
        }

        for (int segment = 24; segment >= 0; segment--) {
            addVertexPair(vertices, matrices, deltaX, deltaY, deltaZ, 0.0F,
                    (float) offsetZ, (float) offsetX, segment, true, state);
        }

        stack.popPose();
    }

    private static void addVertexPair(
            VertexConsumer vertices, Matrix4f matrices,
            float deltaX, float deltaY, float deltaZ,
            float thicknessY,
            float offsetZ, float offsetX,
            int segment, boolean isInnerFace, LeashState state
    ) {
        float progress = (float) segment / 24.0f;

        int blockLight = (int) Mth.lerp(progress, state.startBlockLight, state.endBlockLight);
        int skyLight = (int) Mth.lerp(progress, state.startSkyLight, state.endSkyLight);
        int packedLight = LightTexture.pack(blockLight, skyLight);

        boolean useSecondaryColor = segment % 2 == (isInnerFace ? 1 : 0);
        float r = useSecondaryColor ? RED_SEC : RED_PRIM;
        float g = useSecondaryColor ? GREEN_SEC : GREEN_PRIM;
        float b = useSecondaryColor ? BLUE_SEC : BLUE_PRIM;

        float posX = deltaX * progress;
        float posZ = deltaZ * progress;
        float posY;

        if (state.slack) {
            posY = (deltaY > 0.0f
                    ? deltaY * progress * progress
                    : deltaY - deltaY * (1.0f - progress) * (1.0f - progress));
        } else {
            posY = deltaY * progress;
        }

        if (state.isKnotToKnot && state.droopAmount > 0) {
            float droopFactor = progress * (1.0f - progress) * 4.0f;
            posY -= state.droopAmount * droopFactor;
        }

        vertices.vertex(matrices, posX - offsetZ, posY + thicknessY, posZ + offsetX)
                .color(r, g, b, 1.0f).uv2(packedLight).endVertex();
        vertices.vertex(matrices, posX + offsetZ, posY + LEASH_HEIGHT_THICKNESS - thicknessY, posZ - offsetX)
                .color(r, g, b, 1.0f).uv2(packedLight).endVertex();
    }

    private void setupLeashRendering(T entity, float partialTicks) {
        List<LeashState> collector = new ArrayList<>();

        // Standard leash (entity -> holder)
        if (entity instanceof Leashable leashable) {
            Entity leashHolder = leashable.raspberry$getLeashHolder();
            if (leashHolder != null) {
                collector.add(createLeashState(entity, leashable, leashHolder, partialTicks, false));
            }
        }

        // Knot-to-knot connections
        if (entity instanceof KnotConnectionAccess access && entity instanceof Leashable leashableSelf) {
            List<LeashFenceKnotEntity> connectedKnots = access.raspberry$getConnectionManager()
                    .getConnectedKnots((LeashFenceKnotEntity) entity);

            for (LeashFenceKnotEntity targetKnot : connectedKnots) {
                if (entity.getUUID().compareTo(targetKnot.getUUID()) < 0) {
                    collector.add(createLeashState(entity, leashableSelf, targetKnot, partialTicks, true));
                }
            }
        }

        this.leashStates = collector.isEmpty() ? null : collector;
    }

    private LeashState createLeashState(T entity, Leashable leashable, Entity target, float partialTicks, boolean isKnotToKnot) {
        LeashState state = new LeashState();

        float entityRotation = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) * Mth.DEG_TO_RAD;
        Vec3 leashOffset = leashable.raspberry$getLeashOffset(partialTicks);
        Vec3 rotatedOffset = leashOffset.yRot(-entityRotation);

        state.offset = rotatedOffset;
        state.start = entity.getPosition(partialTicks).add(rotatedOffset);

        if (isKnotToKnot && target instanceof Leashable targetLeashable) {
            float targetRotation = Mth.lerp(partialTicks, target.yRotO, target.getYRot()) * Mth.DEG_TO_RAD;
            Vec3 targetOffset = targetLeashable.raspberry$getLeashOffset(partialTicks).yRot(-targetRotation);
            state.end = target.getPosition(partialTicks).add(targetOffset);
        } else {
            state.end = target.getRopeHoldPosition(partialTicks);
        }

        BlockPos entityPos = BlockPos.containing(entity.getEyePosition(partialTicks));
        BlockPos targetPos = BlockPos.containing(target.getEyePosition(partialTicks));

        state.startBlockLight = this.getBlockLightLevel(entity, entityPos);
        state.endBlockLight = this.getBlockLightLevel(target, targetPos);
        state.startSkyLight = entity.level().getBrightness(LightLayer.SKY, entityPos);
        state.endSkyLight = entity.level().getBrightness(LightLayer.SKY, targetPos);

        state.slack = isKnotToKnot;
        state.isKnotToKnot = isKnotToKnot;

        return state;
    }

    private int getBlockLightLevel(Entity entity, BlockPos pos) {
        return ((EntityRendererAccessor) this.dispatcher.getRenderer(entity)).callGetBlockLightLevel(entity, pos);
    }
}