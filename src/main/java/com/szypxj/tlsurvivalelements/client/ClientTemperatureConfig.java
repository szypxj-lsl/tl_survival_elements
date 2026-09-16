package com.szypxj.tlsurvivalelements.client;

import java.util.List;

public final class ClientTemperatureConfig {
    private static volatile List<String> equipmentRules = List.of();
    private static volatile List<String> hydrationRules = List.of();
    private static volatile double defaultPotionRestore;

    private ClientTemperatureConfig() {
    }

    public static void acceptEquipmentRules(List<String> rules) {
        equipmentRules = rules == null ? List.of() : List.copyOf(rules);
    }

    public static void acceptHydrationConfig(List<String> rules, double potionRestore) {
        hydrationRules = rules == null ? List.of() : List.copyOf(rules);
        defaultPotionRestore = Double.isFinite(potionRestore) ? Math.max(0.0D, potionRestore) : 0.0D;
    }

    public static List<String> equipmentRules() {
        return equipmentRules;
    }

    public static List<String> hydrationRules() {
        return hydrationRules;
    }

    public static double defaultPotionRestore() {
        return defaultPotionRestore;
    }
}
