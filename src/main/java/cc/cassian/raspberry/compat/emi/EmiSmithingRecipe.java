package cc.cassian.raspberry.compat.emi;

import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class EmiSmithingRecipe implements EmiRecipe {
    private final EmiIngredient input1;
    private final EmiStack input2;
    private final EmiStack output;
    private final ResourceLocation id;
    private final int uniq;

    public EmiSmithingRecipe(EmiIngredient input1, EmiStack input2, EmiStack output, ResourceLocation id) {
        this.uniq = EmiUtil.RANDOM.nextInt();
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
        this.id = id;
    }

    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.SMITHING;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public List<EmiIngredient> getInputs() {
        return List.of(this.input1, this.input2);
    }

    public List<EmiStack> getOutputs() {
        return List.of(this.output);
    }

    public boolean supportsRecipeTree() {
        return false;
    }

    public int getDisplayWidth() {
        return 125;
    }

    public int getDisplayHeight() {
        return 18;
    }

    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addGeneratedSlot((r) -> this.input1, this.uniq, 0, 0);
        widgets.addSlot(this.input2, 49, 0);
        widgets.addGeneratedSlot((r) -> this.output, this.uniq, 107, 0).recipeContext(this);
    }

}
