package com.szypxj.tlsurvivalelements.temperature;

import com.szypxj.tlsurvivalelements.config.SurvivalConfig;

import java.util.ArrayList;
import java.util.List;

public final class TemperatureSourceRules {
    private static List<String> cachedRaw = List.of();
    private static List<TemperatureSourceRule> cachedRules = List.of();

    private TemperatureSourceRules() {
    }

    public static synchronized List<TemperatureSourceRule> rules() {
        List<String> raw = SurvivalConfig.TEMPERATURE_SOURCE_RULES.get().stream().map(String::valueOf).toList();
        if (raw.equals(cachedRaw)) return cachedRules;

        List<TemperatureSourceRule> parsed = new ArrayList<>();
        for (String entry : raw) {
            TemperatureSourceRule rule = TemperatureSourceRule.parse(entry);
            if (rule != null) parsed.add(rule);
        }
        cachedRaw = List.copyOf(raw);
        cachedRules = List.copyOf(parsed);
        return cachedRules;
    }

    public static int maximumRadius() {
        int radius = 0;
        for (TemperatureSourceRule rule : rules()) {
            radius = Math.max(radius, rule.radius());
        }
        return radius;
    }
}
