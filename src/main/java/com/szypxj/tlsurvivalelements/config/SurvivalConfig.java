package com.szypxj.tlsurvivalelements.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class SurvivalConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue PLAYER_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PLAYER_WATER_DRAIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PLAYER_FOOD_DRAIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PET_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PET_FOOD_DRAIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue FOOD_DRAIN_PER_MINUTE;
    public static final ForgeConfigSpec.DoubleValue WATER_DRAIN_PER_MINUTE;
    public static final ForgeConfigSpec.DoubleValue ACTIVE_WATER_DRAIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ANESTHESIA_FOOD_DRAIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PLAYER_HEALING_FOOD_COST_PER_HEALTH;
    public static final ForgeConfigSpec.DoubleValue PLAYER_HEALING_WATER_COST_PER_HEALTH;
    public static final ForgeConfigSpec.IntValue PLAYER_FAST_HEAL_COMBAT_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue PLAYER_FAST_HEAL_RESOURCE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue PLAYER_FAST_HEAL_MAX_HEALTH_RATE_PER_SECOND;
    public static final ForgeConfigSpec.DoubleValue PET_HEALING_FOOD_COST_PER_HEALTH;
    public static final ForgeConfigSpec.DoubleValue PET_NATURAL_HEAL_FOOD_THRESHOLD;
    public static final ForgeConfigSpec.IntValue PET_NATURAL_HEAL_INTERVAL_SECONDS;
    public static final ForgeConfigSpec.DoubleValue PET_NATURAL_HEAL_AMOUNT;
    public static final ForgeConfigSpec.IntValue PET_FAST_HEAL_COMBAT_DELAY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue PET_FAST_HEAL_RESOURCE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue PET_FAST_HEAL_MAX_HEALTH_RATE_PER_SECOND;
    public static final ForgeConfigSpec.DoubleValue LOW_RESOURCE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue TAME_FEED_THRESHOLD;
    public static final ForgeConfigSpec.IntValue TAMING_TORPOR_RECOVERY_SAFETY_SECONDS;
    public static final ForgeConfigSpec.DoubleValue CREATURE_FOOD_FIXED_RESTORE;
    public static final ForgeConfigSpec.DoubleValue CREATURE_FOOD_PERCENT_RESTORE;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_DRINK_WATER_RESTORE;
    public static final ForgeConfigSpec.DoubleValue STAMINA_STILL_REGEN_PERCENT;
    public static final ForgeConfigSpec.DoubleValue STAMINA_MOVING_REGEN_PERCENT;
    public static final ForgeConfigSpec.DoubleValue THIRST_STAMINA_REGEN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue BOOST_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue GROUND_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue SWIM_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue FLIGHT_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_FLIGHT_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ATTACK_STAMINA_COST_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MIN_STAMINA_COST;
    public static final ForgeConfigSpec.DoubleValue MAX_STAMINA_COST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> HYDRATION_ITEMS;
    public static final ForgeConfigSpec.BooleanValue USE_BUILTIN_FOOD_DRINK_COMPATIBILITY;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BUILTIN_HYDRATION_RULES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> STAMINA_COST_OVERRIDES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ANESTHESIA_EFFECTS;

    public static final ForgeConfigSpec.BooleanValue WILD_FEEDING_ENABLED;
    public static final ForgeConfigSpec.DoubleValue WILD_FORAGING_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue WILD_RESERVE_CAPACITY_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WILD_RESERVE_TRANSFER_PERCENT_PER_SECOND;
    public static final ForgeConfigSpec.IntValue WILD_HERBIVORE_SCAN_MIN_SECONDS;
    public static final ForgeConfigSpec.IntValue WILD_HERBIVORE_SCAN_MAX_SECONDS;
    public static final ForgeConfigSpec.IntValue WILD_GROUND_FOOD_SCAN_MIN_SECONDS;
    public static final ForgeConfigSpec.IntValue WILD_GROUND_FOOD_SCAN_MAX_SECONDS;
    public static final ForgeConfigSpec.IntValue WILD_HUNT_SCAN_MIN_SECONDS;
    public static final ForgeConfigSpec.IntValue WILD_HUNT_SCAN_MAX_SECONDS;
    public static final ForgeConfigSpec.DoubleValue WILD_SEARCH_RADIUS;
    public static final ForgeConfigSpec.DoubleValue WILD_PREDATION_SEARCH_RADIUS;
    public static final ForgeConfigSpec.DoubleValue WILD_CARNIVORE_HUNT_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue WILD_OMNIVORE_HUNT_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue WILD_DESTROY_PLANTS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WILD_DIET_PROFILE_OVERRIDES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WILD_INEDIBLE_ENTITIES;
    public static final ForgeConfigSpec.DoubleValue WILD_CARCASS_MIN_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue WILD_CARCASS_MAX_NUTRITION;
    public static final ForgeConfigSpec.DoubleValue WILD_CARCASS_HEALTH_WEIGHT;
    public static final ForgeConfigSpec.DoubleValue WILD_CARCASS_VOLUME_WEIGHT;
    public static final ForgeConfigSpec.DoubleValue WILD_ENVIRONMENT_NUTRITION_PERCENT;
    public static final ForgeConfigSpec.DoubleValue WILD_TAGGED_FOOD_FALLBACK_PERCENT;
    public static final ForgeConfigSpec.DoubleValue WILD_CARNIVORE_NUTRITION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WILD_HERBIVORE_NUTRITION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WILD_SCAVENGER_NUTRITION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue WILD_OMNIVORE_NUTRITION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue STARVATION_DAMAGE_MAX_HEALTH_PERCENT_PER_SECOND;
    public static final ForgeConfigSpec.DoubleValue DEHYDRATION_DAMAGE_MAX_HEALTH_PERCENT_PER_SECOND;

    public static final ForgeConfigSpec.BooleanValue TEMPERATURE_ENABLED;
    public static final ForgeConfigSpec.DoubleValue BASE_MINIMUM_TEMPERATURE;
    public static final ForgeConfigSpec.DoubleValue BASE_MAXIMUM_TEMPERATURE;
    public static final ForgeConfigSpec.DoubleValue BIOME_TEMPERATURE_OFFSET;
    public static final ForgeConfigSpec.DoubleValue BIOME_TEMPERATURE_SCALE;
    public static final ForgeConfigSpec.IntValue TEMPERATURE_SEA_LEVEL;
    public static final ForgeConfigSpec.DoubleValue COOLING_PER_BLOCK_ABOVE_SEA_LEVEL;
    public static final ForgeConfigSpec.DoubleValue WARMING_PER_BLOCK_BELOW_SEA_LEVEL;
    public static final ForgeConfigSpec.DoubleValue RAIN_TEMPERATURE_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue THUNDER_TEMPERATURE_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue TDMC_RESISTANCE_PER_POINT;
    public static final ForgeConfigSpec.DoubleValue TEMPERATURE_WARNING_RESOURCE_DRAIN_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue TEMPERATURE_SEVERE_THRESHOLD_PERCENT;
    public static final ForgeConfigSpec.DoubleValue TEMPERATURE_CRITICAL_THRESHOLD_PERCENT;
    public static final ForgeConfigSpec.DoubleValue TEMPERATURE_SEVERE_RECOVERY_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue TEMPERATURE_CRITICAL_HEALTH_DAMAGE_PERCENT;
    public static final ForgeConfigSpec.IntValue TEMPERATURE_SOURCE_SCAN_INTERVAL_TICKS;
    public static final ForgeConfigSpec.DoubleValue MAXIMUM_SOURCE_TEMPERATURE_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TEMPERATURE_SOURCE_RULES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EQUIPMENT_TEMPERATURE_RULES;


    private static final List<String> DEFAULT_BUILTIN_HYDRATION_RULES = List.of(
            "minecraft:apple=8",
            "minecraft:melon_slice=12",
            "minecraft:sweet_berries=5",
            "minecraft:glow_berries=5",
            "minecraft:chorus_fruit=6",
            "#forge:fruits=8",

            "farmersdelight:milk_bottle=20",
            "farmersdelight:hot_cocoa=15",
            "farmersdelight:apple_cider=12",
            "#farmersdelight:drinks=25",

            "fruitsdelight:mango_milkshake=20",
            "fruitsdelight:bellini_cocktail=12",
            "#fruitsdelight:juice=25",

            "arsdelight:mendosteen_tea=25",
            "arsdelight:bastion_tea=25",
            "arsdelight:bombegrante_tea=25",
            "arsdelight:frostaya_tea=25",
            "arsdelight:source_berry_tea=25",
            "arsdelight:flashpine_tea=25",
            "arsdelight:mendosteen_hornbeer=12",
            "arsdelight:bastion_hornbeer=12",
            "arsdelight:bombegrante_hornbeer=12",
            "arsdelight:frostaya_hornbeer=12",
            "arsdelight:source_berry_hornbeer=12",
            "arsdelight:flashpine_hornbeer=12",
            "arsdelight:unstable_cocktail=12",

            "immortalers_delight:abc_offee=15",
            "immortalers_delight:abblue_beauty_c_offee=15",
            "immortalers_delight:jvav_offee=15",
            "immortalers_delight:aromatic_pod_affogato=15",
            "immortalers_delight:british_yellow_tea=25",
            "immortalers_delight:carrot_tea=25",
            "immortalers_delight:cherry_pearlipearl_tea=25",
            "immortalers_delight:contains_tea_leisamboo=25",
            "immortalers_delight:fruit_tea=25",
            "immortalers_delight:iced_black_tea=25",
            "immortalers_delight:leaf_green_tea=25",
            "immortalers_delight:leaf_tea=25",
            "immortalers_delight:leisamboo_tea=25",
            "immortalers_delight:soul_tea=25",
            "immortalers_delight:stove_black_tea=25",
            "immortalers_delight:travarice_tea=25",
            "immortalers_delight:bottle_melon_juice=25",
            "immortalers_delight:evolutcorn_juice=25",
            "immortalers_delight:glistering_watermelon_juice=25",
            "immortalers_delight:obsidian_walnut_milk=20",
            "immortalers_delight:pearlip_bubble_milk=20",
            "immortalers_delight:pearlip_milk_shake=20",
            "immortalers_delight:pearlipearl_milk_green=20",
            "immortalers_delight:pearlipearl_milk_tea=20",
            "immortalers_delight:yogurt=20",
            "immortalers_delight:sparkling_water=30",
            "immortalers_delight:glistering_fizz=20",
            "immortalers_delight:green_tea_fizz=20",
            "immortalers_delight:morning_fizz=20",
            "immortalers_delight:rainbow_fizz=20",
            "immortalers_delight:sextlotus_fizz=20",
            "immortalers_delight:tropical_fruity_cyclone=25",
            "immortalers_delight:clear_water_vodka=12",
            "immortalers_delight:dreumk_wine=12",
            "immortalers_delight:evolutcorn_beer=12",
            "immortalers_delight:gleeman_tear=12",
            "immortalers_delight:lonely_spirit_wine=12",
            "immortalers_delight:nether_kvass=12",
            "immortalers_delight:pearlip_beer=12",
            "immortalers_delight:piglin_odori_sake=12",
            "immortalers_delight:purgatory_ale=12",
            "immortalers_delight:sparrow_wine=12",
            "immortalers_delight:sticky_beer=12",
            "immortalers_delight:traveer=12",
            "immortalers_delight:vulcan_coktail=12"
    );

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("player");
        PLAYER_STAMINA_COST_MULTIPLIER = builder.defineInRange("staminaCostMultiplier", 1.0D, 0.0D, 100.0D);
        PLAYER_WATER_DRAIN_MULTIPLIER = builder.defineInRange("waterDrainMultiplier", 1.0D, 0.0D, 100.0D);
        PLAYER_FOOD_DRAIN_MULTIPLIER = builder.defineInRange("foodDrainMultiplier", 1.0D, 0.0D, 100.0D);
        builder.pop();

        builder.push("pet");
        PET_STAMINA_COST_MULTIPLIER = builder.defineInRange("staminaCostMultiplier", 1.0D, 0.0D, 100.0D);
        PET_FOOD_DRAIN_MULTIPLIER = builder.defineInRange("foodDrainMultiplier", 1.0D, 0.0D, 100.0D);
        builder.pop();

        builder.push("food");
        FOOD_DRAIN_PER_MINUTE = builder.defineInRange("naturalDrainPercentPerMinute", 0.01D, 0.0D, 1.0D);
        ANESTHESIA_FOOD_DRAIN_MULTIPLIER = builder.defineInRange("anesthesiaDrainMultiplier", 5.0D, 1.0D, 100.0D);
        CREATURE_FOOD_FIXED_RESTORE = builder.defineInRange("creatureFixedRestore", 5.0D, 0.0D, 100000.0D);
        CREATURE_FOOD_PERCENT_RESTORE = builder.defineInRange("creaturePercentRestore", 0.01D, 0.0D, 1.0D);
        TAME_FEED_THRESHOLD = builder.defineInRange("tamingFeedCurrentPercentMaximum", 0.90D, 0.0D, 1.0D);
        TAMING_TORPOR_RECOVERY_SAFETY_SECONDS = builder.defineInRange("tamingTorporRecoverySafetySeconds", 30, 0, 3600);
        builder.pop();

        builder.push("healing");
        PLAYER_HEALING_FOOD_COST_PER_HEALTH = builder.defineInRange("playerFoodCostPerHealth", 0.5D, 0.0D, 100000.0D);
        PLAYER_HEALING_WATER_COST_PER_HEALTH = builder.defineInRange("playerWaterCostPerHealth", 0.5D, 0.0D, 100000.0D);
        PLAYER_FAST_HEAL_COMBAT_DELAY_SECONDS = builder.defineInRange("playerFastHealCombatDelaySeconds", 8, 0, 3600);
        PLAYER_FAST_HEAL_RESOURCE_THRESHOLD = builder.defineInRange("playerFastHealResourceThreshold", 0.50D, 0.0D, 1.0D);
        PLAYER_FAST_HEAL_MAX_HEALTH_RATE_PER_SECOND = builder.defineInRange("playerFastHealMaxHealthRatePerSecond", 0.02D, 0.0D, 1.0D);
        PET_HEALING_FOOD_COST_PER_HEALTH = builder.defineInRange("petFoodCostPerHealth", 0.5D, 0.0D, 100000.0D);
        PET_NATURAL_HEAL_FOOD_THRESHOLD = builder.defineInRange("petNaturalHealFoodThreshold", 0.90D, 0.0D, 1.0D);
        PET_NATURAL_HEAL_INTERVAL_SECONDS = builder.defineInRange("petNaturalHealIntervalSeconds", 4, 1, 3600);
        PET_NATURAL_HEAL_AMOUNT = builder.defineInRange("petNaturalHealAmount", 1.0D, 0.0D, 1_000_000.0D);
        PET_FAST_HEAL_COMBAT_DELAY_SECONDS = builder.defineInRange("petFastHealCombatDelaySeconds", 8, 0, 3600);
        PET_FAST_HEAL_RESOURCE_THRESHOLD = builder.defineInRange("petFastHealResourceThreshold", 0.50D, 0.0D, 1.0D);
        PET_FAST_HEAL_MAX_HEALTH_RATE_PER_SECOND = builder.defineInRange("petFastHealMaxHealthRatePerSecond", 0.02D, 0.0D, 1.0D);
        builder.pop();

        builder.push("water");
        WATER_DRAIN_PER_MINUTE = builder.defineInRange("naturalDrainPercentPerMinute", 0.01D, 0.0D, 1.0D);
        ACTIVE_WATER_DRAIN_MULTIPLIER = builder.defineInRange("activeDrainMultiplier", 2.0D, 1.0D, 100.0D);
        DEFAULT_DRINK_WATER_RESTORE = builder.defineInRange("defaultPotionRestore", 30.0D, 0.0D, 100000.0D);
        HYDRATION_ITEMS = builder.defineListAllowEmpty("itemRestoreRules", List.of(), SurvivalConfig::isRule);
        USE_BUILTIN_FOOD_DRINK_COMPATIBILITY = builder.define("useBuiltinFoodDrinkCompatibility", true);
        BUILTIN_HYDRATION_RULES = builder.defineListAllowEmpty("builtinFoodDrinkRules", DEFAULT_BUILTIN_HYDRATION_RULES, SurvivalConfig::isRule);
        builder.pop();

        builder.push("wildFeeding");
        WILD_FEEDING_ENABLED = builder.define("enabled", true);
        WILD_FORAGING_THRESHOLD = builder.defineInRange("foragingThreshold", 0.70D, 0.0D, 1.0D);
        WILD_RESERVE_CAPACITY_MULTIPLIER = builder.defineInRange("reserveCapacityMultiplier", 1.0D, 0.0D, 100.0D);
        WILD_RESERVE_TRANSFER_PERCENT_PER_SECOND = builder.defineInRange("reserveTransferPercentPerSecond", 0.05D, 0.0D, 1.0D);
        WILD_HERBIVORE_SCAN_MIN_SECONDS = builder.defineInRange("herbivoreScanMinSeconds", 10, 1, 3600);
        WILD_HERBIVORE_SCAN_MAX_SECONDS = builder.defineInRange("herbivoreScanMaxSeconds", 30, 1, 3600);
        WILD_GROUND_FOOD_SCAN_MIN_SECONDS = builder.defineInRange("groundFoodScanMinSeconds", 5, 1, 3600);
        WILD_GROUND_FOOD_SCAN_MAX_SECONDS = builder.defineInRange("groundFoodScanMaxSeconds", 15, 1, 3600);
        WILD_HUNT_SCAN_MIN_SECONDS = builder.defineInRange("huntScanMinSeconds", 5, 1, 3600);
        WILD_HUNT_SCAN_MAX_SECONDS = builder.defineInRange("huntScanMaxSeconds", 15, 1, 3600);
        WILD_SEARCH_RADIUS = builder.defineInRange("searchRadius", 8.0D, 1.0D, 64.0D);
        WILD_PREDATION_SEARCH_RADIUS = builder.defineInRange("predationSearchRadius", 16.0D, 1.0D, 128.0D);
        WILD_CARNIVORE_HUNT_THRESHOLD = builder.defineInRange("carnivoreHuntThreshold", 0.60D, 0.0D, 1.0D);
        WILD_OMNIVORE_HUNT_THRESHOLD = builder.defineInRange("omnivoreHuntThreshold", 0.30D, 0.0D, 1.0D);
        WILD_DESTROY_PLANTS = builder.define("destroyPlants", false);
        WILD_DIET_PROFILE_OVERRIDES = builder.defineListAllowEmpty("profileOverrides", List.of(), o -> o instanceof String);
        WILD_INEDIBLE_ENTITIES = builder.defineListAllowEmpty("inedibleEntities", List.of(), o -> o instanceof String);
        WILD_CARCASS_MIN_NUTRITION = builder.defineInRange("carcassMinimumNutrition", 2.0D, 0.0D, 1_000_000.0D);
        WILD_CARCASS_MAX_NUTRITION = builder.defineInRange("carcassMaximumNutrition", 200.0D, 0.0D, 1_000_000.0D);
        WILD_CARCASS_HEALTH_WEIGHT = builder.defineInRange("carcassHealthWeight", 0.25D, 0.0D, 1000.0D);
        WILD_CARCASS_VOLUME_WEIGHT = builder.defineInRange("carcassVolumeWeight", 4.0D, 0.0D, 1000.0D);
        WILD_ENVIRONMENT_NUTRITION_PERCENT = builder.defineInRange("environmentNutritionPercent", 0.10D, 0.0D, 1.0D);
        WILD_TAGGED_FOOD_FALLBACK_PERCENT = builder.defineInRange("taggedFoodFallbackPercent", 0.05D, 0.0D, 1.0D);
        WILD_CARNIVORE_NUTRITION_MULTIPLIER = builder.defineInRange("carnivoreNutritionMultiplier", 1.0D, 0.0D, 100.0D);
        WILD_HERBIVORE_NUTRITION_MULTIPLIER = builder.defineInRange("herbivoreNutritionMultiplier", 1.0D, 0.0D, 100.0D);
        WILD_SCAVENGER_NUTRITION_MULTIPLIER = builder.defineInRange("scavengerNutritionMultiplier", 0.90D, 0.0D, 100.0D);
        WILD_OMNIVORE_NUTRITION_MULTIPLIER = builder.defineInRange("omnivoreNutritionMultiplier", 0.75D, 0.0D, 100.0D);
        builder.pop();

        builder.push("survivalDamage");
        STARVATION_DAMAGE_MAX_HEALTH_PERCENT_PER_SECOND = builder.defineInRange("starvationMaxHealthPercentPerSecond", 0.001D, 0.0D, 1.0D);
        DEHYDRATION_DAMAGE_MAX_HEALTH_PERCENT_PER_SECOND = builder.defineInRange("dehydrationMaxHealthPercentPerSecond", 0.001D, 0.0D, 1.0D);
        builder.pop();

        builder.push("stamina");
        STAMINA_STILL_REGEN_PERCENT = builder.defineInRange("stillRegenPercentPerSecond", 0.05D, 0.0D, 1.0D);
        STAMINA_MOVING_REGEN_PERCENT = builder.defineInRange("movingRegenPercentPerSecond", 0.01D, 0.0D, 1.0D);
        THIRST_STAMINA_REGEN_MULTIPLIER = builder.defineInRange("thirstRegenMultiplier", 0.5D, 0.0D, 1.0D);
        BOOST_SPEED_MULTIPLIER = builder.defineInRange("boostSpeedMultiplier", 1.30D, 1.0D, 8.0D);
        GROUND_STAMINA_COST_MULTIPLIER = builder.defineInRange("groundCostMultiplier", 1.0D, 0.0D, 100.0D);
        SWIM_STAMINA_COST_MULTIPLIER = builder.defineInRange("swimCostMultiplier", 1.0D, 0.0D, 100.0D);
        FLIGHT_STAMINA_COST_MULTIPLIER = builder.defineInRange("flightCostMultiplier", 1.25D, 0.0D, 100.0D);
        PASSIVE_FLIGHT_STAMINA_COST_MULTIPLIER = builder.defineInRange("passiveFlightCostMultiplier", 0.25D, 0.0D, 100.0D);
        ATTACK_STAMINA_COST_MULTIPLIER = builder.defineInRange("attackCostMultiplier", 1.0D, 0.0D, 100.0D);
        MIN_STAMINA_COST = builder.defineInRange("minimumSpeciesCost", 0.5D, 0.0D, 100000.0D);
        MAX_STAMINA_COST = builder.defineInRange("maximumSpeciesCost", 8.0D, 0.0D, 100000.0D);
        STAMINA_COST_OVERRIDES = builder.defineListAllowEmpty("entityCostOverrides", List.of(), SurvivalConfig::isRule);
        builder.pop();

        builder.push("temperature");
        TEMPERATURE_ENABLED = builder.define("enabled", true);
        BASE_MINIMUM_TEMPERATURE = builder.defineInRange("baseMinimumTemperature", 5.0D, -200.0D, 200.0D);
        BASE_MAXIMUM_TEMPERATURE = builder.defineInRange("baseMaximumTemperature", 35.0D, -200.0D, 200.0D);
        BIOME_TEMPERATURE_OFFSET = builder.defineInRange("biomeTemperatureOffset", 5.0D, -200.0D, 200.0D);
        BIOME_TEMPERATURE_SCALE = builder.defineInRange("biomeTemperatureScale", 18.75D, 0.0D, 200.0D);
        TEMPERATURE_SEA_LEVEL = builder.defineInRange("seaLevel", 63, -2048, 2048);
        COOLING_PER_BLOCK_ABOVE_SEA_LEVEL = builder.defineInRange("coolingPerBlockAboveSeaLevel", 0.08D, 0.0D, 10.0D);
        WARMING_PER_BLOCK_BELOW_SEA_LEVEL = builder.defineInRange("warmingPerBlockBelowSeaLevel", 0.02D, 0.0D, 10.0D);
        RAIN_TEMPERATURE_MODIFIER = builder.defineInRange("rainTemperatureModifier", -3.0D, -100.0D, 100.0D);
        THUNDER_TEMPERATURE_MODIFIER = builder.defineInRange("thunderTemperatureModifier", -5.0D, -100.0D, 100.0D);
        TDMC_RESISTANCE_PER_POINT = builder.defineInRange("tdmcResistancePerPoint", 1.0D, 0.0D, 1000.0D);
        TEMPERATURE_WARNING_RESOURCE_DRAIN_MULTIPLIER = builder.defineInRange("warningResourceDrainMultiplier", 1.20D, 1.0D, 100.0D);
        TEMPERATURE_SEVERE_THRESHOLD_PERCENT = builder.defineInRange("severeThresholdPercent", 0.20D, 0.0D, 10.0D);
        TEMPERATURE_CRITICAL_THRESHOLD_PERCENT = builder.defineInRange("criticalThresholdPercent", 0.50D, 0.0D, 10.0D);
        TEMPERATURE_SEVERE_RECOVERY_MULTIPLIER = builder.defineInRange("severeRecoveryMultiplier", 0.80D, 0.0D, 1.0D);
        TEMPERATURE_CRITICAL_HEALTH_DAMAGE_PERCENT = builder.defineInRange("criticalHealthDamagePercentPerSecond", 0.01D, 0.0D, 1.0D);
        TEMPERATURE_SOURCE_SCAN_INTERVAL_TICKS = builder.defineInRange("sourceScanIntervalTicks", 20, 1, 1200);
        MAXIMUM_SOURCE_TEMPERATURE_MODIFIER = builder.defineInRange("maximumSourceTemperatureModifier", 20.0D, 0.0D, 1000.0D);
        TEMPERATURE_SOURCE_RULES = builder.defineListAllowEmpty("sourceRules", List.of(
                "fluid:#minecraft:lava=12.0@5",
                "block:minecraft:fire=8.0@4",
                "block:minecraft:campfire=6.0@5",
                "block:minecraft:soul_campfire=5.0@5",
                "block:minecraft:magma_block=4.0@3",
                "fluid:#minecraft:water=-3.0@3",
                "block:minecraft:ice=-4.0@3",
                "block:minecraft:packed_ice=-6.0@4",
                "block:minecraft:blue_ice=-8.0@5",
                "block:minecraft:powder_snow=-10.0@3"
        ), SurvivalConfig::isTemperatureSourceRule);
        EQUIPMENT_TEMPERATURE_RULES = builder.defineListAllowEmpty("equipmentResistanceRules", List.of(
                "minecraft:leather_helmet=1.0|2.0",
                "minecraft:leather_chestplate=2.0|4.0",
                "minecraft:leather_leggings=1.0|3.0",
                "minecraft:leather_boots=1.0|2.0",
                "minecraft:netherite_helmet=2.0|0.0",
                "minecraft:netherite_chestplate=4.0|0.0",
                "minecraft:netherite_leggings=3.0|0.0",
                "minecraft:netherite_boots=2.0|0.0"
        ), SurvivalConfig::isEquipmentTemperatureRule);
        builder.pop();

        LOW_RESOURCE_THRESHOLD = builder.defineInRange("lowResourceThreshold", 0.20D, 0.0D, 1.0D);
        ANESTHESIA_EFFECTS = builder.defineListAllowEmpty("anesthesiaEffectIds", List.of(
                "tl_domesticate_more_creatures:torpor",
                "tl_domesticate_more_creatures:unconscious",
                "tl_domesticate_more_creatures:anesthesia"
        ), o -> o instanceof String);

        SPEC = builder.build();
    }

    private SurvivalConfig() {
    }

    private static boolean isTemperatureSourceRule(Object value) {
        if (!(value instanceof String text)) return false;
        int colon = text.indexOf(':');
        int equals = text.lastIndexOf('=');
        int at = text.lastIndexOf('@');
        if (colon <= 0 || equals <= colon + 1 || at <= equals + 1 || at >= text.length() - 1) return false;
        String kind = text.substring(0, colon).trim();
        if (!kind.equals("block") && !kind.equals("fluid")) return false;
        try {
            Double.parseDouble(text.substring(equals + 1, at).trim());
            return Integer.parseInt(text.substring(at + 1).trim()) > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static boolean isEquipmentTemperatureRule(Object value) {
        if (!(value instanceof String text)) return false;
        int equals = text.lastIndexOf('=');
        int separator = text.lastIndexOf('|');
        if (equals <= 0 || separator <= equals + 1 || separator >= text.length() - 1) return false;
        try {
            Double.parseDouble(text.substring(equals + 1, separator).trim());
            Double.parseDouble(text.substring(separator + 1).trim());
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static boolean isRule(Object value) {
        if (!(value instanceof String text)) return false;
        int equals = text.lastIndexOf('=');
        if (equals <= 0 || equals >= text.length() - 1) return false;
        try {
            Double.parseDouble(text.substring(equals + 1).trim());
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
