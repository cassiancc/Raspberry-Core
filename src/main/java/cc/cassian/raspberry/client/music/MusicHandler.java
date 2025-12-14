package cc.cassian.raspberry.client.music;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cc.cassian.raspberry.RaspberryMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.RecordItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicHandler implements ResourceManagerReloadListener {
    private static final Map<ResourceLocation, MusicMetadata> MUSIC_DB = new HashMap<>();
    private static final ResourceLocation DATA_LOCATION = new ResourceLocation(RaspberryMod.MOD_ID, "musics.json");

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        MUSIC_DB.clear();

        List<Resource> resources = resourceManager.getResourceStack(DATA_LOCATION);
        for (Resource resource : resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                JsonObject json = GsonHelper.parse(reader);
                for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                    ResourceLocation id = new ResourceLocation(entry.getKey());
                    JsonObject data = entry.getValue().getAsJsonObject();
                    
                    String title = GsonHelper.getAsString(data, "title", "Unknown");
                    String author = GsonHelper.getAsString(data, "author", "");
                    
                    MUSIC_DB.put(id, new MusicMetadata(Component.literal(title), Component.literal(author)));
                }
            } catch (RuntimeException | IOException e) {
                RaspberryMod.LOGGER.error("Failed to load music data from {}", resource.sourcePackId(), e);
            }
        }
    }

    public static MusicMetadata getMusicInfo(ResourceLocation location) {
        if (MUSIC_DB.containsKey(location)) {
            return MUSIC_DB.get(location);
        }
        
        String path = location.getPath();
        if (path.contains("/")) path = path.substring(path.lastIndexOf('/') + 1);
        if (path.contains(".")) path = path.substring(0, path.lastIndexOf('.'));
        
        return new MusicMetadata(Component.literal(beautifyName(path)), Component.empty());
    }

    public static MusicMetadata getDiscInfo(RecordItem disc) {
        String descKey = disc.getDescriptionId() + ".desc";
        String fullDescription = Component.translatable(descKey).getString();

        String[] split = fullDescription.split(" - ");
        if (split.length >= 2) {
            return new MusicMetadata(Component.literal(split[1]), Component.literal(split[0]));
        } else {
            return new MusicMetadata(disc.getDescription(), Component.empty());
        }
    }

    private static String beautifyName(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] words = input.split("[_.\\s]+");
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

    public record MusicMetadata(Component title, Component author) {}
}