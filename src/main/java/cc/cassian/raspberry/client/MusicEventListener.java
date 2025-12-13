package cc.cassian.raspberry.client;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.client.music.MusicHandler;
import cc.cassian.raspberry.client.toast.MusicToast;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;

public class MusicEventListener implements SoundEventListener {
    private static final ResourceLocation GENERIC_ICON = new ResourceLocation("raspberry", "textures/gui/generic_icon.png");

    @Override
    public void onPlaySound(SoundInstance sound, WeighedSoundEvents soundSet) {
        if (!ModConfig.get().showMusicToast) return;

        if (sound.getSource() != SoundSource.MUSIC && sound.getSource() != SoundSource.RECORDS) {
            return;
        }

        RaspberryMod.LOGGER.info("Music detected: " + sound.getSound().getLocation());

        if (sound.getSource() == SoundSource.RECORDS) {
            RecordItem discItem = findDiscBySound(sound);
            
            if (discItem == null) {
                return;
            }

            ItemStack icon = new ItemStack(discItem);
            MusicHandler.MusicMetadata metadata = MusicHandler.getDiscInfo(discItem);
            
            Minecraft.getInstance().getToasts().addToast(new MusicToast(metadata, icon));
            
        } else {
            MusicHandler.MusicMetadata metadata = MusicHandler.getMusicInfo(sound.getSound().getLocation());
            Minecraft.getInstance().getToasts().addToast(new MusicToast(metadata, GENERIC_ICON));
        }
    }

    private RecordItem findDiscBySound(SoundInstance sound) {
        ResourceLocation playingLocation = sound.getLocation();

        if (playingLocation.getNamespace().equals("etched")) {
            try {
                Object innerSound = sound.getSound();
                if (innerSound != null && innerSound.getClass().getName().equals("gg.moonflower.etched.api.sound.AbstractOnlineSoundInstance$OnlineSound")) {
                    Method getUrlMethod = innerSound.getClass().getMethod("getURL");
                    String url = (String) getUrlMethod.invoke(innerSound);

                    if (url != null && !url.contains("://")) {
                        playingLocation = new ResourceLocation(url);
                    }
                }
            } catch (Exception e) {
                RaspberryMod.LOGGER.debug("Failed to extract Etched sound URL", e);
            }
        }

        for (RecordItem disc : ForgeRegistries.ITEMS.getValues().stream()
                .filter(i -> i instanceof RecordItem)
                .map(i -> (RecordItem) i)
                .toList()) {
            if (disc.getSound().getLocation().equals(playingLocation)) {
                return disc;
            }
        }
        return null;
    }
}