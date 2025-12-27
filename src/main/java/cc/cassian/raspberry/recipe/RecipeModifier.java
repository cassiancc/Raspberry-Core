package cc.cassian.raspberry.recipe;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.config.RecipeConfig;
import cc.cassian.raspberry.mixin.accessor.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class RecipeModifier {
    public static int errorCount = 0;

    public static void apply(RecipeManager manager) {
        errorCount = 0;

        JsonObject config = RecipeConfig.load();
        if (!config.has("modifications")) return;

        JsonArray modifications = config.getAsJsonArray("modifications");
        List<Recipe<?>> recipesToRemove = new ArrayList<>();

        RecipeManagerAccessor managerAccessor = (RecipeManagerAccessor) manager;

        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesByType = new HashMap<>(managerAccessor.getRecipes());
        Map<ResourceLocation, Recipe<?>> recipesByName = new HashMap<>(managerAccessor.getByName());

        for (JsonElement element : modifications) {
            if (!element.isJsonObject()) continue;
            JsonObject mod = element.getAsJsonObject();
            String action = mod.get("action").getAsString();
            JsonObject filter = mod.getAsJsonObject("filter");

            try {
                switch (action) {
                    case "remove" -> {
                        for (Recipe<?> recipe : recipesByName.values()) {
                            if (matchesFilter(recipe, filter)) {
                                recipesToRemove.add(recipe);
                            }
                        }
                    }
                    case "replace_input" -> {
                        Ingredient targetIngredient = parseIngredient(mod.get("target"));
                        Ingredient newIngredient = parseIngredient(mod.get("replacement"));

                        if (isEmptyOrInvalid(targetIngredient) || isEmptyOrInvalid(newIngredient)) continue;

                        for (Recipe<?> recipe : recipesByName.values()) {
                            if (matchesFilter(recipe, filter)) {
                                replaceInputInRecipe(recipe, targetIngredient, newIngredient);
                            }
                        }
                    }
                    case "replace_output" -> {
                        String replacement = mod.get("replacement").getAsString();
                        ResourceLocation id = tryParseId(replacement);
                        if (id == null) {
                            errorCount++;
                            continue;
                        }

                        Item item = ForgeRegistries.ITEMS.getValue(id);
                        if (item == null || item == Items.AIR) {
                            RaspberryMod.LOGGER.warn("Skipping replace_output for unknown item: {}", replacement);
                            continue;
                        }

                        ItemStack newResult = new ItemStack(item);

                        for (Recipe<?> recipe : recipesByName.values()) {
                            if (matchesFilter(recipe, filter)) {
                                replaceOutputInRecipe(recipe, newResult);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                errorCount++;
                RaspberryMod.LOGGER.error("Error processing modification entry: {}", mod, e);
            }
        }

        for (Recipe<?> recipe : recipesToRemove) {
            recipesByName.remove(recipe.getId());
            Map<ResourceLocation, Recipe<?>> typeMap = recipesByType.get(recipe.getType());
            if (typeMap != null) {
                if (!(typeMap instanceof HashMap)) {
                    typeMap = new HashMap<>(typeMap);
                    recipesByType.put(recipe.getType(), typeMap);
                }
                typeMap.remove(recipe.getId());
            }
        }

        managerAccessor.setRecipes(recipesByType);
        managerAccessor.setByName(recipesByName);

        if (errorCount > 0) {
            RaspberryMod.LOGGER.warn("Recipe Modifier finished with {} errors. Check logs for details.", errorCount);
        }
    }

    private static boolean matchesFilter(Recipe<?> recipe, JsonObject filter) {
        for (String key : filter.keySet()) {
            JsonElement criterion = filter.get(key);
            boolean match = switch (key) {
                case "not" -> {
                    if (criterion.isJsonArray()) {
                        boolean anyMatch = false;
                        for (JsonElement e : criterion.getAsJsonArray()) {
                            if (matchesFilter(recipe, e.getAsJsonObject())) {
                                anyMatch = true;
                                break;
                            }
                        }
                        yield !anyMatch;
                    }
                    yield !matchesFilter(recipe, criterion.getAsJsonObject());
                }
                case "or" -> {
                    boolean any = false;
                    for (JsonElement e : criterion.getAsJsonArray()) {
                        if (matchesFilter(recipe, e.getAsJsonObject())) {
                            any = true;
                            break;
                        }
                    }
                    yield any;
                }
                case "and" -> {
                    boolean all = true;
                    for (JsonElement e : criterion.getAsJsonArray()) {
                        if (!matchesFilter(recipe, e.getAsJsonObject())) {
                            all = false;
                            break;
                        }
                    }
                    yield all;
                }
                case "type" -> matchStringOrRegexOrArray(criterion, recipe.getType().toString());
                case "mod" -> matchStringOrRegexOrArray(criterion, recipe.getId().getNamespace());
                case "id" -> matchStringOrRegexOrArray(criterion, recipe.getId().toString());
                case "output" -> matchOutput(criterion, recipe);
                case "input" -> matchInput(criterion, recipe);
                default -> true;
            };

            if (!match) return false;
        }
        return true;
    }

    private static boolean matchOutput(JsonElement criterion, Recipe<?> recipe) {
        try {
            ItemStack result = recipe.getResultItem(RegistryAccess.EMPTY);
            if (result.isEmpty()) return false;
            ResourceLocation key = ForgeRegistries.ITEMS.getKey(result.getItem());
            if (key == null) return false;
            return matchStringOrRegexOrArray(criterion, key.toString());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchInput(JsonElement criterion, Recipe<?> recipe) {
        if (criterion.isJsonArray()) {
            for (JsonElement e : criterion.getAsJsonArray()) {
                try {
                    if (matchInput(e, recipe)) return true;
                } catch (Exception ignored) {
                }
            }
            return false;
        }

        Ingredient target = parseIngredient(criterion);
        if (isEmptyOrInvalid(target)) return false;

        for (Ingredient ing : recipe.getIngredients()) {
            if (ingredientMatches(ing, target)) return true;
        }
        return false;
    }

    private static boolean matchStringOrRegexOrArray(JsonElement element, String value) {
        if (element.isJsonPrimitive()) {
            String filterStr = element.getAsString();
            if (filterStr.startsWith("/") && filterStr.endsWith("/") && filterStr.length() > 2) {
                String regex = filterStr.substring(1, filterStr.length() - 1);
                return value.matches(regex);
            }
            return value.equals(filterStr);
        } else if (element.isJsonArray()) {
            for (JsonElement e : element.getAsJsonArray()) {
                if (matchStringOrRegexOrArray(e, value)) return true;
            }
        }
        return false;
    }

    private static void replaceInputInRecipe(Recipe<?> recipe, Ingredient target, Ingredient replacement) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            Ingredient ing = recipe.getIngredients().get(i);
            if (ingredientMatches(ing, target)) {
                recipe.getIngredients().set(i, replacement);
            }
        }
        if (recipe instanceof SingleItemRecipe single) {
            if (ingredientMatches(single.getIngredients().get(0), target)) {
                ((SingleItemRecipeAccessor) single).setIngredient(replacement);
            }
        }
        if (recipe instanceof AbstractCookingRecipe cooking) {
            if (ingredientMatches(cooking.getIngredients().get(0), target)) {
                ((AbstractCookingRecipeAccessor) cooking).setIngredient(replacement);
            }
        }
    }

    private static void replaceOutputInRecipe(Recipe<?> recipe, ItemStack newResult) {
        if (recipe instanceof ShapedRecipe shaped) {
            ((ShapedRecipeAccessor) shaped).setResult(newResult);
        } else if (recipe instanceof ShapelessRecipe shapeless) {
            ((ShapelessRecipeAccessor) shapeless).setResult(newResult);
        } else if (recipe instanceof AbstractCookingRecipe cooking) {
            ((AbstractCookingRecipeAccessor) cooking).setResult(newResult);
        } else if (recipe instanceof SingleItemRecipe single) {
            ((SingleItemRecipeAccessor) single).setResult(newResult);
        }
    }

    private static boolean ingredientMatches(Ingredient ing, Ingredient target) {
        if (ing == null || ing.isEmpty()) return false;
        if (target == null || target.isEmpty()) return false;

        try {
            Ingredient.Value[] values = ((IngredientAccessor) ing).getValues();
            Ingredient.Value[] targetValues = ((IngredientAccessor) target).getValues();

            for (Ingredient.Value targetVal : targetValues) {
                for (Ingredient.Value val : values) {
                    if (valuesMatch(val, targetVal)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static boolean valuesMatch(Ingredient.Value a, Ingredient.Value b) {
        if (a instanceof IngredientTagValueAccessor tagA && b instanceof IngredientTagValueAccessor tagB) {
            return tagA.getTag().equals(tagB.getTag());
        }
        if (a instanceof IngredientItemValueAccessor itemA && b instanceof IngredientItemValueAccessor itemB) {
            return itemA.getItem().getItem().equals(itemB.getItem().getItem());
        }
        if (a instanceof IngredientItemValueAccessor itemA && b instanceof IngredientTagValueAccessor tagB) {
            return itemA.getItem().is(tagB.getTag());
        }
        return false;
    }

    private static Ingredient parseIngredient(JsonElement json) {
        if (json.isJsonArray()) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (JsonElement e : json.getAsJsonArray()) {
                Ingredient ing = parseIngredientString(e.getAsString());
                if (!isEmptyOrInvalid(ing)) {
                    ingredients.add(ing);
                }
            }
            if (ingredients.isEmpty()) return Ingredient.EMPTY;
            return Ingredient.merge(ingredients);
        }
        return parseIngredientString(json.getAsString());
    }

    private static Ingredient parseIngredientString(String str) {
        if (str.startsWith("#")) {
            ResourceLocation id = tryParseId(str.substring(1));
            if (id != null) {
                TagKey<Item> tag = TagKey.create(Registries.ITEM, id);
                return Ingredient.of(tag);
            }
            return Ingredient.EMPTY;
        }

        ResourceLocation id = tryParseId(str);
        if (id != null && ForgeRegistries.ITEMS.containsKey(id)) {
            Item item = ForgeRegistries.ITEMS.getValue(id);
            return Ingredient.of(item);
        }

        return Ingredient.EMPTY;
    }

    private static ResourceLocation tryParseId(String str) {
        try {
            return new ResourceLocation(str);
        } catch (Exception e) {
            RaspberryMod.LOGGER.warn("Invalid ResourceLocation in recipe config: {}", str);
            return null;
        }
    }

    private static boolean isEmptyOrInvalid(Ingredient ing) {
        return ing == null || ing.isEmpty() || (ing.getItems().length == 0) || (ing.getItems().length == 1 && ing.getItems()[0].getItem() == Items.AIR);
    }
}