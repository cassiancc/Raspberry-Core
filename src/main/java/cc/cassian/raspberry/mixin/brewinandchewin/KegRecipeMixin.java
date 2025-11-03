package cc.cassian.raspberry.mixin.brewinandchewin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import umpaz.brewinandchewin.common.crafting.KegRecipe;

@Mixin(KegRecipe.class)
public interface KegRecipeMixin {
    @Accessor("inputItems")
    NonNullList<Ingredient> getInputItems();
}
