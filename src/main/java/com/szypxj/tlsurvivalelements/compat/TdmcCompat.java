package com.szypxj.tlsurvivalelements.compat;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

public final class TdmcCompat {
    private static final Map<LivingEntity, PetCache> PET_CACHE = new WeakHashMap<>();
    private static boolean resolved;
    private static Method ownershipMethod;

    private TdmcCompat() {
    }

    public static boolean loaded() {
        return ModList.get().isLoaded("tl_domesticate_more_creatures");
    }

    public static boolean isOwnedBy(LivingEntity entity, Player player) {
        if (entity instanceof TamableAnimal tamable) return tamable.isOwnedBy(player);
        resolve();
        if (ownershipMethod == null) return false;
        try {
            Object result = ownershipMethod.invoke(null, entity, player);
            return result instanceof Boolean value && value;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    public static boolean isManagedPet(LivingEntity entity) {
        if (entity instanceof TamableAnimal tamable) return tamable.isTame();
        if (!(entity.level() instanceof ServerLevel level)) return false;

        long now = level.getGameTime();
        synchronized (PET_CACHE) {
            PetCache cached = PET_CACHE.get(entity);
            if (cached != null && now < cached.nextCheckTick()) return cached.managed();
        }

        boolean managed = false;
        for (Player player : level.players()) {
            if (isOwnedBy(entity, player)) {
                managed = true;
                break;
            }
        }

        synchronized (PET_CACHE) {
            PET_CACHE.put(entity, new PetCache(managed, now + (managed ? 1200L : 200L)));
        }
        return managed;
    }

    public static void rememberManagedPet(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        synchronized (PET_CACHE) {
            PET_CACHE.put(entity, new PetCache(true, level.getGameTime() + 1200L));
        }
    }

    private static synchronized void resolve() {
        if (resolved) return;
        resolved = true;
        if (!loaded()) return;
        try {
            Class<?> ownership = Class.forName("com.szypxj.tldomesticatemorecreatures.game.PetOwnershipService");
            ownershipMethod = ownership.getMethod("isOwnedBy", LivingEntity.class, Player.class);
        } catch (ReflectiveOperationException ignored) {
            ownershipMethod = null;
        }
    }

    private record PetCache(boolean managed, long nextCheckTick) {
    }
}
