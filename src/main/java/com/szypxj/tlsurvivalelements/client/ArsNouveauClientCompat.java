package com.szypxj.tlsurvivalelements.client;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ArsNouveauClientCompat {
    private static final String MOD_ID = "ars_nouveau";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean resolved;
    private static boolean available;
    private static Method getCurrentManaMethod;
    private static Method getMaxManaMethod;
    private static Field reservedOverlayManaField;
    private static Field redOverlayManaField;
    private static Field redOverlayTicksField;

    private ArsNouveauClientCompat() {
    }

    public static boolean loaded() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static boolean available() {
        return resolve();
    }

    public static double currentMana(Player player) {
        if (player == null || !resolve()) {
            return 0.0D;
        }
        try {
            Object value = getCurrentManaMethod.invoke(null, player);
            return value instanceof Number number ? Math.max(0.0D, number.doubleValue()) : 0.0D;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            disable(exception);
            return 0.0D;
        }
    }

    public static double maxMana(Player player) {
        if (player == null || !resolve()) {
            return 0.0D;
        }
        try {
            Object value = getMaxManaMethod.invoke(null, player);
            return value instanceof Number number ? Math.max(0.0D, number.doubleValue()) : 0.0D;
        } catch (ReflectiveOperationException | RuntimeException exception) {
            disable(exception);
            return 0.0D;
        }
    }

    public static double reservedRatio() {
        if (!resolve()) {
            return 0.0D;
        }
        try {
            return clamp01(reservedOverlayManaField.getFloat(null));
        } catch (IllegalAccessException | RuntimeException exception) {
            disable(exception);
            return 0.0D;
        }
    }

    public static double redOverlayRatio(Player player) {
        if (player == null || !resolve()) {
            return 0.0D;
        }
        try {
            if (redOverlayTicksField.getInt(null) <= 0) {
                return 0.0D;
            }
            double max = maxMana(player);
            if (max <= 0.0D) {
                return 0.0D;
            }
            return clamp01(redOverlayManaField.getFloat(null) / max);
        } catch (IllegalAccessException | RuntimeException exception) {
            disable(exception);
            return 0.0D;
        }
    }

    private static synchronized boolean resolve() {
        if (resolved) {
            return available;
        }
        resolved = true;
        if (!loaded()) {
            return false;
        }
        try {
            Class<?> manaUtil = Class.forName("com.hollingsworth.arsnouveau.api.util.ManaUtil");
            Class<?> clientInfo = Class.forName("com.hollingsworth.arsnouveau.client.ClientInfo");
            getCurrentManaMethod = manaUtil.getMethod("getCurrentMana", LivingEntity.class);
            getMaxManaMethod = manaUtil.getMethod("getMaxMana", Player.class);
            reservedOverlayManaField = clientInfo.getField("reservedOverlayMana");
            redOverlayManaField = clientInfo.getField("redOverlayMana");
            redOverlayTicksField = clientInfo.getField("redOverlayTicks");
            available = true;
        } catch (ReflectiveOperationException | LinkageError exception) {
            LOGGER.warn("Ars Nouveau client mana API could not be resolved. TSE will leave the original mana HUD enabled.", exception);
            available = false;
        }
        return available;
    }

    private static synchronized void disable(Exception exception) {
        if (available) {
            LOGGER.warn("Ars Nouveau client mana API invocation failed. TSE will stop replacing the mana HUD.", exception);
        }
        available = false;
    }

    private static double clamp01(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
