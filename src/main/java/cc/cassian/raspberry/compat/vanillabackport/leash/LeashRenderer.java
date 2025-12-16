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
import com.mojang.math.Matrix4f;
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

import java.util.ArrayList;
import java.util.List;

public class LeashRenderer<T extends Entity> {
    private static final float LEASH_THICKNESS = 0.075F;
    private final EntityRenderDispatcher dispatcher;
    @Nullable private List<LeashState> leashStates;

    public LeashRenderer(EntityRenderDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public boolean shouldRender(T entity, Frustum camera, boolean isVisible) {
        if (!isVisible) {
            AABB entityBox = entity.getBoundingBoxForCulling().inflate(0.5);
            if (Double.isNaN(entityBox.minX) || entityBox.getSize() == 0.0) {
                 entityBox = new AABB(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, 
                                     entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
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

        if (state.isKnotToKnot && deltaY == 0.0F) {
            float horizontalDistance = (float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float maxDroop = 0.15F;
            float distanceScale = 0.05F;
            state.droopAmount = maxDroop / (1.0F + horizontalDistance * distanceScale);
        }

        float scaleFactor = (1.0F / Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ)) * 0.05F / 2.0F;

        float offsetZ = deltaZ * scaleFactor;
        float offsetX = deltaX * scaleFactor;

        stack.pushPose();
        stack.translate(state.offset.x, state.offset.y, state.offset.z);

        VertexConsumer vertices = buffer.getBuffer(RenderType.leash());
        Matrix4f matrices = stack.last().pose();

        for (int segment = 0; segment <= 24; segment++) {
            addVertexPair(vertices, matrices, deltaX, deltaY, deltaZ, LEASH_THICKNESS,
                         offsetZ, offsetX, segment, false, state);
        }

        for (int segment = 24; segment >= 0; segment--) {
            addVertexPair(vertices, matrices, deltaX, deltaY, deltaZ, 0.0F,
                         offsetZ, offsetX, segment, true, state);
        }

        stack.popPose();
    }

    private static void addVertexPair(
        VertexConsumer vertices, Matrix4f matrices,
        float deltaX, float deltaY, float deltaZ,
        float thickness2,
        float offsetZ, float offsetX,
        int segment, boolean isInnerFace, LeashState state
    ) {
        float progress = (float) segment / 24.0f;

        int blockLight = (int) Mth.lerp(progress, state.startBlockLight, state.endBlockLight);
        int skyLight = (int) Mth.lerp(progress, state.startSkyLight, state.endSkyLight);
        int packedLight = LightTexture.pack(blockLight, skyLight);

        boolean useSecondary = segment % 2 == (isInnerFace ? 1 : 0);

        final float A_R = 112f / 255f;
        final float A_G = 75f / 255f;
        final float A_B = 42f  / 255f;

        final float B_R = 79f / 255f;
        final float B_G = 48f / 255f;
        final float B_B = 26f / 255f;

        float red   = useSecondary ? B_R : A_R;
        float green = useSecondary ? B_G : A_G;
        float blue  = useSecondary ? B_B : A_B;

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

        vertices.vertex(matrices, posX - offsetZ, posY + thickness2, posZ + offsetX)
                .color(red, green, blue, 1.0f).uv2(packedLight).endVertex();
        vertices.vertex(matrices, posX + offsetZ, posY + LeashRenderer.LEASH_THICKNESS - thickness2, posZ - offsetX)
                .color(red, green, blue, 1.0f).uv2(packedLight).endVertex();
    }

    private void setupLeashRendering(T entity, float partialTicks) {
        List<LeashState> collector = new ArrayList<>();

        if (entity instanceof Leashable leashable) {
            Entity leashHolder = leashable.raspberry$getLeashHolder();
            if (leashHolder != null) {
                addLeashStates(entity, leashable, leashHolder, partialTicks, collector, false);
            }
        }

        if (entity instanceof KnotConnectionAccess access && entity instanceof Leashable leashableSelf) {
            List<LeashFenceKnotEntity> connectedKnots = access.raspberry$getConnectionManager()
                .getConnectedKnots((LeashFenceKnotEntity) entity);
            for (LeashFenceKnotEntity targetKnot : connectedKnots) {
                if (entity.getUUID().compareTo(targetKnot.getUUID()) < 0) {
                    addLeashStates(entity, leashableSelf, targetKnot, partialTicks, collector, true);
                }
            }
        }

        this.leashStates = collector.isEmpty() ? null : collector;
    }

    private void addLeashStates(T entity, Leashable leashable, Entity target, float partialTicks, 
                                List<LeashState> collector, boolean isKnotToKnot) {
        float entityRotation = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) * ((float) Math.PI / 180);
        Vec3 leashOffset = leashable.raspberry$getLeashOffset(partialTicks);

        BlockPos entityPos = new BlockPos(entity.getEyePosition(partialTicks));
        BlockPos holderPos = new BlockPos(target.getEyePosition(partialTicks));
        int entityBlockLight = this.getBlockLightLevel(entity, entityPos);
        int holderBlockLight = this.getBlockLightLevel(target, holderPos);
        int entitySkyLight = entity.level.getBrightness(LightLayer.SKY, entityPos);
        int holderSkyLight = entity.level.getBrightness(LightLayer.SKY, holderPos);

        Vec3 rotatedOffset = leashOffset.yRot(-entityRotation);
        LeashState leashState = new LeashState();
        leashState.offset = rotatedOffset;
        leashState.start = entity.getPosition(partialTicks).add(rotatedOffset);
        
        if (isKnotToKnot && target instanceof Leashable targetLeashable) {
             float targetRotation = Mth.lerp(partialTicks, target.yRotO, target.getYRot()) * ((float) Math.PI / 180);
             Vec3 targetOffset = targetLeashable.raspberry$getLeashOffset(partialTicks).yRot(-targetRotation);
             leashState.end = target.getPosition(partialTicks).add(targetOffset);
        } else {
             leashState.end = target.getRopeHoldPosition(partialTicks);
        }
        
        leashState.startBlockLight = entityBlockLight;
        leashState.endBlockLight = holderBlockLight;
        leashState.startSkyLight = entitySkyLight;
        leashState.endSkyLight = holderSkyLight;
        leashState.slack = isKnotToKnot;
        leashState.isKnotToKnot = isKnotToKnot;
        collector.add(leashState);
    }

    private int getBlockLightLevel(Entity entity, BlockPos pos) {
        return ((EntityRendererAccessor) this.dispatcher.getRenderer(entity)).callGetBlockLightLevel(entity, pos);
    }
}