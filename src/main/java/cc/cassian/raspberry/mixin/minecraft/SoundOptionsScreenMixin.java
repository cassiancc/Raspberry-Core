package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;
import java.util.List;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin {

    @ModifyReturnValue(method = "getAllSoundOptionsExceptMaster", at = @At("RETURN"))
    private OptionInstance<?>[] raspberry$appendJukeboxToSoundList(OptionInstance<?>[] original) {
        OptionInstance<Integer> jukeboxOption = new OptionInstance<>(
                "raspberry.options.jukebox",
                OptionInstance.noTooltip(),
                (label, val) -> Component.translatable("raspberry.options.jukebox").append(": " + val),
                new OptionInstance.IntRange(0, 256),
                (int) ModConfig.get().jukeboxDistance,
                value -> {
                    ModConfig.get().jukeboxDistance = value.doubleValue();
                    ModConfig.save();
                }
        );

        OptionInstance<?>[] newArray = Arrays.copyOf(original, original.length + 1);
        newArray[original.length] = jukeboxOption;

        return newArray;
    }

    @ModifyReturnValue(method = "buttonOptions", at = @At("RETURN"))
    private static OptionInstance<?>[] raspberry$appendMiscOptions(OptionInstance<?>[] original) {
        OptionInstance<MusicFrequency> frequencyOption = new OptionInstance<>(
                "raspberry.options.frequency",
                OptionInstance.noTooltip(),
                (label, value) -> value.getDisplayName(),
                new OptionInstance.Enum<>(List.of(MusicFrequency.values()), MusicFrequency.CODEC),
                ModConfig.get().musicFrequency,
                value -> {
                    ModConfig.get().musicFrequency = value;
                    ModConfig.save();
                }
        );

        OptionInstance<Boolean> toastOption = OptionInstance.createBoolean(
                "raspberry.options.toast",
                OptionInstance.cachedConstantTooltip(Component.literal("Show a toast when music starts playing")),
                (label, value) -> value ? Component.literal("ON") : Component.literal("OFF"),
                ModConfig.get().showMusicToast,
                value -> {
                    ModConfig.get().showMusicToast = value;
                    ModConfig.save();
                }
        );

        OptionInstance<?>[] newArray = Arrays.copyOf(original, original.length + 2);
        newArray[original.length] = frequencyOption;
        newArray[original.length + 1] = toastOption;

        return newArray;
    }
}