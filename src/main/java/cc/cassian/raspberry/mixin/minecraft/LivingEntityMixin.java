package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.ModCompat;
import cc.cassian.raspberry.config.ModConfig;
import cc.cassian.raspberry.events.AftershockEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void tick(CallbackInfo ci) {
        if (ModCompat.COFH_CORE)
            AftershockEvent.resist((LivingEntity) (Object) this);
    }
}
