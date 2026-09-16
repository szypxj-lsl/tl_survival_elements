package com.szypxj.tlsurvivalelements.network.packet;

import com.szypxj.tlsurvivalelements.service.CtrlBoostService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SBoostStatePacket(boolean active, boolean movingInput) {
    public static void encode(C2SBoostStatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.active);
        buffer.writeBoolean(packet.movingInput);
    }

    public static C2SBoostStatePacket decode(FriendlyByteBuf buffer) {
        return new C2SBoostStatePacket(buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(C2SBoostStatePacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) CtrlBoostService.setRequested(player, packet.active, packet.movingInput);
        });
        context.setPacketHandled(true);
    }
}
