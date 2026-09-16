package com.szypxj.tlsurvivalelements.temperature;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public final class TemperatureDamage {
    public static final ResourceKey<DamageType> HEAT = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, "temperature_heat")
    );
    public static final ResourceKey<DamageType> COLD = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, "temperature_cold")
    );

    private TemperatureDamage() {
    }

    public static DamageSource heat(Level level) {
        return source(level, HEAT);
    }

    public static DamageSource cold(Level level) {
        return source(level, COLD);
    }

    private static DamageSource source(Level level, ResourceKey<DamageType> type) {
        Holder<DamageType> holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(type);
        return new DamageSource(holder);
    }
}
