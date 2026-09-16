package com.szypxj.tlsurvivalelements.service;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class HydrationRules {
    private HydrationRules() {
    }

    public static List<String> effectiveRules(List<String> customRules, boolean useBuiltin, List<String> builtinRules) {
        List<String> effective = new ArrayList<>();
        if (customRules != null && !customRules.isEmpty()) {
            effective.addAll(customRules);
        }
        if (useBuiltin && builtinRules != null && !builtinRules.isEmpty()) {
            effective.addAll(builtinRules);
        }
        return List.copyOf(effective);
    }

    public static double hydrationFor(ItemStack stack, List<String> rules, double defaultPotionRestore) {
        if (stack == null || stack.isEmpty()) {
            return 0.0D;
        }
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId != null) {
            Double direct = findRule(itemId.toString(), stack, rules);
            if (direct != null) {
                return direct;
            }
        }
        if (stack.getItem() instanceof PotionItem) {
            return Math.max(0.0D, defaultPotionRestore);
        }
        return 0.0D;
    }

    private static Double findRule(String itemId, ItemStack stack, List<String> rules) {
        if (rules == null || rules.isEmpty()) {
            return null;
        }
        for (String rule : rules) {
            if (rule == null) {
                continue;
            }
            int equals = rule.lastIndexOf('=');
            if (equals <= 0) {
                continue;
            }
            String key = rule.substring(0, equals).trim();
            double value;
            try {
                value = Double.parseDouble(rule.substring(equals + 1).trim());
            } catch (NumberFormatException ignored) {
                continue;
            }
            if (!Double.isFinite(value)) {
                continue;
            }
            if (key.startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.tryParse(key.substring(1));
                if (tagId == null) {
                    continue;
                }
                TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
                if (stack.is(tag)) {
                    return Math.max(0.0D, value);
                }
            } else if (key.equals(itemId)) {
                return Math.max(0.0D, value);
            }
        }
        return null;
    }
}
