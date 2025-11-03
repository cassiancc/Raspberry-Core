package cc.cassian.raspberry.mixin.create;

import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AllArmorMaterials.class)
public class AllArmorMaterialsMixin {
    @ModifyReturnValue(
            method = "getEquipSound",
            at = @At(value = "RETURN")
    )
    private static SoundEvent mixin(SoundEvent original) {
        return RaspberrySoundEvents.COPPER_EQUIP.get();
    }
}
