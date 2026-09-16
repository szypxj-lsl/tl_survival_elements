package com.szypxj.tlsurvivalelements.network.packet;

import com.szypxj.tlsurvivalelements.client.ClientSurvivalState;
import com.szypxj.tlsurvivalelements.temperature.TemperatureStage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CSurvivalSyncPacket(
        float health,
        float maxHealth,
        int armor,
        double food,
        double maxFood,
        double water,
        double maxWater,
        double stamina,
        double maxStamina,
        double currentTemperature,
        double heatResistance,
        double coldResistance,
        double minimumSafeTemperature,
        double maximumSafeTemperature,
        TemperatureStage temperatureStage,
        int mountId,
        boolean mountManaged,
        float mountHealth,
        float mountMaxHealth,
        double mountFood,
        double mountMaxFood,
        double mountStamina,
        double mountMaxStamina
) {
    public static void encode(S2CSurvivalSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeFloat(packet.health);
        buffer.writeFloat(packet.maxHealth);
        buffer.writeVarInt(packet.armor);
        buffer.writeDouble(packet.food);
        buffer.writeDouble(packet.maxFood);
        buffer.writeDouble(packet.water);
        buffer.writeDouble(packet.maxWater);
        buffer.writeDouble(packet.stamina);
        buffer.writeDouble(packet.maxStamina);
        buffer.writeDouble(packet.currentTemperature);
        buffer.writeDouble(packet.heatResistance);
        buffer.writeDouble(packet.coldResistance);
        buffer.writeDouble(packet.minimumSafeTemperature);
        buffer.writeDouble(packet.maximumSafeTemperature);
        buffer.writeEnum(packet.temperatureStage);
        buffer.writeVarInt(packet.mountId);
        buffer.writeBoolean(packet.mountManaged);
        buffer.writeFloat(packet.mountHealth);
        buffer.writeFloat(packet.mountMaxHealth);
        buffer.writeDouble(packet.mountFood);
        buffer.writeDouble(packet.mountMaxFood);
        buffer.writeDouble(packet.mountStamina);
        buffer.writeDouble(packet.mountMaxStamina);
    }

    public static S2CSurvivalSyncPacket decode(FriendlyByteBuf buffer) {
        return new S2CSurvivalSyncPacket(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readVarInt(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readEnum(TemperatureStage.class),
                buffer.readVarInt(),
                buffer.readBoolean(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readDouble()
        );
    }

    public static void handle(S2CSurvivalSyncPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSurvivalState.accept(packet)));
        context.setPacketHandled(true);
    }
}
