package com.szypxj.tlsurvivalelements.service;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public final class RideRuntime {
    private RideRuntime() {
    }

    public static void update(ServerPlayer player, int mountId, boolean boost, boolean descend) {
    }

    public static void clear(ServerPlayer player) {
    }

    public static boolean isBoosting(LivingEntity entity) {
        return false;
    }

    public static boolean tick(ServerPlayer player) {
        return false;
    }
}
