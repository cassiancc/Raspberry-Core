package cc.cassian.raspberry.mixin.vc_gliders;

import cc.cassian.raspberry.compat.GlidersCompat;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {
    @WrapOperation(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
    public EquipmentSlot doNotTakeGliders(ItemStack stack, Operation<EquipmentSlot> original) {
        if (GlidersCompat.isGlider(stack)) {
            return EquipmentSlot.MAINHAND;
        }
        return original.call(stack);
    }
}
