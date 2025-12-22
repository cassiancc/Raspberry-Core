package cc.cassian.raspberry.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetStackPacket(int slot, ItemStack stack) {

    public static void encode(SetStackPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
        buf.writeItem(packet.stack);
    }

    public static SetStackPacket decode(FriendlyByteBuf buf) {
        return new SetStackPacket(
                buf.readInt(),
                buf.readItem()
        );
    }

    public static void handle(SetStackPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.hasPermissions(2)) {
				InventoryMenu inventory = player.inventoryMenu;
				Slot slot = inventory.getSlot(packet.slot);
				if (slot.mayPlace(packet.stack)) {
					slot.setByPlayer(packet.stack);
					inventory.sendAllDataToRemote();
				}
            }
        });
        context.setPacketHandled(true);
    }
}
