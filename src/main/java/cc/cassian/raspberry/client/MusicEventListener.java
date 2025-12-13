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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.ForgeRegistries;

public class MusicEventListener implements SoundEventListener {
    private static final ResourceLocation GENERIC_ICON = new ResourceLocation("raspberry", "textures/gui/generic_icon.png");

    @Override
    public void onPlaySound(SoundInstance sound, WeighedSoundEvents soundSet) {
        if (!ModConfig.get().showMusicToast) return;

        if (sound.getSource() != SoundSource.MUSIC && sound.getSource() != SoundSource.RECORDS) {
            return;
        }

        RaspberryMod.LOGGER.info("Music detected: " + sound.getSound().getLocation());

        MusicHandler.MusicMetadata metadata;
        
        if (sound.getSource() == SoundSource.RECORDS) {
            RecordItem discItem = findDiscBySound(sound);
            ItemStack icon = new ItemStack(Items.JUKEBOX);
            
            if (discItem != null) {
                icon = new ItemStack(discItem);
                metadata = MusicHandler.getDiscInfo(discItem);
            } else {
                metadata = new MusicHandler.MusicMetadata(net.minecraft.network.chat.Component.literal("Unknown Disc"), net.minecraft.network.chat.Component.empty());
            }
            
            Minecraft.getInstance().getToasts().addToast(new MusicToast(metadata, icon));
            
        } else {
            metadata = MusicHandler.getMusicInfo(sound.getSound().getLocation());
            
            Minecraft.getInstance().getToasts().addToast(new MusicToast(metadata, GENERIC_ICON));
        }
    }

    private RecordItem findDiscBySound(SoundInstance sound) {
        for (RecordItem disc : ForgeRegistries.ITEMS.getValues().stream()
                .filter(i -> i instanceof RecordItem)
                .map(i -> (RecordItem) i)
                .toList()) {
            if (disc.getSound().getLocation().equals(sound.getLocation())) {
                return disc;
            }
        }
        return null;
    }
}