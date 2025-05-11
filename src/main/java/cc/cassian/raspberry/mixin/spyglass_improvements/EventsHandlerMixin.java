package cc.cassian.raspberry.mixin.spyglass_improvements;

import cc.cassian.raspberry.overlay.OverlayHelpers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.juancarloscp52.spyglass_improvements.events.EventsHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EventsHandler.class)
public class EventsHandlerMixin {
    @WrapOperation(
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isCreative()Z")
    )
    private boolean allowSpyglassesInBundles(LocalPlayer player, Operation<Boolean> original) {
        if (!OverlayHelpers.checkInventoryForStack(player.getInventory(), null, Items.SPYGLASS).isEmpty()) {
            return true;
        } else {
            return original.call(player);
        }
    }
}
