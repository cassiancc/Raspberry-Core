package cc.cassian.raspberry.mixin.accessor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractCookingRecipe.class)
public interface AbstractCookingRecipeAccessor {
    @Accessor("result")
    @Mutable
    void setResult(ItemStack result);

    @Accessor("ingredient")
    @Mutable
    void setIngredient(Ingredient ingredient);
}