package com.szypxj.tlsurvivalelements.service;

import com.szypxj.tlsurvivalelements.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class SurvivalAttributeApi {
    private SurvivalAttributeApi() {
    }

    public static int foodPoints(LivingEntity entity) {
        return ModAttributes.foodPoints(entity);
    }

    public static int staminaPoints(LivingEntity entity) {
        return ModAttributes.staminaPoints(entity);
    }

    public static int waterPoints(Player player) {
        return ModAttributes.waterPoints(player);
    }
}
