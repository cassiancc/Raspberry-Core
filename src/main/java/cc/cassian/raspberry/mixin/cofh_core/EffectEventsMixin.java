package cc.cassian.raspberry.mixin.cofh_core;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.events.AftershockEvent;
import cc.cassian.raspberry.config.ModConfig;
import cofh.core.common.event.EffectEvents;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
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
        if (ModCompat.COPPERIZED && ModConfig.get().aftershock)
            AftershockEvent.electrify(event);
    }
}
