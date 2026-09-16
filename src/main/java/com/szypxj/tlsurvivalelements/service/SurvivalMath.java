package com.szypxj.tlsurvivalelements.service;

import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

public final class SurvivalMath {
    public static final double PLAYER_BASE = 100.0D;

    private SurvivalMath() {
    }

    public static double baseFood(LivingEntity entity) {
        return entity instanceof Player ? PLAYER_BASE : Math.max(100.0D, baseMaxHealth(entity) / 5.0D);
    }

    public static double baseWater(Player player) {
        return PLAYER_BASE;
    }

    public static double baseStamina(LivingEntity entity) {
        return entity instanceof Player ? PLAYER_BASE : Math.max(100.0D, baseMaxHealth(entity) / 5.0D);
    }

    public static double baseMaxHealth(LivingEntity entity) {
        AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
        return health == null ? Math.max(1.0D, entity.getMaxHealth()) : Math.max(1.0D, health.getBaseValue());
    }

    public static double maxFromPoints(double base, int points) {
        return Math.max(1.0D, base * (1.0D + Math.max(0, points) * 0.10D));
    }

    public static double standardStaminaCost(LivingEntity entity) {
        Double override = staminaOverride(entity);
        if (override != null) return Math.max(0.0D, override);
        double value = Math.sqrt(baseStamina(entity)) / 3.0D;
        return Math.max(SurvivalConfig.MIN_STAMINA_COST.get(), Math.min(SurvivalConfig.MAX_STAMINA_COST.get(), value));
    }

    private static Double staminaOverride(LivingEntity entity) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (id == null) return null;
        String target = id.toString();
        for (String rule : SurvivalConfig.STAMINA_COST_OVERRIDES.get()) {
            int equals = rule.lastIndexOf('=');
            if (equals <= 0) continue;
            if (!rule.substring(0, equals).trim().equals(target)) continue;
            try {
                return Double.parseDouble(rule.substring(equals + 1).trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
