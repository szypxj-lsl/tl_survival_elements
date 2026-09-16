package com.szypxj.tlsurvivalelements.temperature;

import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class EquipmentTemperatureService {
    private static List<String> cachedRaw = List.of();
    private static List<Rule> cachedRules = List.of();

    private EquipmentTemperatureService() {
    }

    public static Resistance total(Player player) {
        if (player == null) return Resistance.ZERO;
        Resistance result = Resistance.ZERO;
        for (ItemStack stack : player.getArmorSlots()) {
            result = result.add(forStack(stack));
        }
        return result;
    }

    public static Resistance forStack(ItemStack stack) {
        return forParsedRules(stack, rules());
    }

    public static Resistance forStack(ItemStack stack, List<String> rawRules) {
        return forParsedRules(stack, parseRules(rawRules));
    }

    private static Resistance forParsedRules(ItemStack stack, List<Rule> rules) {
        if (stack == null || stack.isEmpty()) return Resistance.ZERO;
        Rule directMatch = null;
        Rule tagMatch = null;
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());

        for (Rule rule : rules) {
            if (rule.tag()) {
                if (tagMatch == null && rule.matches(stack)) tagMatch = rule;
            } else if (itemId != null && itemId.equals(rule.id())) {
                directMatch = rule;
                break;
            }
        }

        Rule selected = directMatch != null ? directMatch : tagMatch;
        return selected == null ? Resistance.ZERO : selected.resistance();
    }

    private static synchronized List<Rule> rules() {
        List<String> raw = SurvivalConfig.EQUIPMENT_TEMPERATURE_RULES.get().stream().map(String::valueOf).toList();
        if (raw.equals(cachedRaw)) return cachedRules;

        List<Rule> parsed = parseRules(raw);
        cachedRaw = List.copyOf(raw);
        cachedRules = List.copyOf(parsed);
        return cachedRules;
    }


    private static List<Rule> parseRules(List<String> raw) {
        if (raw == null || raw.isEmpty()) return List.of();
        List<Rule> parsed = new ArrayList<>();
        for (String entry : raw) {
            Rule rule = Rule.parse(entry);
            if (rule != null) parsed.add(rule);
        }
        return List.copyOf(parsed);
    }

    public record Resistance(double heat, double cold) {
        public static final Resistance ZERO = new Resistance(0.0D, 0.0D);

        public Resistance {
            heat = finiteNonNegative(heat);
            cold = finiteNonNegative(cold);
        }

        public Resistance add(Resistance other) {
            return other == null ? this : new Resistance(heat + other.heat, cold + other.cold);
        }

        private static double finiteNonNegative(double value) {
            return Double.isFinite(value) ? Math.max(0.0D, value) : 0.0D;
        }
    }

    private record Rule(ResourceLocation id, boolean tag, Resistance resistance) {
        boolean matches(ItemStack stack) {
            if (!tag || stack == null || stack.isEmpty()) return false;
            TagKey<Item> key = TagKey.create(Registries.ITEM, id);
            return stack.is(key);
        }

        static Rule parse(String text) {
            if (text == null) return null;
            int equals = text.lastIndexOf('=');
            int separator = text.lastIndexOf('|');
            if (equals <= 0 || separator <= equals + 1 || separator >= text.length() - 1) return null;

            String selector = text.substring(0, equals).trim();
            boolean tag = selector.startsWith("#");
            ResourceLocation id = ResourceLocation.tryParse(tag ? selector.substring(1) : selector);
            if (id == null) return null;

            try {
                double heat = Double.parseDouble(text.substring(equals + 1, separator).trim());
                double cold = Double.parseDouble(text.substring(separator + 1).trim());
                if (!Double.isFinite(heat) || !Double.isFinite(cold)) return null;
                return new Rule(id, tag, new Resistance(heat, cold));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
    }
}
