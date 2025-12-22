package cc.cassian.raspberry.networking;

import cc.cassian.raspberry.RaspberryMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RaspberryNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RaspberryMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int packetId = 0;
        INSTANCE.registerMessage(packetId++, SetStackPacket.class,
                SetStackPacket::encode,
                SetStackPacket::decode,
                SetStackPacket::handle);
    }

    public static void sendToServer(SetStackPacket packet) {
        INSTANCE.sendToServer(packet);
    }

}
