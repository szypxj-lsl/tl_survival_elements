package com.szypxj.tlsurvivalelements.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class TemperatureEffect extends MobEffect {
    public TemperatureEffect(int color) {
        super(MobEffectCategory.HARMFUL, color);
    }
}
