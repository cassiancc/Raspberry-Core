package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin extends OptionsSubScreen {

    private int raspberry_frequencyButtonY = -1;

    public SoundOptionsScreenMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @WrapOperation(method = "init", at = @At(value = "NEW", target = "net/minecraft/client/gui/components/Button"))
    private Button moveDoneButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, Operation<Button> original) {
        this.raspberry_frequencyButtonY = y;
        return original.call(x, y + 24, width, height, message, onPress);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addMusicControls(CallbackInfo ci) {
        if (this.raspberry_frequencyButtonY != -1) {
            int leftButtonX = this.width / 2 - 155;
            int rightButtonX = this.width / 2 + 5;

            this.addRenderableWidget(CycleButton.builder(MusicFrequency::getDisplayName)
                    .withValues(MusicFrequency.values())
                    .withInitialValue(ModConfig.get().musicFrequency)
                    .create(leftButtonX, this.raspberry_frequencyButtonY, 150, 20, Component.literal("Frequency"), (button, value) -> {
                        ModConfig.get().musicFrequency = value;
                        ModConfig.save();
                    }));

            this.addRenderableWidget(CycleButton.onOffBuilder(ModConfig.get().showMusicToast)
                    .create(rightButtonX, this.raspberry_frequencyButtonY, 150, 20, Component.literal("Music Toast"), (button, value) -> {
                        ModConfig.get().showMusicToast = value;
                        ModConfig.save();
                    }));
        }
    }
}