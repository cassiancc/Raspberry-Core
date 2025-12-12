package cc.cassian.raspberry.client;

import cc.cassian.raspberry.client.toast.MusicToast;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.ForgeRegistries;

public class MusicEventListener implements SoundEventListener {

    @Override
    public void onPlaySound(SoundInstance sound, WeighedSoundEvents soundSet) {
        if (!ModConfig.get().showMusicToast) return;

        if (sound.getSource() != SoundSource.MUSIC && sound.getSource() != SoundSource.RECORDS) {
            return;
        }

        String displayText = "";
        ItemStack icon = new ItemStack(Items.NOTE_BLOCK);

        if (sound.getSource() == SoundSource.RECORDS) {
            icon = new ItemStack(Items.JUKEBOX);
            RecordItem discItem = findDiscBySound(sound);
            
            if (discItem != null) {
                icon = new ItemStack(discItem);

                String descKey = discItem.getDescriptionId() + ".desc";
                String translated = Component.translatable(descKey).getString();
                
                if (!translated.equals(descKey)) {
                    displayText = translated;
                } else {
                    displayText = discItem.getDescription().getString();
                }
            } else {
                displayText = "Unknown Disc";
            }
        } 
        else {
            if (soundSet.getSubtitle() != null) {
                displayText = soundSet.getSubtitle().getString();
            } else {
                String path = sound.getLocation().getPath();
                path = path.replace("music/", "").replace("music.", "")
                           .replace("game/", "").replace("game.", "");
                
                if (path.contains("/")) path = path.substring(path.lastIndexOf('/') + 1);
                if (path.contains(".")) path = path.substring(path.lastIndexOf('.') + 1);

                displayText = beautifyName(path);
            }
        }

        Minecraft.getInstance().getToasts().addToast(new MusicToast(Component.literal(displayText), icon));
    }

    private String beautifyName(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] words = input.split("[_.]");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) sb.append(word.substring(1));
                sb.append(" ");
            }
        }
        return sb.toString().trim();
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