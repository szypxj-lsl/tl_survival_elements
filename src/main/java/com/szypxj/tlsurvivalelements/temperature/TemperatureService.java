package com.szypxj.tlsurvivalelements.temperature;

import com.szypxj.tldomesticatemorecreatures.data.ProgressData;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TemperatureService {
    private static final Map<UUID, CacheEntry> CACHE = new ConcurrentHashMap<>();

    private TemperatureService() {
    }

    public static TemperatureSnapshot snapshot(ServerPlayer player) {
        if (player == null) {
            return TemperatureSnapshot.neutral(5.0D, 35.0D);
        }
        if (!SurvivalConfig.TEMPERATURE_ENABLED.get()) {
            double baseMinimum = Math.min(SurvivalConfig.BASE_MINIMUM_TEMPERATURE.get(), SurvivalConfig.BASE_MAXIMUM_TEMPERATURE.get());
            double baseMaximum = Math.max(SurvivalConfig.BASE_MINIMUM_TEMPERATURE.get(), SurvivalConfig.BASE_MAXIMUM_TEMPERATURE.get());
            return TemperatureSnapshot.neutral(baseMinimum, baseMaximum);
        }

        long now = player.level().getGameTime();
        int interval = Math.max(1, SurvivalConfig.TEMPERATURE_SOURCE_SCAN_INTERVAL_TICKS.get());
        CacheEntry cached = CACHE.get(player.getUUID());
        if (cached != null && now >= cached.gameTime() && now - cached.gameTime() < interval) {
            return cached.snapshot();
        }

        TemperatureSnapshot computed = compute(player);
        CACHE.put(player.getUUID(), new CacheEntry(now, computed));
        return computed;
    }

    public static void invalidate(ServerPlayer player) {
        if (player != null) CACHE.remove(player.getUUID());
    }

    private static TemperatureSnapshot compute(ServerPlayer player) {
        double resistancePoints = ProgressData.exists(player)
                ? Math.max(0, ProgressData.of(player).stat("resistance").total())
                : 0.0D;
        double tdmcResistance = resistancePoints * SurvivalConfig.TDMC_RESISTANCE_PER_POINT.get();
        EquipmentTemperatureService.Resistance equipment = EquipmentTemperatureService.total(player);
        double heatResistance = tdmcResistance + equipment.heat();
        double coldResistance = tdmcResistance + equipment.cold();
        double baseMinimum = Math.min(SurvivalConfig.BASE_MINIMUM_TEMPERATURE.get(), SurvivalConfig.BASE_MAXIMUM_TEMPERATURE.get());
        double baseMaximum = Math.max(SurvivalConfig.BASE_MINIMUM_TEMPERATURE.get(), SurvivalConfig.BASE_MAXIMUM_TEMPERATURE.get());
        double minimumSafeTemperature = TemperatureMath.minimumSafeTemperature(baseMinimum, coldResistance);
        double maximumSafeTemperature = TemperatureMath.maximumSafeTemperature(baseMaximum, heatResistance);
        double currentTemperature = TemperatureEnvironment.currentTemperature(
                (ServerLevel) player.level(),
                player.blockPosition()
        );
        TemperatureStage stage = TemperatureMath.stage(
                currentTemperature,
                minimumSafeTemperature,
                maximumSafeTemperature,
                SurvivalConfig.TEMPERATURE_SEVERE_THRESHOLD_PERCENT.get(),
                SurvivalConfig.TEMPERATURE_CRITICAL_THRESHOLD_PERCENT.get()
        );
        return new TemperatureSnapshot(
                currentTemperature,
                heatResistance,
                coldResistance,
                minimumSafeTemperature,
                maximumSafeTemperature,
                stage
        );
    }

    private record CacheEntry(long gameTime, TemperatureSnapshot snapshot) {
    }
}
