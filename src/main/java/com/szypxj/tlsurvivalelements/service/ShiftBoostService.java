package com.szypxj.tlsurvivalelements.service;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public final class ShiftBoostService {
    private ShiftBoostService() {
    }

    public static boolean tick(ServerPlayer player) {
        return false;
    }

    public static boolean isBoosting(LivingEntity entity) {
        return false;
    }

    public static void clear(ServerPlayer player) {
    }
}
