package com.szypxj.tlsurvivalelements.compat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.szypxj.tldomesticatemorecreatures.api.compat.ErsCompatApi;
import com.szypxj.tldomesticatemorecreatures.api.attribute.DynamicAttributeDisplayRegistry;
import com.szypxj.tldomesticatemorecreatures.api.attribute.DynamicAttributeDisplayValue;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeDefinition;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeFlags;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeRegistrationEvent;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeRegistry;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeValue;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeValueFormat;
import com.szypxj.tldomesticatemorecreatures.api.attribute.TdmcAttributeValueStore;
import com.szypxj.tldomesticatemorecreatures.data.ProgressData;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.registry.ModAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegistryObject;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class TdmcAttributeDefinitions {
    public static final ResourceLocation FOOD_ID = id("food");
    public static final ResourceLocation WATER_ID = id("water");
    public static final ResourceLocation STAMINA_ID = id("stamina");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int MAX_POINTS = 10000;
    private static boolean registered;

    private TdmcAttributeDefinitions() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }
        registered = true;
        removeLegacyDefinitions();
        DynamicAttributeDisplayRegistry.register(FOOD_ID, (viewer, target) -> {
            if (ErsCompatApi.usesNativeHunger(target)) {
                return new DynamicAttributeDisplayValue(ErsCompatApi.currentHunger(target), ErsCompatApi.maxHunger(target));
            }
            SurvivalData data = SurvivalData.of(target);
            return new DynamicAttributeDisplayValue(data.food(), data.maxFood());
        });
        DynamicAttributeDisplayRegistry.register(WATER_ID, (viewer, target) -> {
            if (!(target instanceof Player)) {
                return null;
            }
            SurvivalData data = SurvivalData.of(target);
            return new DynamicAttributeDisplayValue(Math.round(data.water()), Math.round(data.maxWater()));
        });
        DynamicAttributeDisplayRegistry.register(STAMINA_ID, (viewer, target) -> {
            SurvivalData data = SurvivalData.of(target);
            return new DynamicAttributeDisplayValue(data.stamina(), data.maxStamina());
        });
        MinecraftForge.EVENT_BUS.addListener(TdmcAttributeDefinitions::onAttributeRegistration);
    }


    public static int foodPoints(LivingEntity entity) {
        return points(entity, ModAttributes.FOOD, "food", FOOD_ID);
    }

    public static int waterPoints(LivingEntity entity) {
        return points(entity, ModAttributes.WATER, "water", WATER_ID);
    }

    public static int staminaPoints(LivingEntity entity) {
        return points(entity, ModAttributes.STAMINA, "stamina", STAMINA_ID);
    }

    public static void ensureRegistered() {
        TdmcAttributeRegistry.registerIfAbsent(TlSurvivalElements.MOD_ID, definition(
                FOOD_ID,
                "stat.tl_survival_elements.food",
                "tl_survival_elements:textures/gui/hud/food_full.png",
                200,
                true,
                true,
                ModAttributes.FOOD,
                "food"
        ));
        TdmcAttributeRegistry.registerIfAbsent(TlSurvivalElements.MOD_ID, definition(
                WATER_ID,
                "stat.tl_survival_elements.water",
                "tl_survival_elements:textures/gui/hud/water_full.png",
                300,
                false,
                false,
                ModAttributes.WATER,
                "water"
        ));
        TdmcAttributeRegistry.registerIfAbsent(TlSurvivalElements.MOD_ID, definition(
                STAMINA_ID,
                "stat.tl_survival_elements.stamina",
                "tl_survival_elements:textures/gui/hud/stamina_bolt.png",
                400,
                true,
                true,
                ModAttributes.STAMINA,
                "stamina"
        ));
    }

    private static void onAttributeRegistration(TdmcAttributeRegistrationEvent event) {
        ensureRegistered();
    }

    private static TdmcAttributeDefinition definition(
            ResourceLocation id,
            String nameKey,
            String icon,
            int displayOrder,
            boolean showMob,
            boolean randomAssignable,
            RegistryObject<Attribute> attribute,
            String legacyStatId
    ) {
        TdmcAttributeFlags flags = new TdmcAttributeFlags(
                true,
                true,
                randomAssignable,
                true,
                randomAssignable,
                randomAssignable,
                true
        );
        return TdmcAttributeDefinition.builder(id, nameKey)
                .icon(icon)
                .displayOrder(displayOrder)
                .bounds(0.0D, MAX_POINTS)
                .defaultValue(0.0D)
                .valueFormat(TdmcAttributeValueFormat.NUMBER)
                .flags(flags)
                .pointRule(1.0D, 1)
                .applicability(entity -> showMob || entity instanceof Player)
                .valueProvider(entity -> {
                    if (FOOD_ID.equals(id) && ErsCompatApi.usesNativeHunger(entity)) {
                        return TdmcAttributeValue.of(ErsCompatApi.MAX_HUNGER, ErsCompatApi.MAX_HUNGER);
                    }
                    int points = points(entity, attribute, legacyStatId, id);
                    TdmcAttributeValueStore.allocatedPoints(entity, id, points);
                    return TdmcAttributeValue.of(points, MAX_POINTS);
                })
                .valueWriter((entity, value) -> FOOD_ID.equals(id) && ErsCompatApi.usesNativeHunger(entity)
                        ? false
                        : setPoints(entity, attribute, id, value))
                .build();
    }

    private static int points(
            LivingEntity entity,
            RegistryObject<Attribute> attribute,
            String legacyStatId,
            ResourceLocation attributeId
    ) {
        AttributeInstance instance = entity.getAttribute(attribute.get());
        if (instance == null) {
            return 0;
        }
        int current = basePoints(instance);
        int stored = Math.min(MAX_POINTS, TdmcAttributeValueStore.allocatedPoints(entity, attributeId));
        if (ProgressData.exists(entity)) {
            ProgressData data = ProgressData.of(entity);
            ProgressData.StatPoints legacy = data.stat(legacyStatId);
            int legacyPoints = Math.min(MAX_POINTS, legacy.total());
            if (legacyPoints > 0) {
                int migrated = Math.max(current, Math.max(stored, legacyPoints));
                instance.setBaseValue(migrated);
                data.stat(legacyStatId, new ProgressData.StatPoints(0, 0, 0));
                TdmcAttributeValueStore.allocatedPoints(entity, attributeId, migrated);
                return migrated;
            }
        }
        if (current > 0) {
            if (stored != current) {
                TdmcAttributeValueStore.allocatedPoints(entity, attributeId, current);
            }
            return current;
        }
        if (stored > 0) {
            instance.setBaseValue(stored);
            return stored;
        }
        return 0;
    }

    private static boolean setPoints(
            LivingEntity entity,
            RegistryObject<Attribute> attribute,
            ResourceLocation attributeId,
            double value
    ) {
        AttributeInstance instance = entity.getAttribute(attribute.get());
        if (instance == null || !Double.isFinite(value)) {
            return false;
        }
        long rounded = Math.round(value);
        int points = (int) Math.max(0L, Math.min(MAX_POINTS, rounded));
        instance.setBaseValue(points);
        if (points == 0) {
            TdmcAttributeValueStore.allocatedPoints(entity, attributeId, 0);
        }
        return true;
    }

    private static int basePoints(AttributeInstance instance) {
        return Math.max(0, (int) Math.min(MAX_POINTS, Math.floor(instance.getBaseValue())));
    }

    private static ResourceLocation id(String path) {
        ResourceLocation id = ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, path);
        if (id == null) {
            throw new IllegalStateException("Invalid TSE attribute id: " + path);
        }
        return id;
    }

    private static void removeLegacyDefinitions() {
        for (Path path : legacyDefinitionFiles()) {
            removeLegacyDefinitions(path);
        }
    }

    private static List<Path> legacyDefinitionFiles() {
        Path root = FMLPaths.CONFIGDIR.get();
        return List.of(
                root.resolve("tl_domesticate_more_creatures").resolve("attributes.json"),
                root.resolve("tl_biological_attribute_panel").resolve("attributes.json")
        );
    }

    private static void removeLegacyDefinitions(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                return;
            }
            JsonArray array = root.getAsJsonArray();
            boolean changed = false;
            for (int i = array.size() - 1; i >= 0; i--) {
                JsonElement element = array.get(i);
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                String id = string(object, "id");
                String target = string(object, "target");
                if (isLegacyDefinition(id, target)) {
                    array.remove(i);
                    changed = true;
                }
            }
            if (!changed) {
                return;
            }
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(array, writer);
            }
        } catch (IOException | RuntimeException ignored) {
        }
    }

    private static boolean isLegacyDefinition(String id, String target) {
        if (id == null || target == null) {
            return false;
        }
        return (id.equals("food") && target.equals(FOOD_ID.toString()))
                || (id.equals("water") && target.equals(WATER_ID.toString()))
                || (id.equals("stamina") && target.equals(STAMINA_ID.toString()));
    }

    private static String string(JsonObject object, String key) {
        JsonElement value = object.get(key);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : null;
    }
}
