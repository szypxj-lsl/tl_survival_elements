package com.szypxj.tlsurvivalelements.temperature;

public final class TemperatureMath {
    public static final double BIOME_TEMPERATURE_SCALE = 18.75D;

    private TemperatureMath() {
    }

    public static double biomeToCelsius(double biomeTemperature) {
        return 5.0D + biomeTemperature * BIOME_TEMPERATURE_SCALE;
    }

    public static double altitudeModifier(
            double y,
            double seaLevel,
            double coolingPerBlockAboveSeaLevel,
            double warmingPerBlockBelowSeaLevel
    ) {
        if (y > seaLevel) {
            return -(y - seaLevel) * Math.max(0.0D, coolingPerBlockAboveSeaLevel);
        }
        if (y < seaLevel) {
            return (seaLevel - y) * Math.max(0.0D, warmingPerBlockBelowSeaLevel);
        }
        return 0.0D;
    }

    public static double minimumSafeTemperature(double baseMinimumTemperature, double coldResistance) {
        return baseMinimumTemperature - Math.max(0.0D, coldResistance);
    }

    public static double maximumSafeTemperature(double baseMaximumTemperature, double heatResistance) {
        return baseMaximumTemperature + Math.max(0.0D, heatResistance);
    }

    public static TemperatureStage stage(
            double currentTemperature,
            double minimumSafeTemperature,
            double maximumSafeTemperature,
            double severeThresholdPercent,
            double criticalThresholdPercent
    ) {
        double safeSpan = Math.max(0.0D, maximumSafeTemperature - minimumSafeTemperature);
        double severeDelta = safeSpan * Math.max(0.0D, severeThresholdPercent);
        double criticalDelta = safeSpan * Math.max(0.0D, criticalThresholdPercent);

        if (currentTemperature >= maximumSafeTemperature + criticalDelta) {
            return TemperatureStage.CRITICAL_HEAT;
        }
        if (currentTemperature <= minimumSafeTemperature - criticalDelta) {
            return TemperatureStage.CRITICAL_COLD;
        }
        if (currentTemperature >= maximumSafeTemperature + severeDelta) {
            return TemperatureStage.HEATSTROKE;
        }
        if (currentTemperature <= minimumSafeTemperature - severeDelta) {
            return TemperatureStage.HYPOTHERMIA;
        }
        if (currentTemperature >= maximumSafeTemperature) {
            return TemperatureStage.HOT;
        }
        if (currentTemperature <= minimumSafeTemperature) {
            return TemperatureStage.COLD;
        }
        return TemperatureStage.NORMAL;
    }
}
