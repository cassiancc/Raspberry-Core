package cc.cassian.raspberry.fluids;

import cc.cassian.raspberry.RaspberryMod;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class MoltenFluidExtensions implements IClientFluidTypeExtensions {
	private final String name;

	public MoltenFluidExtensions(String name) {
		this.name = name;
	}

	@Override
	public ResourceLocation getStillTexture() {
		return RaspberryMod.locate("fluid/%s".formatted(name));
	}

	@Override
	public ResourceLocation getFlowingTexture() {
		return RaspberryMod.locate("fluid/%s_flowing".formatted(name));
	}

	@Override
	public ResourceLocation getOverlayTexture() {
		return RaspberryMod.locate("fluid/%s_flowing".formatted(name));
	}

	@Override
	public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
		return new Vector3f(57 / 255F, 25 / 255F, 80 / 255F);
	}

	@Override
	public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
		RenderSystem.setShaderFogStart(0.0F);
		RenderSystem.setShaderFogEnd(3.0F);
	}
}
