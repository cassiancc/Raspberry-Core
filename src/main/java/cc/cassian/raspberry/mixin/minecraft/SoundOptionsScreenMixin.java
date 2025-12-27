package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.config.MusicFrequency;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundOptionsScreen.class)
public abstract class SoundOptionsScreenMixin extends OptionsSubScreen {

    @Unique
    private int raspberry$frequencyButtonY = -1;

    public SoundOptionsScreenMixin(Screen parent, Options options, Component title) {
        super(parent, options, title);
    }

    @WrapOperation(method = "init", at = @At(value = "NEW", target = "net/minecraft/client/gui/components/Button"))
    private Button moveDoneButton(int x, int y, int width, int height, Component message, Button.OnPress onPress, Operation<Button> original) {
        this.raspberry$frequencyButtonY = y;
        return original.call(x, y + 24, width, height, message, onPress);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addMusicControls(CallbackInfo ci) {
        AbstractWidget voiceButton = null;
        String voiceLabel = Component.translatable("soundCategory.voice").getString();

        for (GuiEventListener child : this.children()) {
            if (child instanceof AbstractWidget widget) {
                if (widget.getMessage().getString().contains(voiceLabel)) {
                    voiceButton = widget;
                    break;
                }
            }
        }

        if (voiceButton != null) {
            int sliderWidth = 150;
            int sliderX = voiceButton.x + voiceButton.getWidth() + 10;
            int sliderY = voiceButton.y;

            this.addRenderableWidget(new ForgeSlider(
                    sliderX, sliderY, sliderWidth, 20,
                    Component.literal("Jukebox Distance: "),
                    Component.empty(),
                    0.0, 256.0,
                    ModConfig.get().jukeboxDistance,
                    4.0,
                    0,
                    true
            ) {
                @Override
                protected void applyValue() {
                    ModConfig.get().jukeboxDistance = this.getValue();
                    ModConfig.save();
                }
            });
        }

        if (this.raspberry$frequencyButtonY != -1) {
            int leftButtonX = this.width / 2 - 155;
            int rightButtonX = this.width / 2 + 5;

            this.addRenderableWidget(CycleButton.builder(MusicFrequency::getDisplayName)
                    .withValues(MusicFrequency.values())
                    .withInitialValue(ModConfig.get().musicFrequency)
                    .create(leftButtonX, this.raspberry$frequencyButtonY, 150, 20, Component.literal("Music Frequency"), (button, value) -> {
                        ModConfig.get().musicFrequency = value;
                        ModConfig.save();
                    }));

            this.addRenderableWidget(CycleButton.onOffBuilder(ModConfig.get().showMusicToast)
                    .create(rightButtonX, this.raspberry$frequencyButtonY, 150, 20, Component.literal("Music Toast"), (button, value) -> {
                        ModConfig.get().showMusicToast = value;
                        ModConfig.save();
                    }));
        }
    }
}