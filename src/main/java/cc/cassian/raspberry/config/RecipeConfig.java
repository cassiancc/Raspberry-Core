package cc.cassian.raspberry.config;

import cc.cassian.raspberry.RaspberryMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class RecipeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("raspberry-recipes.json");

    public static JsonObject load() {
        if (!CONFIG_PATH.toFile().exists()) {
            createDefault();
        }
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);

            if (json == null) {
                RaspberryMod.LOGGER.warn("Recipe config file was empty: {}", CONFIG_PATH);
                return new JsonObject();
            }

            return json;
        } catch (JsonSyntaxException e) {
            RaspberryMod.LOGGER.error("Malformed JSON in recipe config: {}", CONFIG_PATH);
            RaspberryMod.LOGGER.error("Please check your syntax. Changes will not be applied.", e);
            return new JsonObject();
        } catch (IOException e) {
            RaspberryMod.LOGGER.error("Failed to read recipe config: {}", CONFIG_PATH, e);
            return new JsonObject();
        }
    }

    private static void createDefault() {
        JsonObject root = new JsonObject();
        JsonArray modifications = new JsonArray();

        JsonObject removeExample = new JsonObject();
        removeExample.addProperty("action", "remove");
        JsonObject filter = new JsonObject();
        filter.addProperty("output", "minecraft:stone_pickaxe");
        removeExample.add("filter", filter);

        modifications.add(removeExample);
        root.add("modifications", modifications);

        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            RaspberryMod.LOGGER.error("Failed to create default recipe config: {}", CONFIG_PATH, e);
        }
    }
}