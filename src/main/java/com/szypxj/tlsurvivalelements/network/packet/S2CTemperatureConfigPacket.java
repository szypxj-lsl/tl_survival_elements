package com.szypxj.tlsurvivalelements.network.packet;

import com.szypxj.tlsurvivalelements.client.ClientTemperatureConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record S2CTemperatureConfigPacket(
        List<String> equipmentRules,
        List<String> hydrationRules,
        double defaultPotionRestore
) {
    private static final int MAX_RULES = 1024;

    public S2CTemperatureConfigPacket {
        equipmentRules = equipmentRules == null ? List.of() : List.copyOf(equipmentRules);
        hydrationRules = hydrationRules == null ? List.of() : List.copyOf(hydrationRules);
        if (!Double.isFinite(defaultPotionRestore) || defaultPotionRestore < 0.0D) {
            defaultPotionRestore = 0.0D;
        }
    }

    public static void encode(S2CTemperatureConfigPacket packet, FriendlyByteBuf buffer) {
        writeRules(buffer, packet.equipmentRules);
        writeRules(buffer, packet.hydrationRules);
        buffer.writeDouble(packet.defaultPotionRestore);
    }

    public static S2CTemperatureConfigPacket decode(FriendlyByteBuf buffer) {
        List<String> equipmentRules = readRules(buffer);
        List<String> hydrationRules = readRules(buffer);
        double defaultPotionRestore = buffer.readDouble();
        return new S2CTemperatureConfigPacket(equipmentRules, hydrationRules, defaultPotionRestore);
    }

    public static void handle(S2CTemperatureConfigPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                Dist.CLIENT,
                () -> () -> {
                    ClientTemperatureConfig.acceptEquipmentRules(packet.equipmentRules);
                    ClientTemperatureConfig.acceptHydrationConfig(packet.hydrationRules, packet.defaultPotionRestore);
                }
        ));
        context.setPacketHandled(true);
    }

    private static void writeRules(FriendlyByteBuf buffer, List<String> rules) {
        int size = Math.min(MAX_RULES, rules.size());
        buffer.writeVarInt(size);
        for (int i = 0; i < size; i++) {
            buffer.writeUtf(rules.get(i));
        }
    }

    private static List<String> readRules(FriendlyByteBuf buffer) {
        int size = Math.max(0, Math.min(MAX_RULES, buffer.readVarInt()));
        List<String> rules = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            rules.add(buffer.readUtf());
        }
        return rules;
    }
}
