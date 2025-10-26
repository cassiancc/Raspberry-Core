package cc.cassian.raspberry.compat.emi;

import cc.cassian.raspberry.compat.BrewinAndChewinCompat;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.client.recipebook.KegRecipeBookTab;

import java.util.List;

public class EmiFermentingRecipe implements EmiRecipe {

    private final ResourceLocation id;
    private final String group;
    private final KegRecipeBookTab tab;
    private final NonNullList<EmiIngredient> inputItems;
    private final Ingredient fluidItem;
    private final ItemStack output;
    private final ItemStack container;
    private final float experience;
    private final int fermentTime;
    private final int temperature;


    public EmiFermentingRecipe(ResourceLocation id, String group, @javax.annotation.Nullable KegRecipeBookTab tab, NonNullList<Ingredient> inputItems, Ingredient fluidItem, ItemStack output, ItemStack container, float experience, int fermentTime, int temperature) {
        this.id = id;
        this.group = group;
        this.tab = tab;
        NonNullList<EmiIngredient> ingredients = NonNullList.create();
        for (Ingredient inputItem : inputItems) {
            ingredients.add(EmiIngredient.of(inputItem));
        }
        this.inputItems = ingredients;

        this.fluidItem = fluidItem;
        this.output = output;
        if (!container.isEmpty()) {
            this.container = container;
        } else if (!output.getCraftingRemainingItem().isEmpty()) {
            this.container = output.getCraftingRemainingItem();
        } else {
            this.container = ItemStack.EMPTY;
        }

        this.experience = experience;
        this.fermentTime = fermentTime;
        this.temperature = temperature;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return BrewinAndChewinCompat.FERMENTING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputItems;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(output));
    }

    @Override
    public int getDisplayWidth() {
        return 117;
    }

    @Override
    public int getDisplayHeight() {
        return 57;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        ResourceLocation backgroundImage = new ResourceLocation("brewinandchewin", "textures/gui/jei/keg.png");
        // background
        widgetHolder.addTexture(backgroundImage, 0, 0, 117, 57, 29,16 );
        // arrow
        widgetHolder.addAnimatedTexture(backgroundImage, 48, 28, 33, 9, 176, 28, fermentTime, true, false, false);
        // frigid
        if (temperature == 1) {
            widgetHolder.addTexture(backgroundImage, 47, 23, 7, 3, 176, 0).tooltipText(List.of(Component.translatable("emi.brewinandchewin.fermenting.frigid")));
            widgetHolder.addTexture(backgroundImage, 54, 23, 7, 3, 182, 0).tooltipText(List.of(Component.translatable("emi.brewinandchewin.fermenting.frigid")));
        }
        // cold
        if (temperature == 2) {
            widgetHolder.addTexture(backgroundImage, 54, 23, 7, 3, 182, 0).tooltipText(List.of(Component.translatable("emi.brewinandchewin.fermenting.cold")));
        }
        // warm
        if (temperature == 4) {
            widgetHolder.addTexture(backgroundImage, 67, 23, 7, 3, 195, 0).tooltipText(List.of(Component.translatable("emi.brewinandchewin.fermenting.warm")));
        }
        // hot
        if (temperature == 5) {
            widgetHolder.addTexture(backgroundImage, 74, 23, 7, 3, 202, 0).tooltipText(List.of(Component.translatable("emi.brewinandchewin.fermenting.hot")));
            widgetHolder.addTexture(backgroundImage, 67, 23, 7, 3, 195, 0).tooltipText(List.of(Component.translatable("emi.brewinandchewin.fermenting.hot")));
        }

        int borderSlotSize = 18;

        for(int row = 0; row < 2; ++row) {
            for(int column = 0; column < 2; ++column) {
                int inputIndex = row * 2 + column;

                try {
                    widgetHolder.addSlot((this.inputItems.get(inputIndex)), column * borderSlotSize + 3, row * borderSlotSize + 11);
                } catch (Exception var13) {
                }
            }
        }

        if (!this.fluidItem.isEmpty()) {
            widgetHolder.addSlot(EmiIngredient.of(this.fluidItem), 55, 1);
        }

        widgetHolder.addSlot(EmiIngredient.of(Ingredient.of(this.output)), 92, 7).drawBack(false).recipeContext(this);
        if (!this.container.isEmpty()) {
            widgetHolder.addSlot(EmiIngredient.of(Ingredient.of(this.container)), 60, 38);
        }

        widgetHolder.addSlot(EmiIngredient.of(Ingredient.of(this.output)), 92, 38);

    }
}