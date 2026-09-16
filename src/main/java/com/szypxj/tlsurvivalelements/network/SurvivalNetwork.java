package com.szypxj.tlsurvivalelements.network;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.compat.TdmcCompat;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.service.HydrationRules;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.network.packet.C2SBoostStatePacket;
import com.szypxj.tlsurvivalelements.network.packet.C2SDrinkWaterSourcePacket;
import com.szypxj.tlsurvivalelements.network.packet.S2CSurvivalSyncPacket;
import com.szypxj.tlsurvivalelements.network.packet.S2CTemperatureConfigPacket;
import com.szypxj.tlsurvivalelements.temperature.TemperatureService;
import com.szypxj.tlsurvivalelements.temperature.TemperatureSnapshot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.Objects;

public final class SurvivalNetwork {
    private static final String PROTOCOL = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Objects.requireNonNull(ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, "main")),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );
    private static int nextId;

    private SurvivalNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(nextId++, S2CSurvivalSyncPacket.class, S2CSurvivalSyncPacket::encode, S2CSurvivalSyncPacket::decode, S2CSurvivalSyncPacket::handle);
        CHANNEL.registerMessage(nextId++, C2SBoostStatePacket.class, C2SBoostStatePacket::encode, C2SBoostStatePacket::decode, C2SBoostStatePacket::handle);
        CHANNEL.registerMessage(nextId++, C2SDrinkWaterSourcePacket.class, C2SDrinkWaterSourcePacket::encode, C2SDrinkWaterSourcePacket::decode, C2SDrinkWaterSourcePacket::handle);
        CHANNEL.registerMessage(nextId++, S2CTemperatureConfigPacket.class, S2CTemperatureConfigPacket::encode, S2CTemperatureConfigPacket::decode, S2CTemperatureConfigPacket::handle);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendTemperatureConfig(ServerPlayer player) {
        if (player == null) return;
        List<String> equipmentRules = SurvivalConfig.EQUIPMENT_TEMPERATURE_RULES.get().stream()
                .map(String::valueOf)
                .toList();
        List<String> hydrationRules = HydrationRules.effectiveRules(
                SurvivalConfig.HYDRATION_ITEMS.get().stream().map(String::valueOf).toList(),
                SurvivalConfig.USE_BUILTIN_FOOD_DRINK_COMPATIBILITY.get(),
                SurvivalConfig.BUILTIN_HYDRATION_RULES.get().stream().map(String::valueOf).toList()
        );
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2CTemperatureConfigPacket(
                equipmentRules,
                hydrationRules,
                SurvivalConfig.DEFAULT_DRINK_WATER_RESTORE.get()
        ));
    }

    public static void sendState(ServerPlayer player) {
        SurvivalData playerData = SurvivalData.of(player);
        TemperatureSnapshot temperature = TemperatureService.snapshot(player);
        int mountId = -1;
        boolean mountManaged = false;
        float mountHealth = 0.0F;
        float mountMaxHealth = 0.0F;
        double mountFood = 0.0D;
        double mountMaxFood = 0.0D;
        double mountStamina = 0.0D;
        double mountMaxStamina = 0.0D;

        if (player.getVehicle() instanceof LivingEntity mount) {
            SurvivalData mountData = SurvivalData.of(mount);
            mountId = mount.getId();
            mountManaged = TdmcCompat.isManagedPet(mount);
            mountHealth = mount.getHealth();
            mountMaxHealth = mount.getMaxHealth();
            mountFood = mountData.food();
            mountMaxFood = mountData.maxFood();
            mountStamina = mountData.stamina();
            mountMaxStamina = mountData.maxStamina();
        }

        S2CSurvivalSyncPacket packet = new S2CSurvivalSyncPacket(
                player.getHealth(),
                player.getMaxHealth(),
                player.getArmorValue(),
                playerData.food(),
                playerData.maxFood(),
                playerData.water(),
                playerData.maxWater(),
                playerData.stamina(),
                playerData.maxStamina(),
                temperature.currentTemperature(),
                temperature.heatResistance(),
                temperature.coldResistance(),
                temperature.minimumSafeTemperature(),
                temperature.maximumSafeTemperature(),
                temperature.stage(),
                mountId,
                mountManaged,
                mountHealth,
                mountMaxHealth,
                mountFood,
                mountMaxFood,
                mountStamina,
                mountMaxStamina
        );
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
