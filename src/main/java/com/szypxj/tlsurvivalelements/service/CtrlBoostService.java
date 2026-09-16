package com.szypxj.tlsurvivalelements.service;

import com.szypxj.tldomesticatemorecreatures.api.riding.RideStateApi;
import com.szypxj.tlsurvivalelements.compat.TdmcCompat;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class CtrlBoostService {
    private static final UUID BOOST_ID = UUID.fromString("8f4ab731-3d45-4d71-9b50-9a2e1ff455c1");
    private static final String BOOST_NAME = "tl_survival_elements.ctrl_boost";
    private static final Set<UUID> REQUESTED = new HashSet<>();
    private static final Set<UUID> MOVING_REQUESTED = new HashSet<>();
    private static final Map<UUID, LivingEntity> LAST_TARGET = new HashMap<>();

    private CtrlBoostService() {
    }

    public static void setRequested(ServerPlayer player, boolean active, boolean movingInput) {
        UUID playerId = player.getUUID();
        if (active) {
            REQUESTED.add(playerId);
            if (movingInput) {
                MOVING_REQUESTED.add(playerId);
            } else {
                MOVING_REQUESTED.remove(playerId);
            }
        } else {
            REQUESTED.remove(playerId);
            MOVING_REQUESTED.remove(playerId);
            clearModifier(player);
        }
    }

    public static boolean tick(ServerPlayer player) {
        UUID playerId = player.getUUID();
        LivingEntity target = target(player);
        LivingEntity previous = LAST_TARGET.get(playerId);

        if (previous != null && previous != target) remove(previous);
        if (target == null) {
            LAST_TARGET.remove(playerId);
            remove(player);
            return false;
        }

        LAST_TARGET.put(playerId, target);
        if (!REQUESTED.contains(playerId) || !hasResource(target)) {
            remove(target);
            return false;
        }

        boolean moving = MOVING_REQUESTED.contains(playerId) || RideStateApi.isAccelerating(target) || isMoving(target);
        if (moving && !SurvivalService.consumeBoostStamina(target, mode(target))) {
            remove(target);
            return false;
        }

        apply(target);
        return moving;
    }

    public static boolean isBoosting(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        return speed != null && speed.getModifier(BOOST_ID) != null;
    }

    public static boolean isActivelyBoosting(LivingEntity entity) {
        if (!isBoosting(entity)) return false;
        if (RideStateApi.isAccelerating(entity) || isMoving(entity)) return true;
        for (Map.Entry<UUID, LivingEntity> entry : LAST_TARGET.entrySet()) {
            UUID playerId = entry.getKey();
            if (entry.getValue() == entity && REQUESTED.contains(playerId) && MOVING_REQUESTED.contains(playerId)) {
                return true;
            }
        }
        return false;
    }

    public static void clear(ServerPlayer player) {
        REQUESTED.remove(player.getUUID());
        MOVING_REQUESTED.remove(player.getUUID());
        clearModifier(player);
    }

    private static void clearModifier(ServerPlayer player) {
        LivingEntity previous = LAST_TARGET.remove(player.getUUID());
        if (previous != null) remove(previous);
        remove(player);
        if (player.getVehicle() instanceof LivingEntity mount) remove(mount);
    }

    private static LivingEntity target(ServerPlayer player) {
        if (player.getVehicle() instanceof LivingEntity mount) {
            return TdmcCompat.isManagedPet(mount) ? mount : null;
        }
        return player;
    }

    private static boolean hasResource(LivingEntity entity) {
        if (entity instanceof ServerPlayer player && (player.isCreative() || player.isSpectator())) return true;
        SurvivalData data = SurvivalData.of(entity);
        if (data.stamina() <= 0.0D) return false;
        return !(entity instanceof ServerPlayer) || data.water() > 0.0D;
    }

    private static boolean isMoving(LivingEntity entity) {
        var movement = entity.getDeltaMovement();
        return movement.horizontalDistanceSqr() > 0.0004D || Math.abs(movement.y) > 0.02D;
    }

    private static SurvivalService.BoostMode mode(LivingEntity entity) {
        if (entity.isInWaterOrBubble()) return SurvivalService.BoostMode.SWIM;
        if (RideStateApi.isRiddenFlying(entity)) return SurvivalService.BoostMode.FLIGHT;
        return SurvivalService.BoostMode.GROUND;
    }

    private static void apply(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) return;

        double amount = Math.max(0.0D, SurvivalConfig.BOOST_SPEED_MULTIPLIER.get() - 1.0D);
        AttributeModifier current = speed.getModifier(BOOST_ID);
        if (current != null
                && Math.abs(current.getAmount() - amount) <= 1.0E-9D
                && current.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
            return;
        }

        if (current != null) speed.removeModifier(BOOST_ID);
        speed.addTransientModifier(new AttributeModifier(
                BOOST_ID,
                BOOST_NAME,
                amount,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        ));
    }

    private static void remove(LivingEntity entity) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(BOOST_ID) != null) speed.removeModifier(BOOST_ID);
    }
}
