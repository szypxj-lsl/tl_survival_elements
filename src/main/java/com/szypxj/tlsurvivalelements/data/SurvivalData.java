package com.szypxj.tlsurvivalelements.data;

import com.szypxj.tlsurvivalelements.registry.ModAttributes;
import com.szypxj.tlsurvivalelements.service.FoodDataAccess;
import com.szypxj.tlsurvivalelements.service.SurvivalMath;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class SurvivalData {
    private static final String ROOT = "tl_survival_elements";
    private static final String INITIALIZED = "Initialized";
    private static final String FOOD = "Food";
    private static final String WATER = "Water";
    private static final String STAMINA = "Stamina";
    private static final String LAST_MAX_FOOD = "LastMaxFood";
    private static final String LAST_MAX_WATER = "LastMaxWater";
    private static final String LAST_MAX_STAMINA = "LastMaxStamina";
    private static final String LAST_ACTION = "LastAction";
    private static final String FOOD_DRAIN_BUFFER = "FoodDrainBuffer";
    private static final String HEALTH_BASE_INITIALIZED = "HealthBase100Initialized";
    private static final String LAST_COMBAT_GAME_TIME = "LastCombatGameTime";
    private static final String FOOD_RESERVE = "FoodReserve";
    private static final String NEXT_FORAGE_GAME_TIME = "NextForageGameTime";
    private static final String NEXT_GROUND_FOOD_GAME_TIME = "NextGroundFoodGameTime";
    private static final String NEXT_HUNT_GAME_TIME = "NextHuntGameTime";

    private final LivingEntity entity;
    private final CompoundTag tag;

    private SurvivalData(LivingEntity entity) {
        this.entity = entity;
        CompoundTag persistent = entity.getPersistentData();
        if (!persistent.contains(ROOT, CompoundTag.TAG_COMPOUND)) persistent.put(ROOT, new CompoundTag());
        this.tag = persistent.getCompound(ROOT);
        ensureInitialized();
    }

    public static SurvivalData of(LivingEntity entity) {
        return new SurvivalData(entity);
    }

    private void ensureInitialized() {
        boolean playerEntity = entity instanceof Player;
        boolean firstInitialization = !tag.getBoolean(INITIALIZED);
        boolean missingFood = !playerEntity && !tag.contains(FOOD);
        boolean missingWater = playerEntity && !tag.contains(WATER);
        boolean missingStamina = !tag.contains(STAMINA);
        boolean missingLastMaxFood = !tag.contains(LAST_MAX_FOOD);
        boolean missingLastMaxWater = !tag.contains(LAST_MAX_WATER);
        boolean missingLastMaxStamina = !tag.contains(LAST_MAX_STAMINA);
        boolean missingLastAction = !tag.contains(LAST_ACTION);
        boolean missingFoodReserve = !tag.contains(FOOD_RESERVE);
        boolean missingNextForageGameTime = !tag.contains(NEXT_FORAGE_GAME_TIME);
        boolean missingNextGroundFoodGameTime = !tag.contains(NEXT_GROUND_FOOD_GAME_TIME);
        boolean missingNextHuntGameTime = !tag.contains(NEXT_HUNT_GAME_TIME);

        if (!firstInitialization
                && !missingFood
                && !missingWater
                && !missingStamina
                && !missingLastMaxFood
                && !missingLastMaxWater
                && !missingLastMaxStamina
                && !missingLastAction
                && !missingFoodReserve
                && !missingNextForageGameTime
                && !missingNextGroundFoodGameTime
                && !missingNextHuntGameTime) {
            return;
        }

        if (firstInitialization) {
            tag.putBoolean(INITIALIZED, true);
        }

        double maxFood = firstInitialization || missingFood || missingLastMaxFood ? maxFood() : 0.0D;
        double maxWater = missingWater || missingLastMaxWater ? maxWater() : 0.0D;
        double maxStamina = missingStamina || missingLastMaxStamina ? maxStamina() : 0.0D;

        if (entity instanceof Player player) {
            if (firstInitialization) {
                player.getFoodData().setFoodLevel((int) Math.round(maxFood));
                player.getFoodData().setSaturation(25.0F);
            }
            if (missingWater) {
                tag.putDouble(WATER, maxWater);
            }
        } else if (missingFood) {
            tag.putDouble(FOOD, maxFood);
        }

        if (missingStamina) {
            tag.putDouble(STAMINA, maxStamina);
        }
        if (missingLastMaxFood) {
            tag.putDouble(LAST_MAX_FOOD, maxFood);
        }
        if (missingLastMaxWater) {
            tag.putDouble(LAST_MAX_WATER, maxWater);
        }
        if (missingLastMaxStamina) {
            tag.putDouble(LAST_MAX_STAMINA, maxStamina);
        }
        if (missingLastAction) {
            tag.putLong(LAST_ACTION, entity.level().getGameTime());
        }
        if (missingFoodReserve) {
            tag.putDouble(FOOD_RESERVE, 0.0D);
        }
        long now = entity.level().getGameTime();
        long jitter = Math.floorMod(entity.getUUID().getLeastSignificantBits(), 200L);
        if (missingNextForageGameTime) {
            tag.putLong(NEXT_FORAGE_GAME_TIME, now + 20L + jitter);
        }
        if (missingNextGroundFoodGameTime) {
            tag.putLong(NEXT_GROUND_FOOD_GAME_TIME, now + 20L + jitter);
        }
        if (missingNextHuntGameTime) {
            tag.putLong(NEXT_HUNT_GAME_TIME, now + 20L + jitter);
        }
    }

    public double maxFood() {
        double value = SurvivalMath.maxFromPoints(SurvivalMath.baseFood(entity), foodPoints());
        if (entity instanceof Player player && player.getFoodData() instanceof FoodDataAccess access) {
            access.tlse$setMaxFood((int) Math.round(value));
        }
        return value;
    }

    public double maxWater() {
        return entity instanceof Player player ? SurvivalMath.maxFromPoints(SurvivalMath.baseWater(player), waterPoints()) : 0.0D;
    }

    public double maxStamina() {
        return SurvivalMath.maxFromPoints(SurvivalMath.baseStamina(entity), staminaPoints());
    }

    public double food() {
        if (entity instanceof Player player) return player.getFoodData().getFoodLevel();
        return tag.getDouble(FOOD);
    }

    public void food(double value) {
        if (entity instanceof Player player) {
            player.getFoodData().setFoodLevel((int) Math.round(clamp(value, 0.0D, maxFood())));
        } else {
            tag.putDouble(FOOD, clamp(value, 0.0D, maxFood()));
        }
    }

    public double availableFoodForDrain() {
        if (!(entity instanceof Player)) return Math.max(0.0D, food());
        double buffered = Math.max(0.0D, tag.getDouble(FOOD_DRAIN_BUFFER));
        return Math.max(0.0D, food() - buffered);
    }

    public void drainFood(double amount) {
        if (amount <= 0.0D) return;
        if (!(entity instanceof Player player)) {
            food(food() - amount);
            return;
        }
        double buffered = Math.max(0.0D, tag.getDouble(FOOD_DRAIN_BUFFER)) + amount;
        int whole = (int) Math.floor(buffered + 1.0E-9D);
        if (whole > 0) {
            int current = player.getFoodData().getFoodLevel();
            int applied = Math.min(current, whole);
            player.getFoodData().setFoodLevel(current - applied);
            buffered -= applied;
            if (player.getFoodData().getFoodLevel() <= 0) buffered = 0.0D;
        }
        tag.putDouble(FOOD_DRAIN_BUFFER, Math.max(0.0D, buffered));
    }

    public double water() {
        return entity instanceof Player ? tag.getDouble(WATER) : 0.0D;
    }

    public void water(double value) {
        if (entity instanceof Player) tag.putDouble(WATER, clamp(value, 0.0D, maxWater()));
    }

    public double stamina() {
        return tag.getDouble(STAMINA);
    }

    public void stamina(double value) {
        tag.putDouble(STAMINA, clamp(value, 0.0D, maxStamina()));
    }

    public int foodPoints() {
        return ModAttributes.foodPoints(entity);
    }


    public int waterPoints() {
        return entity instanceof Player ? ModAttributes.waterPoints(entity) : 0;
    }


    public int staminaPoints() {
        return ModAttributes.staminaPoints(entity);
    }


    public void syncCapacity() {
        double newFoodMax = maxFood();
        double oldFoodMax = rememberedMax(LAST_MAX_FOOD, newFoodMax);
        if (changed(oldFoodMax, newFoodMax)) {
            double ratio = oldFoodMax <= 0.0D ? 1.0D : clamp(food() / oldFoodMax, 0.0D, 1.0D);
            food(newFoodMax * ratio);
        }
        tag.putDouble(LAST_MAX_FOOD, newFoodMax);

        if (entity instanceof Player) {
            double newWaterMax = maxWater();
            double oldWaterMax = rememberedMax(LAST_MAX_WATER, newWaterMax);
            if (changed(oldWaterMax, newWaterMax)) {
                double ratio = oldWaterMax <= 0.0D ? 1.0D : clamp(water() / oldWaterMax, 0.0D, 1.0D);
                water(newWaterMax * ratio);
            }
            tag.putDouble(LAST_MAX_WATER, newWaterMax);
        }

        double newStaminaMax = maxStamina();
        double oldStaminaMax = rememberedMax(LAST_MAX_STAMINA, newStaminaMax);
        if (changed(oldStaminaMax, newStaminaMax)) {
            double ratio = oldStaminaMax <= 0.0D ? 1.0D : clamp(stamina() / oldStaminaMax, 0.0D, 1.0D);
            stamina(newStaminaMax * ratio);
        }
        tag.putDouble(LAST_MAX_STAMINA, newStaminaMax);
    }


    public double foodReserve() {
        return Math.max(0.0D, tag.getDouble(FOOD_RESERVE));
    }

    public void foodReserve(double value) {
        tag.putDouble(FOOD_RESERVE, Double.isFinite(value) ? Math.max(0.0D, value) : 0.0D);
    }

    public long nextForageGameTime() {
        return Math.max(0L, tag.getLong(NEXT_FORAGE_GAME_TIME));
    }

    public void nextForageGameTime(long value) {
        tag.putLong(NEXT_FORAGE_GAME_TIME, Math.max(0L, value));
    }

    public long nextGroundFoodGameTime() {
        return Math.max(0L, tag.getLong(NEXT_GROUND_FOOD_GAME_TIME));
    }

    public void nextGroundFoodGameTime(long value) {
        tag.putLong(NEXT_GROUND_FOOD_GAME_TIME, Math.max(0L, value));
    }

    public long nextHuntGameTime() {
        return Math.max(0L, tag.getLong(NEXT_HUNT_GAME_TIME));
    }

    public void nextHuntGameTime(long value) {
        tag.putLong(NEXT_HUNT_GAME_TIME, Math.max(0L, value));
    }

    public long lastCombatGameTime() {
        return Math.max(0L, tag.getLong(LAST_COMBAT_GAME_TIME));
    }

    public void lastCombatGameTime(long value) {
        tag.putLong(LAST_COMBAT_GAME_TIME, Math.max(0L, value));
    }

    public boolean healthBaseInitialized() {
        return tag.getBoolean(HEALTH_BASE_INITIALIZED);
    }

    public void healthBaseInitialized(boolean value) {
        tag.putBoolean(HEALTH_BASE_INITIALIZED, value);
    }

    public long lastAction() {
        return tag.getLong(LAST_ACTION);
    }

    public void lastAction(long value) {
        tag.putLong(LAST_ACTION, value);
    }

    public void refillAll() {
        food(maxFood());
        water(maxWater());
        stamina(maxStamina());
        tag.putDouble(LAST_MAX_FOOD, maxFood());
        tag.putDouble(LAST_MAX_WATER, maxWater());
        tag.putDouble(LAST_MAX_STAMINA, maxStamina());
        tag.putDouble(FOOD_DRAIN_BUFFER, 0.0D);
    }

    public void clamp() {
        food(food());
        water(water());
        stamina(stamina());
    }

    private double rememberedMax(String key, double fallback) {
        return tag.contains(key) ? Math.max(0.0D, tag.getDouble(key)) : fallback;
    }

    private static boolean changed(double first, double second) {
        return Math.abs(first - second) > 1.0E-6D;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
