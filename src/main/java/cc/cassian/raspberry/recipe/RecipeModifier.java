package cc.cassian.raspberry.recipe;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.ItemObliteratorCompat;
import cc.cassian.raspberry.config.RecipeConfig;
import cc.cassian.raspberry.mixin.accessor.*;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;
import java.util.function.Predicate;

public class RecipeModifier {

    public static void apply(RecipeManager manager) {
        int lastErrorCount = 0;

        List<RecipeRule> rules = RecipeConfig.loadRules();

        List<Predicate<ItemStack>> hiddenItemStrategies = new ArrayList<>();
        if (ModCompat.ITEM_OBLITERATOR) {
            hiddenItemStrategies.add(ItemObliteratorCompat::shouldHide);
        }

        if (rules.isEmpty() && hiddenItemStrategies.isEmpty()) return;

        RecipeManagerAccessor managerAccessor = (RecipeManagerAccessor) manager;
        Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipesByType = new HashMap<>(managerAccessor.getRecipes());
        Map<ResourceLocation, Recipe<?>> recipesByName = new HashMap<>(managerAccessor.getByName());

        List<ResourceLocation> toRemove = new ArrayList<>();

        for (Recipe<?> recipe : recipesByName.values()) {
            try {
                boolean shouldRemove = false;

                if (!hiddenItemStrategies.isEmpty()) {
                    ItemStack result = getResult(recipe);
                    if (!result.isEmpty()) {
                        for (Predicate<ItemStack> strategy : hiddenItemStrategies) {
                            if (strategy.test(result)) {
                                shouldRemove = true;
                                break;
                            }
                        }
                    }
                }

                if (!shouldRemove) {
                    for (RecipeRule rule : rules) {
                        if (rule.test(recipe)) {
                            if (rule.getAction() == RecipeRule.Action.REMOVE) {
                                shouldRemove = true;
                                break;
                            } else if (rule.getAction() == RecipeRule.Action.REPLACE_INPUT) {
                                replaceInputInRecipe(recipe, rule.getTargetInput(), rule.getNewInput());
                            } else if (rule.getAction() == RecipeRule.Action.REPLACE_OUTPUT) {
                                replaceOutputInRecipe(recipe, rule.getNewOutput());
                            }
                        }
                    }
                }

                if (shouldRemove) {
                    toRemove.add(recipe.getId());
                }
            } catch (Exception e) {
                lastErrorCount++;
                RaspberryMod.LOGGER.error("Error processing recipe {}: {}", recipe.getId(), e.getMessage());
            }
        }

        for (ResourceLocation id : toRemove) {
            Recipe<?> recipe = recipesByName.remove(id);
            if (recipe != null) {
                Map<ResourceLocation, Recipe<?>> typeMap = recipesByType.get(recipe.getType());
                if (typeMap != null) {
                    if (!(typeMap instanceof HashMap)) {
                        typeMap = new HashMap<>(typeMap);
                        recipesByType.put(recipe.getType(), typeMap);
                    }
                    typeMap.remove(id);
                }
            }
        }

        managerAccessor.setRecipes(recipesByType);
        managerAccessor.setByName(recipesByName);

        if (!toRemove.isEmpty()) {
            RaspberryMod.LOGGER.info("RecipeModifier removed {} recipes.", toRemove.size());
        }
        if (lastErrorCount > 0) {
            RaspberryMod.LOGGER.warn("RecipeModifier encountered {} errors during execution.", lastErrorCount);
        }
    }

    private static ItemStack getResult(Recipe<?> recipe) {
        try {
            return recipe.getResultItem(RegistryAccess.EMPTY);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    private static void replaceInputInRecipe(Recipe<?> recipe, Ingredient target, Ingredient replacement) {
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            if (ingredientMatches(recipe.getIngredients().get(i), target)) {
                recipe.getIngredients().set(i, replacement);
            }
        }
        if (recipe instanceof SingleItemRecipe single && ingredientMatches(single.getIngredients().get(0), target)) {
            ((SingleItemRecipeAccessor) single).setIngredient(replacement);
        }
        if (recipe instanceof AbstractCookingRecipe cooking && ingredientMatches(cooking.getIngredients().get(0), target)) {
            ((AbstractCookingRecipeAccessor) cooking).setIngredient(replacement);
        }
    }

    private static void replaceOutputInRecipe(Recipe<?> recipe, ItemStack newResult) {
        if (recipe instanceof ShapedRecipe shaped) ((ShapedRecipeAccessor) shaped).setResult(newResult);
        else if (recipe instanceof ShapelessRecipe shapeless) ((ShapelessRecipeAccessor) shapeless).setResult(newResult);
        else if (recipe instanceof AbstractCookingRecipe cooking) ((AbstractCookingRecipeAccessor) cooking).setResult(newResult);
        else if (recipe instanceof SingleItemRecipe single) ((SingleItemRecipeAccessor) single).setResult(newResult);
    }

    private static boolean ingredientMatches(Ingredient ing, Ingredient target) {
        if (ing == null || ing.isEmpty() || target == null || target.isEmpty()) return false;

        try {
            for (ItemStack targetItem : target.getItems()) {
                if (ing.test(targetItem)) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }
}