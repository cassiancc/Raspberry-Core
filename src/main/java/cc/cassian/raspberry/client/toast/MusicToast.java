package cc.cassian.raspberry.client.toast;

import cc.cassian.raspberry.client.music.MusicHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MusicToast implements Toast {
    private final MusicHandler.MusicMetadata music;
    private final ItemStack iconItem;
    private final ResourceLocation iconTexture;

    public MusicToast(MusicHandler.MusicMetadata music, ItemStack icon) {
        this.music = music;
        this.iconItem = icon;
        this.iconTexture = null;
    }

    public MusicToast(MusicHandler.MusicMetadata music, ResourceLocation icon) {
        this.music = music;
        this.iconItem = null;
        this.iconTexture = icon;
    }

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent toastComponent, long timeSinceLastVisible) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        toastComponent.blit(poseStack, 0, 0, 0, 0, this.width(), this.height());

        if (iconTexture != null) {
            int iconSize = 20;
            int iconX = 8;
            int iconY = 6;

            RenderSystem.setShaderTexture(0, iconTexture);
            GuiComponent.blit(poseStack, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        } else if (iconItem != null) {
            toastComponent.getMinecraft().getItemRenderer().renderAndDecorateItem(iconItem, 8, 8);
        }

        int textLeft = 30;
        boolean hasAuthor = !music.author().getString().isEmpty();

        if (hasAuthor) {
            toastComponent.getMinecraft().font.draw(poseStack, music.title(), textLeft, 7, 0xFFFFFF00);
            toastComponent.getMinecraft().font.draw(poseStack, music.author(), textLeft, 18, 0xFFFFFFFF);
        } else {
            toastComponent.getMinecraft().font.draw(poseStack, music.title(), textLeft, 12, 0xFFFFFF00);
        }

        return timeSinceLastVisible >= 5000L ? Visibility.HIDE : Visibility.SHOW;
    }
}