package com.szypxj.tlsurvivalelements.temperature;

public enum TemperatureStage {
    NORMAL,
    HOT,
    COLD,
    HEATSTROKE,
    HYPOTHERMIA,
    CRITICAL_HEAT,
    CRITICAL_COLD;

    public boolean isHot() {
        return this == HOT || this == HEATSTROKE || this == CRITICAL_HEAT;
    }

    public boolean isCold() {
        return this == COLD || this == HYPOTHERMIA || this == CRITICAL_COLD;
    }

    public boolean isSevere() {
        return this == HEATSTROKE || this == HYPOTHERMIA || isCritical();
    }

    public boolean isCritical() {
        return this == CRITICAL_HEAT || this == CRITICAL_COLD;
    }
}
