package com.szypxj.tlsurvivalelements.service;

import com.szypxj.tlsurvivalelements.compat.TdmcCompat;
import com.szypxj.tlsurvivalelements.compat.TdmcTamingRules;
import com.szypxj.tldomesticatemorecreatures.api.compat.ErsCompatApi;
import com.szypxj.tldomesticatemorecreatures.api.torpor.TorporApi;
import com.szypxj.tldomesticatemorecreatures.backpack.PetBackpackService;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.registry.ModEffects;
import com.szypxj.tlsurvivalelements.temperature.TemperatureService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class SurvivalService {
    private SurvivalService() {
    }

    private static final ThreadLocal<Player> VANILLA_NATURAL_HEALING = new ThreadLocal<>();
    private static final ThreadLocal<LivingEntity> MANAGED_PET_FEEDING = new ThreadLocal<>();

    public static void beginVanillaNaturalHealing(Player player) {
        if (player != null && !player.level().isClientSide) {
            VANILLA_NATURAL_HEALING.set(player);
        }
    }

    public static void endVanillaNaturalHealing(Player player) {
        if (VANILLA_NATURAL_HEALING.get() == player) {
            VANILLA_NATURAL_HEALING.remove();
        }
    }

    public static boolean hasVanillaNaturalHealingContext() {
        return VANILLA_NATURAL_HEALING.get() != null;
    }

    public static boolean isVanillaNaturalHealing(Player player) {
        return player != null && VANILLA_NATURAL_HEALING.get() == player;
    }

    public static float adjustVanillaNaturalHealing(Player player, float requestedAmount) {
        if (player == null || requestedAmount <= 0.0F || !Float.isFinite(requestedAmount)) return 0.0F;
        if (player.isCreative() || player.isSpectator()) return requestedAmount;

        double requested = Math.min(requestedAmount, Math.max(0.0D, player.getMaxHealth() - player.getHealth()));
        requested *= temperatureRecoveryMultiplier(player);
        double allowed = Math.min(requested, maxAffordablePlayerHealing(player));
        if (allowed <= 0.0D) return 0.0F;

        chargePlayerHealing(player, allowed);
        return (float) allowed;
    }

    public static double healingFoodCostPerHealth() {
        return Math.max(0.0D, SurvivalConfig.PLAYER_HEALING_FOOD_COST_PER_HEALTH.get())
                * Math.max(0.0D, SurvivalConfig.PLAYER_FOOD_DRAIN_MULTIPLIER.get());
    }

    public static double healingWaterCostPerHealth() {
        return Math.max(0.0D, SurvivalConfig.PLAYER_HEALING_WATER_COST_PER_HEALTH.get())
                * Math.max(0.0D, SurvivalConfig.PLAYER_WATER_DRAIN_MULTIPLIER.get());
    }

    public static double maxAffordablePlayerHealing(Player player) {
        if (player == null || player.isCreative() || player.isSpectator()) return Double.POSITIVE_INFINITY;
        SurvivalData data = SurvivalData.of(player);
        double foodCost = healingFoodCostPerHealth();
        double waterCost = healingWaterCostPerHealth();
        double foodLimit = foodCost <= 0.0D ? Double.POSITIVE_INFINITY : data.availableFoodForDrain() / foodCost;
        double waterLimit = waterCost <= 0.0D ? Double.POSITIVE_INFINITY : data.water() / waterCost;
        return Math.max(0.0D, Math.min(foodLimit, waterLimit));
    }

    public static void chargePlayerHealing(Player player, double healedAmount) {
        if (player == null || healedAmount <= 0.0D || !Double.isFinite(healedAmount)
                || player.isCreative() || player.isSpectator()) return;
        SurvivalData data = SurvivalData.of(player);
        data.drainFood(healedAmount * healingFoodCostPerHealth());
        data.water(data.water() - healedAmount * healingWaterCostPerHealth());
    }

    public static void recordPlayerCombat(Player player) {
        if (player == null || player.level().isClientSide) return;
        SurvivalData.of(player).lastCombatGameTime(player.level().getGameTime());
    }

    public static void tickPlayerFastHealing(ServerPlayer player) {
        if (player == null || player.isCreative() || player.isSpectator() || !player.isAlive() || !player.isHurt()) return;
        if (!player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) return;

        SurvivalData data = SurvivalData.of(player);
        long now = player.level().getGameTime();
        long delay = Math.max(0L, (long) SurvivalConfig.PLAYER_FAST_HEAL_COMBAT_DELAY_SECONDS.get()) * 20L;
        if (now - data.lastCombatGameTime() < delay) return;

        double threshold = Math.max(0.0D, Math.min(1.0D, SurvivalConfig.PLAYER_FAST_HEAL_RESOURCE_THRESHOLD.get()));
        if (data.food() + 1.0E-6D < data.maxFood() * threshold) return;
        if (data.water() + 1.0E-6D < data.maxWater() * threshold) return;

        double requested = player.getMaxHealth()
                * Math.max(0.0D, SurvivalConfig.PLAYER_FAST_HEAL_MAX_HEALTH_RATE_PER_SECOND.get())
                * temperatureRecoveryMultiplier(player);
        requested = Math.min(requested, Math.max(0.0D, player.getMaxHealth() - player.getHealth()));
        requested = Math.min(requested, maxAffordablePlayerHealing(player));
        if (requested <= 0.0D) return;

        float healed = healAndMeasure(player, (float) requested);
        if (healed > 0.0F) chargePlayerHealing(player, healed);
    }

    public static double currentCreatureFood(LivingEntity entity) {
        if (entity == null || entity instanceof Player) return 0.0D;
        if (ErsCompatApi.usesNativeHunger(entity)) return ErsCompatApi.currentHunger(entity);
        return SurvivalData.of(entity).food();
    }

    public static double maxCreatureFood(LivingEntity entity) {
        if (entity == null || entity instanceof Player) return 0.0D;
        if (ErsCompatApi.usesNativeHunger(entity)) return ErsCompatApi.maxHunger(entity);
        return SurvivalData.of(entity).maxFood();
    }

    public static void addCreatureFood(LivingEntity entity, double amount) {
        if (entity == null || entity instanceof Player || !Double.isFinite(amount)) return;
        if (ErsCompatApi.usesNativeHunger(entity)) {
            ErsCompatApi.addHunger(entity, amount);
            return;
        }
        SurvivalData data = SurvivalData.of(entity);
        data.food(data.food() + amount);
    }

    public static void drainCreatureFood(LivingEntity entity, double amount) {
        if (entity == null || entity instanceof Player || amount <= 0.0D || !Double.isFinite(amount)) return;
        if (ErsCompatApi.usesNativeHunger(entity)) {
            ErsCompatApi.addHunger(entity, -amount);
            return;
        }
        SurvivalData.of(entity).drainFood(amount);
    }

    public static boolean usesNativeCreatureFood(LivingEntity entity) {
        return ErsCompatApi.usesNativeHunger(entity);
    }

    public static double petFeedingFoodCostPerHealth() {
        return Math.max(0.0D, SurvivalConfig.PET_HEALING_FOOD_COST_PER_HEALTH.get());
    }

    public static double petHealingFoodCostPerHealth() {
        return petFeedingFoodCostPerHealth()
                * Math.max(0.0D, SurvivalConfig.PET_FOOD_DRAIN_MULTIPLIER.get());
    }

    public static double maxAffordablePetHealing(LivingEntity entity) {
        if (entity == null || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity) || !TdmcCompat.isManagedPet(entity)) return 0.0D;
        double foodCost = petHealingFoodCostPerHealth();
        if (foodCost <= 0.0D) return Double.POSITIVE_INFINITY;
        return Math.max(0.0D, currentCreatureFood(entity) / foodCost);
    }

    public static void chargePetHealing(LivingEntity entity, double healedAmount) {
        if (entity == null || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity)
                || healedAmount <= 0.0D || !Double.isFinite(healedAmount)) return;
        drainCreatureFood(entity, healedAmount * petHealingFoodCostPerHealth());
    }

    public static void recordPetCombat(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide || entity instanceof Player || !TdmcCompat.isManagedPet(entity)) return;
        SurvivalData.of(entity).lastCombatGameTime(entity.level().getGameTime());
    }

    public static void tickPetNaturalHealing(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity) || !entity.isAlive()
                || !isEntityHurt(entity) || !TdmcCompat.isManagedPet(entity)) return;
        int intervalTicks = Math.max(1, SurvivalConfig.PET_NATURAL_HEAL_INTERVAL_SECONDS.get()) * 20;
        if (entity.tickCount % intervalTicks != 0) return;

        SurvivalData data = SurvivalData.of(entity);
        double threshold = Math.max(0.0D, Math.min(1.0D, SurvivalConfig.PET_NATURAL_HEAL_FOOD_THRESHOLD.get()));
        if (data.food() + 1.0E-6D < data.maxFood() * threshold) return;

        double requested = Math.min(
                Math.max(0.0D, SurvivalConfig.PET_NATURAL_HEAL_AMOUNT.get()),
                Math.max(0.0D, entity.getMaxHealth() - entity.getHealth())
        );
        requested = Math.min(requested, maxAffordablePetHealing(entity));
        if (requested <= 0.0D) return;

        float healed = healAndMeasure(entity, (float) requested);
        if (healed > 0.0F) chargePetHealing(entity, healed);
    }

    public static void tickPetFastHealing(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity) || !entity.isAlive()
                || !isEntityHurt(entity) || !TdmcCompat.isManagedPet(entity)) return;
        SurvivalData data = SurvivalData.of(entity);
        long now = entity.level().getGameTime();
        long delay = Math.max(0L, (long) SurvivalConfig.PET_FAST_HEAL_COMBAT_DELAY_SECONDS.get()) * 20L;
        if (now - data.lastCombatGameTime() < delay) return;

        double threshold = Math.max(0.0D, Math.min(1.0D, SurvivalConfig.PET_FAST_HEAL_RESOURCE_THRESHOLD.get()));
        if (data.food() + 1.0E-6D < data.maxFood() * threshold) return;

        double requested = entity.getMaxHealth()
                * Math.max(0.0D, SurvivalConfig.PET_FAST_HEAL_MAX_HEALTH_RATE_PER_SECOND.get());
        requested = Math.min(requested, Math.max(0.0D, entity.getMaxHealth() - entity.getHealth()));
        requested = Math.min(requested, maxAffordablePetHealing(entity));
        if (requested <= 0.0D) return;

        float healed = healAndMeasure(entity, (float) requested);
        if (healed > 0.0F) chargePetHealing(entity, healed);
    }

    public static void beginManagedPetFeeding(LivingEntity entity) {
        if (entity != null && !entity.level().isClientSide && !(entity instanceof Player)
                && !ErsCompatApi.usesNativeHunger(entity) && TdmcCompat.isManagedPet(entity)) {
            MANAGED_PET_FEEDING.set(entity);
        }
    }

    public static void endManagedPetFeeding(LivingEntity entity) {
        if (MANAGED_PET_FEEDING.get() == entity) {
            MANAGED_PET_FEEDING.remove();
        }
    }

    public static boolean isManagedPetFeedingHealing(LivingEntity entity) {
        return entity != null && MANAGED_PET_FEEDING.get() == entity;
    }

    private static float healAndMeasure(LivingEntity entity, float amount) {
        if (entity == null || amount <= 0.0F || !Float.isFinite(amount) || !entity.isAlive()) return 0.0F;
        float before = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        float requested = Math.min(amount, Math.max(0.0F, maxHealth - before));
        if (requested <= 0.0F) return 0.0F;

        entity.heal(requested);
        float after = entity.getHealth();
        if (!(entity instanceof Player) && after <= before + 1.0E-6F) {
            entity.setHealth(Math.min(maxHealth, before + requested));
            after = entity.getHealth();
        }
        return Math.max(0.0F, after - before);
    }

    public static void updateThirst(Player player) {
        SurvivalData data = SurvivalData.of(player);
        boolean resourceThirst = data.water() < data.maxWater() * SurvivalConfig.LOW_RESOURCE_THRESHOLD.get();
        boolean temperatureThirst = player instanceof ServerPlayer serverPlayer
                && !serverPlayer.isCreative()
                && !serverPlayer.isSpectator()
                && TemperatureService.snapshot(serverPlayer).stage().isHot();
        boolean thirsty = resourceThirst || temperatureThirst;
        MobEffectInstance current = player.getEffect(ModEffects.THIRST.get());
        if (thirsty) {
            if (current == null || current.getDuration() <= 20) {
                player.addEffect(new MobEffectInstance(ModEffects.THIRST.get(), 60, 0, false, true, true));
            }
        } else if (current != null) {
            player.removeEffect(ModEffects.THIRST.get());
        }
    }

    public static boolean isThirsty(Player player) {
        SurvivalData data = SurvivalData.of(player);
        return data.water() < data.maxWater() * SurvivalConfig.LOW_RESOURCE_THRESHOLD.get();
    }

    public static boolean canTamingFeed(LivingEntity entity) {
        if (ErsCompatApi.usesNativeHunger(entity)) return true;
        SurvivalData data = SurvivalData.of(entity);
        return data.food() < data.maxFood() * SurvivalConfig.TAME_FEED_THRESHOLD.get();
    }

    public static double foodValue(ItemStack stack, LivingEntity consumer) {
        if (stack == null || stack.isEmpty()) {
            return 0.0D;
        }
        var foodProperties = stack.getFoodProperties(consumer);
        return foodProperties == null ? 0.0D : Math.max(0.0D, foodProperties.getNutrition() * 5.0D);
    }

    public static double foodValueForCreature(LivingEntity entity, ItemStack stack) {
        double direct = foodValue(stack, entity);
        if (direct > 0.0D) {
            return direct;
        }
        if (entity != null && stack != null && !stack.isEmpty() && TdmcTamingRules.isTamingFood(entity, stack)) {
            return creatureFallbackFeedRestore(entity);
        }
        return 0.0D;
    }

    private static double creatureFallbackFeedRestore(LivingEntity entity) {
        SurvivalData data = SurvivalData.of(entity);
        return Math.max(0.0D, SurvivalConfig.CREATURE_FOOD_FIXED_RESTORE.get()
                + data.maxFood() * SurvivalConfig.CREATURE_FOOD_PERCENT_RESTORE.get());
    }

    private static boolean isEntityHurt(LivingEntity entity) {
        return entity != null && entity.getHealth() < entity.getMaxHealth();
    }

    public static boolean creatureNeedsFood(LivingEntity entity) {
        if (ErsCompatApi.usesNativeHunger(entity)) return false;
        SurvivalData data = SurvivalData.of(entity);
        return data.food() + 1.0E-6D < data.maxFood();
    }

    public static boolean creatureNeedsFeeding(LivingEntity entity) {
        return entity != null && !ErsCompatApi.usesNativeHunger(entity) && (isEntityHurt(entity) || creatureNeedsFood(entity));
    }

    public static void feedManagedPet(LivingEntity entity, ItemStack stack) {
        if (entity == null || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity) || !TdmcCompat.isManagedPet(entity)) return;
        applyCreatureFeedBudget(entity, foodValueForCreature(entity, stack));
    }

    public static void restoreCreatureFood(LivingEntity entity, ItemStack stack) {
        if (entity == null || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity)) return;
        double value = foodValueForCreature(entity, stack);
        if (value <= 0.0D) return;
        SurvivalData data = SurvivalData.of(entity);
        data.food(data.food() + value);
    }

    private static boolean applyCreatureFeedBudget(LivingEntity entity, double feedBudget) {
        if (entity == null || feedBudget <= 0.0D || !Double.isFinite(feedBudget)) return false;
        SurvivalData data = SurvivalData.of(entity);
        double healCost = petFeedingFoodCostPerHealth();
        double missingHealth = Math.max(0.0D, entity.getMaxHealth() - entity.getHealth());
        double requestedHealing = healCost <= 0.0D
                ? missingHealth
                : Math.min(missingHealth, feedBudget / healCost);
        float healed = healAndMeasure(entity, (float) requestedHealing);
        double spentBudget = healCost <= 0.0D ? 0.0D : Math.min(feedBudget, healed * healCost);
        double remainingBudget = Math.max(0.0D, feedBudget - spentBudget);
        double beforeFood = data.food();
        data.food(beforeFood + remainingBudget);
        return healed > 0.0F || data.food() > beforeFood + 1.0E-6D;
    }

    public static int bestNonWastingBackpackFood(LivingEntity entity) {
        if (entity == null || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity) || !TdmcCompat.isManagedPet(entity)) return -1;
        SurvivalData data = SurvivalData.of(entity);
        double missing = Math.max(0.0D, data.maxFood() - data.food());
        if (missing <= 1.0E-6D) return -1;
        int bestSlot = -1;
        double bestValue = -1.0D;
        for (int slot = 0; slot < PetBackpackService.BACKPACK_SIZE; slot++) {
            ItemStack stack = PetBackpackService.getItem(entity, slot);
            if (stack.isEmpty() || !stack.isEdible()) continue;
            double value = foodValue(stack, entity);
            if (value <= 0.0D || value > missing + 1.0E-6D || value <= bestValue) continue;
            bestValue = value;
            bestSlot = slot;
        }
        return bestSlot;
    }

    public static void tickPetBackpackAutoFeed(LivingEntity entity) {
        if (entity == null || entity.level().isClientSide || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity) || !TdmcCompat.isManagedPet(entity)) return;
        int slot = bestNonWastingBackpackFood(entity);
        if (slot < 0) return;
        ItemStack stack = PetBackpackService.getItem(entity, slot);
        double value = foodValue(stack, entity);
        if (value <= 0.0D) return;
        SurvivalData data = SurvivalData.of(entity);
        if (value > data.maxFood() - data.food() + 1.0E-6D) return;
        stack.shrink(1);
        PetBackpackService.setItem(entity, slot, stack);
        data.food(data.food() + value);
    }

    public static boolean forceUseBackpackFood(Player player, LivingEntity entity, ItemStack stack) {
        if (player == null || entity == null || stack == null || stack.isEmpty()
                || entity instanceof Player || ErsCompatApi.usesNativeHunger(entity)
                || !TdmcCompat.isManagedPet(entity) || !TdmcCompat.isOwnedBy(entity, player)) {
            return false;
        }
        double budget = foodValue(stack, entity);
        if (budget <= 0.0D) return false;
        if (!applyCreatureFeedBudget(entity, budget)) return false;
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        TdmcCompat.rememberManagedPet(entity);
        return true;
    }

    public static double hydrationFor(ItemStack stack) {
        List<String> effectiveRules = HydrationRules.effectiveRules(
                SurvivalConfig.HYDRATION_ITEMS.get().stream().map(String::valueOf).toList(),
                SurvivalConfig.USE_BUILTIN_FOOD_DRINK_COMPATIBILITY.get(),
                SurvivalConfig.BUILTIN_HYDRATION_RULES.get().stream().map(String::valueOf).toList()
        );
        return HydrationRules.hydrationFor(
                stack,
                effectiveRules,
                SurvivalConfig.DEFAULT_DRINK_WATER_RESTORE.get()
        );
    }

    public static void consumeAttackStamina(LivingEntity attacker) {
        if (attacker instanceof Player player && (player.isCreative() || player.isSpectator())) return;
        if (!(attacker instanceof Player) && !TdmcCompat.isManagedPet(attacker)) return;
        SurvivalData data = SurvivalData.of(attacker);
        double sideMultiplier = attacker instanceof Player ? SurvivalConfig.PLAYER_STAMINA_COST_MULTIPLIER.get() : SurvivalConfig.PET_STAMINA_COST_MULTIPLIER.get();
        double cost = SurvivalMath.standardStaminaCost(attacker) * SurvivalConfig.ATTACK_STAMINA_COST_MULTIPLIER.get() * sideMultiplier;
        data.stamina(data.stamina() - cost);
        data.lastAction(attacker.level().getGameTime());
    }

    public static boolean consumeBoostStamina(LivingEntity entity, BoostMode mode) {
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return true;
        SurvivalData data = SurvivalData.of(entity);
        if (entity instanceof Player && data.water() <= 0.0D) return false;
        if (data.stamina() <= 0.0D) return false;
        double modeMultiplier = switch (mode) {
            case GROUND -> SurvivalConfig.GROUND_STAMINA_COST_MULTIPLIER.get();
            case SWIM -> SurvivalConfig.SWIM_STAMINA_COST_MULTIPLIER.get();
            case FLIGHT -> SurvivalConfig.FLIGHT_STAMINA_COST_MULTIPLIER.get();
        };
        double sideMultiplier = entity instanceof Player ? SurvivalConfig.PLAYER_STAMINA_COST_MULTIPLIER.get() : SurvivalConfig.PET_STAMINA_COST_MULTIPLIER.get();
        double perSecond = SurvivalMath.standardStaminaCost(entity) * modeMultiplier * sideMultiplier;
        data.stamina(data.stamina() - perSecond / 20.0D);
        data.lastAction(entity.level().getGameTime());
        return data.stamina() > 0.0D;
    }


    public static boolean consumePassiveFlightStamina(LivingEntity entity) {
        if (entity == null) return false;
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return true;
        if (!(entity instanceof Player) && !TdmcCompat.isManagedPet(entity)) return false;
        SurvivalData data = SurvivalData.of(entity);
        if (data.stamina() <= 0.0D) return false;
        double sideMultiplier = entity instanceof Player
                ? SurvivalConfig.PLAYER_STAMINA_COST_MULTIPLIER.get()
                : SurvivalConfig.PET_STAMINA_COST_MULTIPLIER.get();
        double perSecond = SurvivalMath.standardStaminaCost(entity)
                * SurvivalConfig.PASSIVE_FLIGHT_STAMINA_COST_MULTIPLIER.get()
                * sideMultiplier;
        if (perSecond > 0.0D && Double.isFinite(perSecond)) {
            data.stamina(data.stamina() - perSecond / 20.0D);
            data.lastAction(entity.level().getGameTime());
        }
        return data.stamina() > 0.0D;
    }

    public static void tickStaminaRegeneration(LivingEntity entity, boolean moving, boolean active) {
        if (active) return;
        SurvivalData data = SurvivalData.of(entity);
        long idleTicks = entity.level().getGameTime() - data.lastAction();
        double percent = !moving && idleTicks >= 20 ? SurvivalConfig.STAMINA_STILL_REGEN_PERCENT.get() : SurvivalConfig.STAMINA_MOVING_REGEN_PERCENT.get();
        if (entity instanceof Player player && isThirsty(player)) percent *= SurvivalConfig.THIRST_STAMINA_REGEN_MULTIPLIER.get();
        if (entity instanceof Player player) percent *= temperatureRecoveryMultiplier(player);
        data.stamina(data.stamina() + data.maxStamina() * percent / 20.0D);
    }

    public static double naturalFoodDrainPerSecond(LivingEntity entity, boolean anesthetized) {
        if (entity == null || ErsCompatApi.usesNativeHunger(entity)) return 0.0D;
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) return 0.0D;
        SurvivalData data = SurvivalData.of(entity);
        double sideMultiplier = entity instanceof Player ? SurvivalConfig.PLAYER_FOOD_DRAIN_MULTIPLIER.get() : SurvivalConfig.PET_FOOD_DRAIN_MULTIPLIER.get();
        double anesthesiaMultiplier = anesthetized ? SurvivalConfig.ANESTHESIA_FOOD_DRAIN_MULTIPLIER.get() : 1.0D;
        double temperatureMultiplier = temperatureFoodDrainMultiplier(entity);
        double result = data.maxFood()
                * SurvivalConfig.FOOD_DRAIN_PER_MINUTE.get()
                * sideMultiplier
                * anesthesiaMultiplier
                * temperatureMultiplier
                / 60.0D;
        return Double.isFinite(result) ? Math.max(0.0D, result) : 0.0D;
    }

    public static void drainNaturalFood(LivingEntity entity, boolean anesthetized) {
        double amount = naturalFoodDrainPerSecond(entity, anesthetized);
        if (amount <= 0.0D) return;
        SurvivalData.of(entity).drainFood(amount);
    }

    public static void drainNaturalWater(Player player, boolean active) {
        if (player.isCreative() || player.isSpectator()) return;
        SurvivalData data = SurvivalData.of(player);
        double activeMultiplier = active ? SurvivalConfig.ACTIVE_WATER_DRAIN_MULTIPLIER.get() : 1.0D;
        double temperatureMultiplier = temperatureWaterDrainMultiplier(player);
        data.water(data.water() - data.maxWater() * SurvivalConfig.WATER_DRAIN_PER_MINUTE.get() * SurvivalConfig.PLAYER_WATER_DRAIN_MULTIPLIER.get() * activeMultiplier * temperatureMultiplier / 60.0D);
    }

    private static double temperatureRecoveryMultiplier(Player player) {
        if (player instanceof ServerPlayer serverPlayer
                && !serverPlayer.isCreative()
                && !serverPlayer.isSpectator()
                && TemperatureService.snapshot(serverPlayer).stage().isSevere()) {
            return SurvivalConfig.TEMPERATURE_SEVERE_RECOVERY_MULTIPLIER.get();
        }
        return 1.0D;
    }

    private static double temperatureFoodDrainMultiplier(LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer
                && !serverPlayer.isCreative()
                && !serverPlayer.isSpectator()
                && TemperatureService.snapshot(serverPlayer).stage().isCold()) {
            return SurvivalConfig.TEMPERATURE_WARNING_RESOURCE_DRAIN_MULTIPLIER.get();
        }
        return 1.0D;
    }

    private static double temperatureWaterDrainMultiplier(Player player) {
        if (player instanceof ServerPlayer serverPlayer
                && !serverPlayer.isCreative()
                && !serverPlayer.isSpectator()
                && TemperatureService.snapshot(serverPlayer).stage().isHot()) {
            return SurvivalConfig.TEMPERATURE_WARNING_RESOURCE_DRAIN_MULTIPLIER.get();
        }
        return 1.0D;
    }

    public static boolean hasConfiguredAnesthesia(LivingEntity entity) {
        if (entity == null) return false;
        if (TorporApi.isUnconscious(entity)) {
            return true;
        }
        for (MobEffectInstance effect : entity.getActiveEffects()) {
            ResourceLocation id = ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect());
            if (id != null && SurvivalConfig.ANESTHESIA_EFFECTS.get().contains(id.toString())) return true;
        }
        return false;
    }

    public static void initializePlayer(ServerPlayer player) {
        SurvivalData data = SurvivalData.of(player);
        var health = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        boolean raisedHealthBase = health != null && health.getBaseValue() < 100.0D;
        if (raisedHealthBase) {
            health.setBaseValue(100.0D);
        }
        if (!data.healthBaseInitialized()) {
            player.setHealth(player.getMaxHealth());
            data.healthBaseInitialized(true);
        }
        data.clamp();
    }

    public enum BoostMode {
        GROUND,
        SWIM,
        FLIGHT
    }
}
