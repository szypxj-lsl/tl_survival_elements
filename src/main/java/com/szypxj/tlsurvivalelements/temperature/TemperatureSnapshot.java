package com.szypxj.tlsurvivalelements.temperature;

public record TemperatureSnapshot(
        double currentTemperature,
        double heatResistance,
        double coldResistance,
        double minimumSafeTemperature,
        double maximumSafeTemperature,
        TemperatureStage stage
) {
    public static TemperatureSnapshot neutral(double minimumSafeTemperature, double maximumSafeTemperature) {
        double current = (minimumSafeTemperature + maximumSafeTemperature) * 0.5D;
        return new TemperatureSnapshot(
                current,
                0.0D,
                0.0D,
                minimumSafeTemperature,
                maximumSafeTemperature,
                TemperatureStage.NORMAL
        );
    }

    public double safeSpan() {
        return Math.max(0.0D, maximumSafeTemperature - minimumSafeTemperature);
    }
}
