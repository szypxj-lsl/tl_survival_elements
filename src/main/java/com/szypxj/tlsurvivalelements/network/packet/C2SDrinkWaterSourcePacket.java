package com.szypxj.tlsurvivalelements.network.packet;

import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.item.WaterContainerItem;
import com.szypxj.tlsurvivalelements.network.SurvivalNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public final class C2SDrinkWaterSourcePacket {
    public static final double RESTORE = 25.0D;

    public static void encode(C2SDrinkWaterSourcePacket packet, FriendlyByteBuf buffer) {
    }

    public static C2SDrinkWaterSourcePacket decode(FriendlyByteBuf buffer) {
        return new C2SDrinkWaterSourcePacket();
    }

    public static void handle(C2SDrinkWaterSourcePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !player.getMainHandItem().isEmpty()) return;
            BlockHitResult hit = WaterContainerItem.waterSourceHit(player.level(), player);
            if (!WaterContainerItem.isWaterSource(player.level(), hit)) return;
            SurvivalData data = SurvivalData.of(player);
            if (data.water() >= data.maxWater()) return;
            data.water(data.water() + RESTORE);
            SurvivalNetwork.sendState(player);
        });
        context.setPacketHandled(true);
    }
}
