package cc.cassian.raspberry.config;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.recipe.RecipeRule;
import cc.cassian.raspberry.recipe.TagRule;
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

    public static List<TagRule> loadTagRules() {
        List<TagRule> rules = new ArrayList<>();
        JsonObject config = loadJson();

        if (config == null || !config.has("tag_modifications")) return rules;

        JsonArray modifications = config.getAsJsonArray("tag_modifications");

        for (JsonElement element : modifications) {
            try {
                if (!element.isJsonObject()) continue;
                JsonObject mod = element.getAsJsonObject();
                rules.add(parseTagRule(mod));
            } catch (Exception e) {
                RaspberryMod.LOGGER.error("Failed to parse tag rule: {}", element);
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

    private static TagRule parseTagRule(JsonObject mod) {
        String actionStr = mod.has("action") ? mod.get("action").getAsString() : "unknown";

        List<ResourceLocation> items = new ArrayList<>();
        if (mod.has("items")) {
            JsonElement itemsEl = mod.get("items");
            if (itemsEl.isJsonArray()) {
                itemsEl.getAsJsonArray().forEach(e -> {
                    try {
                        items.add(new ResourceLocation(e.getAsString()));
                    } catch (Exception ex) {
                        RaspberryMod.LOGGER.warn("Skipping invalid item ID in tag rule: {}", e.getAsString());
                    }
                });
            } else {
                try {
                    items.add(new ResourceLocation(itemsEl.getAsString()));
                } catch (Exception ex) {
                    RaspberryMod.LOGGER.warn("Skipping invalid item ID in tag rule: {}", itemsEl.getAsString());
                }
            }
        }

        List<ResourceLocation> tags = new ArrayList<>();
        if (mod.has("tags")) {
            mod.get("tags").getAsJsonArray().forEach(e -> {
                try {
                    tags.add(new ResourceLocation(e.getAsString()));
                } catch (Exception ex) {
                    RaspberryMod.LOGGER.warn("Skipping invalid tag ID in tag rule: {}", e.getAsString());
                }
            });
        } else if (mod.has("tag")) {
            try {
                tags.add(new ResourceLocation(mod.get("tag").getAsString()));
            } catch (Exception ex) {
                RaspberryMod.LOGGER.warn("Skipping invalid tag ID in tag rule: {}", mod.get("tag").getAsString());
            }
        }

        return switch (actionStr) {
            case "remove_all_tags" -> new TagRule(TagRule.Action.REMOVE_ALL_TAGS, items, null);
            case "remove_from_tag" -> {
                if (tags.isEmpty()) throw new IllegalArgumentException("Missing valid tag(s) for remove_from_tag action");
                yield new TagRule(TagRule.Action.REMOVE_FROM_TAG, items, tags);
            }
            case "clear_tag" -> {
                if (tags.isEmpty()) throw new IllegalArgumentException("Missing valid tag(s) for clear_tag action");
                yield new TagRule(TagRule.Action.CLEAR_TAG, null, tags);
            }
            default -> throw new IllegalArgumentException("Unknown tag action: " + actionStr);
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
                    case "input" -> r -> {
                        Predicate<String> matcher = getStringMatcher(criterion);
                        return r.getIngredients().stream().anyMatch(ing -> {
                            for (ItemStack stack : ing.getItems()) {
                                ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
                                if (id != null && matcher.test(id.toString())) return true;
                            }
                            return false;
                        });
                    };
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
                    default -> throw new IllegalArgumentException("Unknown filter key: " + key);
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
            try {
                Pattern pattern = Pattern.compile(str.substring(1, str.length() - 1));
                return s -> pattern.matcher(s).matches();
            } catch (Exception e) {
                RaspberryMod.LOGGER.warn("Invalid regex pattern in filter: {}", str);
                return s -> false;
            }
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

        // Recipe Modifications
        JsonArray modifications = new JsonArray();
        JsonObject removeExample = new JsonObject();
        removeExample.addProperty("action", "remove");
        JsonObject filter = new JsonObject();
        filter.addProperty("output", "minecraft:stone_pickaxe");
        removeExample.add("filter", filter);
        modifications.add(removeExample);
        root.add("modifications", modifications);

        // Tag Modifications
        JsonArray tagModifications = new JsonArray();
        JsonObject tagRemoveExample = new JsonObject();
        tagRemoveExample.addProperty("action", "remove_all_tags");
        JsonArray items = new JsonArray();
        items.add("minecraft:wooden_hoe");
        tagRemoveExample.add("items", items);
        tagModifications.add(tagRemoveExample);
        root.add("tag_modifications", tagModifications);

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
            json.getAsJsonArray().forEach(e -> {
                try {
                    list.add(parseIngredientString(e.getAsString()));
                } catch (Exception ex) {
                    RaspberryMod.LOGGER.warn("Skipping invalid ingredient: {}", e.getAsString());
                }
            });
            return Ingredient.merge(list);
        }
        try {
            return parseIngredientString(json.getAsString());
        } catch (Exception e) {
            RaspberryMod.LOGGER.warn("Skipping invalid ingredient: {}", json.getAsString());
            return Ingredient.EMPTY;
        }
    }

    private static Ingredient parseIngredientString(String str) {
        if (str.startsWith("#")) {
            return Ingredient.of(TagKey.create(Registries.ITEM, new ResourceLocation(str.substring(1))));
        }
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(str));
        return (item != null && item != Items.AIR) ? Ingredient.of(item) : Ingredient.EMPTY;
    }
}