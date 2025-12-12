package cc.cassian.raspberry.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class MusicToast implements Toast {
    private final Component text;
    private final ItemStack icon;
    private long startTime;
    private boolean justUpdated;

    public MusicToast(Component text, ItemStack icon) {
        this.text = text;
        this.icon = icon;
        this.justUpdated = true;
    }

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent toastComponent, long timeSinceLastVisible) {
        if (this.justUpdated) {
            this.startTime = timeSinceLastVisible;
            this.justUpdated = false;
        }

        // Calculate opacity based on time (Fade out after 4 seconds)
        // timeSinceLastVisible counts up from 0 when shown
        long aliveTime = timeSinceLastVisible; 
        
        // Show for 5000ms total
        if (aliveTime > 5000) {
            return Visibility.HIDE;
        }

        float alpha = 1.0f;
        if (aliveTime > 4000) {
            alpha = 1.0f - ((aliveTime - 4000) / 1000f);
        }
        int alphaInt = (int) (alpha * 255) << 24;

        // Custom Rendering - Dark Grey Box
        int x = 0; 
        int y = 0;
        int width = 160; // Standard toast width
        int height = 32; // Standard toast height

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Background (Dark Grey: 0x202020 with alpha)
        int colorBg = alphaInt | 0x202020; 
        // Border (Light Grey: 0xAAAAAA with alpha)
        int colorBorder = alphaInt | 0xAAAAAA;

        // Draw Box
        GuiComponent.fill(poseStack, x, y, x + width, y + height, colorBg);
        
        // Draw Borders (Top, Bottom, Left, Right)
        GuiComponent.fill(poseStack, x, y, x + width, y + 1, colorBorder);
        GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, colorBorder);
        GuiComponent.fill(poseStack, x, y, x + 1, y + height, colorBorder);
        GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, colorBorder);

        // Draw Icon
        if (icon != null) {
            poseStack.pushPose();
            poseStack.translate(x + 8, y + 8, 0);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, 0, 0);
            poseStack.popPose();
        }

        // Draw Text (Centered vertically)
        int textColor = alphaInt | 0xFFFFFF; // White
        // Basic parsing to split Artist - Song if present for two lines, or keep one line
        String fullText = text.getString();
        if (fullText.contains(" - ")) {
            String[] parts = fullText.split(" - ", 2);
            // Artist (Yellow)
            toastComponent.getMinecraft().font.draw(poseStack, parts[0], x + 30, y + 7, alphaInt | 0xFFFF00);
            // Song (White)
            toastComponent.getMinecraft().font.draw(poseStack, parts[1], x + 30, y + 18, textColor);
        } else {
            // Single line centered
            toastComponent.getMinecraft().font.draw(poseStack, text, x + 30, y + 12, textColor);
        }

        RenderSystem.disableBlend();
        return Visibility.SHOW;
    }
}