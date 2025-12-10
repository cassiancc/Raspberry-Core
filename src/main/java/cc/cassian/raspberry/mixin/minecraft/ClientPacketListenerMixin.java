package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.common.api.leash.Leashable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow private ClientLevel level;

    @Inject(method = "handleEntityLinkPacket", at = @At("TAIL"))
    private void raspberry$onEntityLinkPacket(ClientboundSetEntityLinkPacket packet, CallbackInfo ci) {
        Entity entity = this.level.getEntity(packet.getSourceId());
        if (entity instanceof Leashable leashable && entity instanceof Boat) {
            leashable.setDelayedLeashHolderId(packet.getDestId());
        }
    }
}