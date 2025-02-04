package cc.cassian.raspberry.mixin.cofh_core;

import cc.cassian.raspberry.compat.CopperizedCompat;
import cofh.core.event.EffectEvents;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(EffectEvents.class)
public class EffectEventsMixin {
    @Inject(method = "handleEntityStruckByLightningEvent", at = @At(value = "HEAD"), remap = false)
    private static void mixin(EntityStruckByLightningEvent event, CallbackInfo ci) {
        if (ModList.get().isLoaded("copperized"))
            CopperizedCompat.electrify(event);
    }
}
