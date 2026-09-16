package com.szypxj.tlsurvivalelements.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SRideControlPacket(int mountId, boolean boost, boolean descend, boolean dismount) {
    public static void encode(C2SRideControlPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.mountId);
        buffer.writeBoolean(packet.boost);
        buffer.writeBoolean(packet.descend);
        buffer.writeBoolean(packet.dismount);
    }

    public static C2SRideControlPacket decode(FriendlyByteBuf buffer) {
        return new C2SRideControlPacket(buffer.readVarInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(C2SRideControlPacket packet, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().setPacketHandled(true);
    }
}
