package com.szypxj.tlsurvivalelements.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TdmcTamingRules {
    private static final Pattern RULE_OBJECT = Pattern.compile("entity\\s*=\\s*\"([^\"]+)\"");
    private static final Pattern ITEM = Pattern.compile("item\\s*=\\s*\"([^\"]+)\"");
    private static final Map<String, Set<String>> FOODS = new HashMap<>();
    private static boolean loaded;

    private TdmcTamingRules() {
    }

    public static boolean hasRule(LivingEntity entity) {
        ensureLoaded();
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return id != null && FOODS.containsKey(id.toString());
    }

    public static boolean isTamingFood(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty()) return false;
        ensureLoaded();
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (entityId == null || itemId == null) return false;
        Set<String> foods = FOODS.get(entityId.toString());
        return foods != null && foods.contains(itemId.toString());
    }

    public static synchronized void reload() {
        loaded = false;
        FOODS.clear();
        ensureLoaded();
    }

    private static synchronized void ensureLoaded() {
        if (loaded) return;
        loaded = true;
        Path root = FMLPaths.CONFIGDIR.get();
        List<Path> candidates = List.of(
                root.resolve("tl_domesticate_more_creatures-common.toml"),
                root.resolve("tl_domesticate_more_creatures").resolve("taming_rules.toml"),
                root.resolve("taming_rules.toml")
        );
        for (Path path : candidates) {
            if (!Files.isRegularFile(path)) continue;
            try {
                load(path);
                if (!FOODS.isEmpty()) return;
            } catch (IOException ignored) {
                FOODS.clear();
            }
        }
    }

    private static void load(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        Matcher rules = RULE_OBJECT.matcher(text);
        int previousStart = -1;
        String previousEntity = null;
        while (rules.find()) {
            if (previousEntity != null) parseRule(previousEntity, text.substring(previousStart, rules.start()));
            previousEntity = rules.group(1);
            previousStart = rules.start();
        }
        if (previousEntity != null) parseRule(previousEntity, text.substring(previousStart));
    }

    private static void parseRule(String entityId, String ruleText) {
        if (!ruleText.contains("native_foods") && !ruleText.contains("extra_foods")) return;
        Set<String> foods = new HashSet<>();
        Matcher items = ITEM.matcher(ruleText);
        while (items.find()) foods.add(items.group(1));
        if (!foods.isEmpty()) FOODS.put(entityId, Set.copyOf(foods));
    }
}
