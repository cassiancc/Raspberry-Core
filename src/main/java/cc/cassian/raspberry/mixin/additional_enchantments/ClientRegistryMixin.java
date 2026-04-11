package cc.cassian.raspberry.mixin.additional_enchantments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.cadentem.additional_enchantments.client.ClientRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientRegistry.class)
public class ClientRegistryMixin {
    @WrapOperation(
            method = "registerKeys",
            at = @At(value = "INVOKE", target = "net/minecraftforge/client/event/RegisterKeyMappingsEvent.register (Lnet/minecraft/client/KeyMapping;)V"),
            remap = false
    )
    private static void disableHomingCycleHotKey(RegisterKeyMappingsEvent instance, KeyMapping key, Operation<Void> original) {
        // Don't register any kepmappings
    }
}
