package cc.cassian.raspberry.mixin.copperized;

import cc.cassian.raspberry.registry.RaspberrySoundEvents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.sounds.SoundEvent;
import net.onvoid.copperized.common.CopperizedArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CopperizedArmorMaterials.class)
public class CopperizedArmorMaterialsMixin {
    @ModifyReturnValue(
            method = "getEquipSound",
            at = @At(value = "RETURN")
    )
    private static SoundEvent mixin(SoundEvent original) {
        return RaspberrySoundEvents.COPPER_EQUIP.get();
    }
}
