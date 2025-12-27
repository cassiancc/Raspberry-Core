package cc.cassian.raspberry.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Predicate;

public class RecipeRule {
    public enum Action { REMOVE, REPLACE_INPUT, REPLACE_OUTPUT }

    private final Action action;
    private final Predicate<Recipe<?>> filter;

    private final Ingredient targetInput;
    private final Ingredient newInput;
    private final ItemStack newOutput;

    // Removals
    public RecipeRule(Action action, Predicate<Recipe<?>> filter) {
        this(action, filter, Ingredient.EMPTY, Ingredient.EMPTY, ItemStack.EMPTY);
    }

    // Input Replacement
    public RecipeRule(Action action, Predicate<Recipe<?>> filter, Ingredient target, Ingredient replacement) {
        this(action, filter, target, replacement, ItemStack.EMPTY);
    }

    // Output Replacement
    public RecipeRule(Action action, Predicate<Recipe<?>> filter, ItemStack output) {
        this(action, filter, Ingredient.EMPTY, Ingredient.EMPTY, output);
    }

    private RecipeRule(Action action, Predicate<Recipe<?>> filter, Ingredient target, Ingredient rep, ItemStack out) {
        this.action = action;
        this.filter = filter;
        this.targetInput = target;
        this.newInput = rep;
        this.newOutput = out;
    }

    public boolean test(Recipe<?> recipe) {
        return filter.test(recipe);
    }

    public Action getAction() { return action; }
    public Ingredient getTargetInput() { return targetInput; }
    public Ingredient getNewInput() { return newInput; }
    public ItemStack getNewOutput() { return newOutput; }
}