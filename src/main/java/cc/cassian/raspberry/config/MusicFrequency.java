package cc.cassian.raspberry.config;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum MusicFrequency implements StringRepresentable {
    DEFAULT("Default", 1.0f),
    FREQUENT("Frequent", 0.5f),
    CONSTANT("Constant", 0.0f);

    private final String name;
    private final float delayMultiplier;

    MusicFrequency(String name, float delayMultiplier) {
        this.name = name;
        this.delayMultiplier = delayMultiplier;
    }

    public float getDelayMultiplier() {
        return delayMultiplier;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public Component getDisplayName() {
        return Component.literal(this.name);
    }
}