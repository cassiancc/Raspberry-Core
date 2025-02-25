package cc.cassian.raspberry.mixin.vc_gliders;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.venturecraft.gliders.common.item.GliderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getEquipSound", at = @At(value = "RETURN"), cancellable = true)
    private void glidersMakeEquipNoises(CallbackInfoReturnable<SoundEvent> cir) {
        if ((Item)(Object) this instanceof GliderItem) {
            cir.setReturnValue(SoundEvents.ARMOR_EQUIP_GENERIC);
        }
    }
}
