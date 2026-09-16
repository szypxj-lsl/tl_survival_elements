package com.szypxj.tlsurvivalelements.mixin;

import com.szypxj.tlsurvivalelements.service.FoodDataAccess;
import com.szypxj.tlsurvivalelements.service.SurvivalService;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin implements FoodDataAccess {
    @Shadow private int foodLevel;
    @Shadow private float saturationLevel;
    @Shadow private float exhaustionLevel;
    @Shadow private int tickTimer;
    @Unique private int tlse$maxFood = 100;

    @Override
    public int tlse$getMaxFood() {
        return this.tlse$maxFood;
    }

    @Override
    public void tlse$setMaxFood(int value) {
        this.tlse$maxFood = Math.max(1, value);
        this.foodLevel = Math.min(this.foodLevel, this.tlse$maxFood);
        this.saturationLevel = Math.min(this.saturationLevel, this.foodLevel);
    }

    @Override
    public float tlse$getSaturationLevel() {
        return this.saturationLevel;
    }

    @Override
    public void tlse$addExhaustion(float amount) {
        this.exhaustionLevel += Math.max(0.0F, amount);
    }

    @Override
    public int tlse$getNaturalHealTimer() {
        return this.tickTimer;
    }

    @Override
    public void tlse$setNaturalHealTimer(int value) {
        this.tickTimer = Math.max(0, value);
    }

    /**
     * @author szypxj
     * @reason Scale vanilla nutrition to the TSE 100-point food system.
     */
    @Overwrite
    public void eat(int nutrition, float saturationModifier) {
        int scaledNutrition = Math.max(0, nutrition * 5);
        this.foodLevel = Math.min(scaledNutrition + this.foodLevel, this.tlse$maxFood);
        this.saturationLevel = Math.min(this.saturationLevel + scaledNutrition * saturationModifier * 2.0F, this.foodLevel);
    }

    /**
     * @author szypxj
     * @reason Compare against the configurable TSE maximum food value.
     */
    @Overwrite
    public boolean needsFood() {
        return this.foodLevel < this.tlse$maxFood;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tlse$beginNaturalHealingContext(Player player, CallbackInfo ci) {
        SurvivalService.beginVanillaNaturalHealing(player);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void tlse$endNaturalHealingContext(Player player, CallbackInfo ci) {
        SurvivalService.endVanillaNaturalHealing(player);
    }

    @Inject(method = "addExhaustion", at = @At("HEAD"), cancellable = true)
    private void tlse$suppressNaturalHealingExhaustion(float amount, CallbackInfo ci) {
        if (SurvivalService.hasVanillaNaturalHealingContext()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float tlse$scaleExhaustionForHundredPointFood(float amount) {
        return amount * 5.0F;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 20))
    private int tlse$fullFoodThreshold(int original) {
        return this.tlse$maxFood;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 18))
    private int tlse$naturalHealingFoodThreshold(int original) {
        return Math.max(1, (int) Math.ceil(this.tlse$maxFood * 0.90D));
    }
}
