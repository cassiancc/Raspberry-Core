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

    public MusicToast(Component text, ItemStack icon) {
        this.text = text;
        this.icon = icon;
    }

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent toastComponent, long timeSinceLastVisible) {
        if (timeSinceLastVisible > 5000) {
            return Visibility.HIDE;
        }

        float alpha = 1.0f;
        if (timeSinceLastVisible > 4000) {
            alpha = 1.0f - ((timeSinceLastVisible - 4000) / 1000f);
        }
        
        int alphaInt = (int) (alpha * 255) << 24;

        int x = 0; 
        int y = 0;
        int width = 160; 
        int height = 32; 

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int colorBg = alphaInt | 0x202020; 
        int colorBorder = alphaInt | 0xAAAAAA;

        GuiComponent.fill(poseStack, x, y, x + width, y + height, colorBg);
        
        GuiComponent.fill(poseStack, x, y, x + width, y + 1, colorBorder);
        GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, colorBorder);
        GuiComponent.fill(poseStack, x, y, x + 1, y + height, colorBorder);
        GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, colorBorder);

        if (icon != null) {
            poseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha); // Apply alpha to item
            poseStack.translate(x + 8, y + 8, 0);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(icon, 0, 0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset
            poseStack.popPose();
        }

        int textColor = alphaInt | 0xFFFFFF; 
        
        if (text.getString().contains(" - ")) {
            String[] parts = text.getString().split(" - ", 2);
            toastComponent.getMinecraft().font.draw(poseStack, parts[0], x + 30, y + 7, alphaInt | 0xFFFF00);
            toastComponent.getMinecraft().font.draw(poseStack, parts[1], x + 30, y + 18, textColor);
        } else {
            toastComponent.getMinecraft().font.draw(poseStack, text, x + 30, y + 12, textColor);
        }

        return Visibility.SHOW;
    }
}