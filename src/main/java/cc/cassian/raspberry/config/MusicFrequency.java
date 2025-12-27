package cc.cassian.raspberry.config;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MusicFrequency implements StringRepresentable {
    DEFAULT("Default"),
    FREQUENT("Frequent"),
    CONSTANT("Constant");

    private final String name;

    MusicFrequency(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }

    public Component getDisplayName() {
        return Component.literal(this.name);
    }
}