package cc.cassian.raspberry.compat.vanillabackport.leash.network;

import cc.cassian.raspberry.compat.vanillabackport.leash.KnotConnectionAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class KnotConnectionSyncPacket {
    private final int entityId;
    private final Set<UUID> connectedUuids;

    public KnotConnectionSyncPacket(int entityId, Set<UUID> connectedUuids) {
        this.entityId = entityId;
        this.connectedUuids = new HashSet<>(connectedUuids);
    }

    public KnotConnectionSyncPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        int size = buf.readVarInt();
        this.connectedUuids = new HashSet<>();
        for (int i = 0; i < size; i++) {
            this.connectedUuids.add(buf.readUUID());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeVarInt(this.connectedUuids.size());
        for (UUID uuid : this.connectedUuids) {
            buf.writeUUID(uuid);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                return;
            }
            
            Entity entity = mc.level.getEntity(this.entityId);
            if (entity instanceof LeashFenceKnotEntity && entity instanceof KnotConnectionAccess access) {
                access.raspberry$getConnectionManager().setConnectedUuids(new HashSet<>(this.connectedUuids));
            }
        });
        context.get().setPacketHandled(true);
    }
    
    public void sendTo(ServerPlayer player) {
        cc.cassian.raspberry.network.RaspberryNetwork.CHANNEL.send(
            PacketDistributor.PLAYER.with(() -> player), 
            this
        );
    }
}