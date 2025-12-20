package cc.cassian.raspberry.mixin.emi;

import cc.cassian.raspberry.config.ModConfig;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.recipe.EmiSmithingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EmiSmithingRecipe.class)
public abstract class EmiSmithingTransformMixin implements EmiRecipe {
    @Shadow
    @Final
    protected EmiIngredient template;

    @Shadow
    @Final
    protected EmiIngredient input;

    @Shadow
    @Final
    protected EmiIngredient addition;

    @Shadow
    @Final
    protected EmiStack output;

    @WrapMethod(method = "addWidgets", remap = false)
    private void removeInputWidget(WidgetHolder widgets, Operation<Void> original) {
       if (ModConfig.get().noTemplates) {
           widgets.addTexture(EmiTexture.EMPTY_ARROW, 48, 1);
           widgets.addSlot(input, 4, 0);
           widgets.addSlot(addition, 22, 0);
           widgets.addSlot(output, 81, 0).recipeContext(this);
       } else {
           original.call(widgets);
       }
    }
}
