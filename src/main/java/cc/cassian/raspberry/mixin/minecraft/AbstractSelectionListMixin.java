package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.achievement.StatsScreen.GeneralStatisticsList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GeneralStatisticsList.Entry.class)
public abstract class AbstractSelectionListMixin extends net.minecraft.client.gui.components.ObjectSelectionList.Entry<GeneralStatisticsList.Entry> {
	@Shadow
	@Final
	private Stat<ResourceLocation> stat;

	@Inject(method = "render", at = @At(value = "TAIL"))
	private void hideStats(PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick, CallbackInfo ci) {
		if (ModConfig.get().infoTooltips && isMouseOver) {
			Minecraft.getInstance().screen.renderTooltip(poseStack, Component.literal(stat.getValue().toString()), mouseX, mouseY);
		}
	}
}
