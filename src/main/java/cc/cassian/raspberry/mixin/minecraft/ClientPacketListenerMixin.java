package cc.cassian.raspberry.mixin.minecraft;

import cc.cassian.raspberry.compat.vanillabackport.leash.Leashable;
import cc.cassian.raspberry.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "handleEntityLinkPacket", at = @At("HEAD"), cancellable = true)
    private void raspberry$onEntityLinkPacket(ClientboundSetEntityLinkPacket packet, CallbackInfo ci) {
        if (!ModConfig.get().backportLeash) return;

        if (!this.minecraft.isSameThread()) {
            return;
        }
        if (this.level == null) return;

        Entity entity = this.level.getEntity(packet.getSourceId());
        
        if (entity instanceof Leashable leashable) {
            ci.cancel();

            int destId = packet.getDestId();
            
            if (destId == 0) {
                leashable.raspberry$dropLeash(true, false);
            } else {
                if (entity instanceof Mob mob) {
                    ((MobAccessor) mob).raspberry$setDelayedLeashHolderId(destId);
                } else {
                    leashable.raspberry$setDelayedLeashHolderId(destId);
                }
                
                Entity target = this.level.getEntity(destId);
                if (target != null) {
                    leashable.raspberry$setLeashedTo(target, true);
                }
            }
        }
    }
}