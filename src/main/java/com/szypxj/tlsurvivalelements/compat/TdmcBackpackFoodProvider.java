package com.szypxj.tlsurvivalelements.compat;

import com.szypxj.tldomesticatemorecreatures.api.backpack.PetBackpackFoodApi;
import com.szypxj.tldomesticatemorecreatures.api.compat.ErsCompatApi;
import com.szypxj.tlsurvivalelements.service.SurvivalService;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class TdmcBackpackFoodProvider implements PetBackpackFoodApi.Provider {
    @Override
    public boolean supports(LivingEntity entity) {
        return entity != null && !(entity instanceof Player) && !ErsCompatApi.usesNativeHunger(entity);
    }

    @Override
    public double foodValue(LivingEntity entity, ItemStack stack) {
        return SurvivalService.foodValueForCreature(entity, stack);
    }

    @Override
    public double currentFood(LivingEntity entity) {
        return SurvivalService.currentCreatureFood(entity);
    }

    @Override
    public double maxFood(LivingEntity entity) {
        return SurvivalService.maxCreatureFood(entity);
    }

    @Override
    public void addFood(LivingEntity entity, double amount) {
        SurvivalService.addCreatureFood(entity, amount);
    }

    @Override
    public boolean forceUse(Player player, LivingEntity entity, ItemStack stack) {
        return SurvivalService.forceUseBackpackFood(player, entity, stack);
    }
}
