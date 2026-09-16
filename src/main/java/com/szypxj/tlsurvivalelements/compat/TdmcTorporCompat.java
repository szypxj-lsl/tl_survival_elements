package com.szypxj.tlsurvivalelements.compat;

import com.szypxj.tldomesticatemorecreatures.api.torpor.TorporRecoveryModifierRegistry;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.service.SurvivalService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class TdmcTorporCompat {
    private static final ResourceLocation RECOVERY_MODIFIER_ID = ResourceLocation.tryBuild(
            TlSurvivalElements.MOD_ID,
            "taming_food_gate"
    );

    private TdmcTorporCompat() {
    }

    public static void register() {
        TorporRecoveryModifierRegistry.register(RECOVERY_MODIFIER_ID, TdmcTorporCompat::limitRecovery);
    }

    private static double limitRecovery(
            LivingEntity entity,
            double currentTorpor,
            double maxTorpor,
            double normalRecoveryPerSecond,
            boolean unconscious
    ) {
        if (!unconscious
                || entity == null
                || entity instanceof Player
                || SurvivalService.usesNativeCreatureFood(entity)
                || TdmcCompat.isManagedPet(entity)
                || !TdmcTamingRules.hasRule(entity)) {
            return normalRecoveryPerSecond;
        }

        SurvivalData data = SurvivalData.of(entity);
        double threshold = Math.max(0.0D, Math.min(1.0D, SurvivalConfig.TAME_FEED_THRESHOLD.get()));
        double remainingFood = data.food() - data.maxFood() * threshold;
        if (remainingFood <= 0.0D) {
            return normalRecoveryPerSecond;
        }

        double foodDrainPerSecond = SurvivalService.naturalFoodDrainPerSecond(entity, true);
        if (foodDrainPerSecond <= 0.0D || !Double.isFinite(foodDrainPerSecond)) {
            return normalRecoveryPerSecond;
        }

        double waitSeconds = remainingFood / foodDrainPerSecond;
        double safetySeconds = Math.max(0, SurvivalConfig.TAMING_TORPOR_RECOVERY_SAFETY_SECONDS.get());
        double protectedSeconds = waitSeconds + safetySeconds;
        if (protectedSeconds <= 0.0D || !Double.isFinite(protectedSeconds)) {
            return normalRecoveryPerSecond;
        }

        double allowedRecovery = Math.max(0.0D, currentTorpor) / protectedSeconds;
        if (!Double.isFinite(allowedRecovery)) {
            return normalRecoveryPerSecond;
        }
        return Math.min(Math.max(0.0D, normalRecoveryPerSecond), allowedRecovery);
    }
}
