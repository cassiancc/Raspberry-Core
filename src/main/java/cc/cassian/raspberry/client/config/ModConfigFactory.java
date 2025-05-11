package cc.cassian.raspberry.client.config;

import cc.cassian.raspberry.config.ClothConfigFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ModConfigFactory {
    public static Screen createScreen(Minecraft arg, Screen parent) {
        return ClothConfigFactory.create(parent);
    }
}
