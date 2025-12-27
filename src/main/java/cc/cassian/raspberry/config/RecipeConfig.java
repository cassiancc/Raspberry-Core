package cc.cassian.raspberry.config;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.recipe.RecipeRule;
import com.google.gson.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RecipeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("raspberry-recipes.json");

    public static List<RecipeRule> loadRules() {
        List<RecipeRule> rules = new ArrayList<>();
        JsonObject config = loadJson();

        if (config == null || !config.has("modifications")) return rules;

        JsonArray modifications = config.getAsJsonArray("modifications");

        for (JsonElement element : modifications) {
            try {
                if (!element.isJsonObject()) continue;
                JsonObject mod = element.getAsJsonObject();
                rules.add(parseRule(mod));
            } catch (Exception e) {
                RaspberryMod.LOGGER.error("Failed to parse recipe rule: {}", element);
            }
        }
        return rules;
    }

    private static RecipeRule parseRule(JsonObject mod) {
        String actionStr = mod.has("action") ? mod.get("action").getAsString() : "unknown";
        if (!mod.has("filter")) throw new IllegalArgumentException("Missing filter");

        Predicate<Recipe<?>> filter = parseFilter(mod.get("filter"));

        return switch (actionStr) {
            case "remove" -> new RecipeRule(RecipeRule.Action.REMOVE, filter);
            case "replace_input" -> {
                Ingredient target = parseIngredient(mod.get("target"));
                Ingredient replace = parseIngredient(mod.get("replacement"));
                yield new RecipeRule(RecipeRule.Action.REPLACE_INPUT, filter, target, replace);
            }
            case "replace_output" -> {
                String idStr = mod.get("replacement").getAsString();
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(idStr));
                if (item == null || item == Items.AIR) throw new IllegalArgumentException("Invalid replacement item: " + idStr);
                yield new RecipeRule(RecipeRule.Action.REPLACE_OUTPUT, filter, new ItemStack(item));
            }
            default -> throw new IllegalArgumentException("Unknown action: " + actionStr);
        };
    }

    private static Predicate<Recipe<?>> parseFilter(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();

            if (obj.has("not")) return parseFilter(obj.get("not")).negate();

            if (obj.has("or")) {
                Predicate<Recipe<?>> p = r -> false;
                for (JsonElement e : obj.getAsJsonArray("or")) p = p.or(parseFilter(e));
                return p;
            }

            if (obj.has("and")) {
                Predicate<Recipe<?>> p = r -> true;
                for (JsonElement e : obj.getAsJsonArray("and")) p = p.and(parseFilter(e));
                return p;
            }

            Predicate<Recipe<?>> combined = r -> true;
            for (String key : obj.keySet()) {
                JsonElement criterion = obj.get(key);
                Predicate<Recipe<?>> check = switch (key) {
                    case "type" -> r -> getStringMatcher(criterion).test(r.getType().toString());
                    case "mod" -> r -> getStringMatcher(criterion).test(r.getId().getNamespace());
                    case "id" -> r -> getStringMatcher(criterion).test(r.getId().toString());
                    case "output" -> r -> {
                        try {
                            ItemStack out = r.getResultItem(RegistryAccess.EMPTY);
                            if (out.isEmpty()) return false;
                            ResourceLocation id = ForgeRegistries.ITEMS.getKey(out.getItem());
                            return id != null && getStringMatcher(criterion).test(id.toString());
                        } catch (Exception e) {
                            return false;
                        }
                    };
                    default -> r -> true;
                };
                combined = combined.and(check);
            }
            return combined;
        }
        return r -> true;
    }

    private static Predicate<String> getStringMatcher(JsonElement element) {
        if (element.isJsonArray()) {
            Predicate<String> p = s -> false;
            for (JsonElement e : element.getAsJsonArray()) p = p.or(getStringMatcher(e));
            return p;
        }
        String str = element.getAsString();
        if (str.startsWith("/") && str.endsWith("/") && str.length() > 2) {
            Pattern pattern = Pattern.compile(str.substring(1, str.length() - 1));
            return s -> pattern.matcher(s).matches();
        }
        return str::equals;
    }

    private static JsonObject loadJson() {
        if (!CONFIG_PATH.toFile().exists()) createDefault();
        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            return GSON.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            RaspberryMod.LOGGER.error("Failed to load recipe config JSON", e);
            return null;
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

    private static Ingredient parseIngredient(JsonElement json) {
        if (json == null) return Ingredient.EMPTY;
        if (json.isJsonArray()) {
            List<Ingredient> list = new ArrayList<>();
            json.getAsJsonArray().forEach(e -> list.add(parseIngredientString(e.getAsString())));
            return Ingredient.merge(list);
        }
        return parseIngredientString(json.getAsString());
    }

    private static Ingredient parseIngredientString(String str) {
        if (str.startsWith("#")) {
            return Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation(str.substring(1))));
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str));
        return (item != null && item != Items.AIR) ? Ingredient.of(item) : Ingredient.EMPTY;
    }
}