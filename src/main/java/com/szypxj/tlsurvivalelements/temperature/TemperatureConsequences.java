package com.szypxj.tlsurvivalelements.temperature;

import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.registry.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public final class TemperatureConsequences {
    private TemperatureConsequences() {
    }

    public static void tick(ServerPlayer player) {
        if (player == null) return;
        if (!SurvivalConfig.TEMPERATURE_ENABLED.get() || player.isCreative() || player.isSpectator()) {
            clearTemperatureEffects(player);
            return;
        }

        TemperatureStage stage = TemperatureService.snapshot(player).stage();
        updateEffect(player, ModEffects.COLD.get(), stage.isCold());
        updateEffect(
                player,
                ModEffects.HEATSTROKE.get(),
                stage == TemperatureStage.HEATSTROKE || stage == TemperatureStage.CRITICAL_HEAT
        );
        updateEffect(
                player,
                ModEffects.HYPOTHERMIA.get(),
                stage == TemperatureStage.HYPOTHERMIA || stage == TemperatureStage.CRITICAL_COLD
        );

        if (stage.isCritical()) {
            float damage = (float) (player.getMaxHealth() * SurvivalConfig.TEMPERATURE_CRITICAL_HEALTH_DAMAGE_PERCENT.get());
            if (damage > 0.0F && Float.isFinite(damage)) {
                player.hurt(
                        stage == TemperatureStage.CRITICAL_HEAT
                                ? TemperatureDamage.heat(player.level())
                                : TemperatureDamage.cold(player.level()),
                        damage
                );
            }
        }
    }

    private static void updateEffect(ServerPlayer player, MobEffect effect, boolean active) {
        if (active) {
            MobEffectInstance current = player.getEffect(effect);
            if (current == null || current.getDuration() <= 30) {
                player.addEffect(new MobEffectInstance(effect, 60, 0, false, true, true));
            }
        } else if (player.hasEffect(effect)) {
            player.removeEffect(effect);
        }
    }

    private static void clearTemperatureEffects(ServerPlayer player) {
        player.removeEffect(ModEffects.COLD.get());
        player.removeEffect(ModEffects.HEATSTROKE.get());
        player.removeEffect(ModEffects.HYPOTHERMIA.get());
    }
}
