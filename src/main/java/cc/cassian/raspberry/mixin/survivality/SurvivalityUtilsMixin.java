package cc.cassian.raspberry.mixin.survivality;

import cc.cassian.raspberry.events.EquipEvent;
import com.github.creoii.survivality.util.SurvivalityUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(SurvivalityUtils.class)
public class SurvivalityUtilsMixin {
    @WrapOperation(
            method = "swapArmor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private static Item gogglesAreHelmetsBasically(ItemStack stack, Operation<Item> original) {
        if (EquipEvent.shouldModifyReturnValue(stack)) {
            return EquipEvent.modifyReturnValue(stack);
        }
        else return original.call(stack);
    }
}
