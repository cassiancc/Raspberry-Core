package cc.cassian.raspberry.network;

import cc.cassian.raspberry.RaspberryMod;
import cc.cassian.raspberry.compat.vanillabackport.leash.network.KnotConnectionSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RaspberryNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RaspberryMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, KnotConnectionSyncPacket.class, 
            KnotConnectionSyncPacket::encode, 
            KnotConnectionSyncPacket::new, 
            KnotConnectionSyncPacket::handle);
    }
}