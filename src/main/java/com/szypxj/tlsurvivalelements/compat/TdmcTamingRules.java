package com.szypxj.tlsurvivalelements.compat;

import com.szypxj.tldomesticatemorecreatures.api.creature.CreatureInfoApi;
import com.szypxj.tldomesticatemorecreatures.api.creature.TamingFoodInfo;
import com.szypxj.tldomesticatemorecreatures.api.creature.TamingInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class TdmcTamingRules {
    private TdmcTamingRules() {
    }

    public static boolean hasRule(LivingEntity entity) {
        return info(entity).tameable();
    }

    public static boolean isTamingFood(LivingEntity entity, ItemStack stack) {
        if (entity == null || stack == null || stack.isEmpty()) return false;
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null) return false;
        return foods(entity).stream().anyMatch(food -> itemId.equals(food.itemId()));
    }

    public static boolean isKnockoutTaming(LivingEntity entity) {
        return "KNOCKOUT".equalsIgnoreCase(info(entity).methodName());
    }

    public static String methodName(LivingEntity entity) {
        return info(entity).methodName();
    }

    public static List<TamingFoodInfo> foods(LivingEntity entity) {
        return info(entity).foods();
    }

    public static void reload() {
    }

    private static TamingInfo info(LivingEntity entity) {
        return entity == null ? TamingInfo.NOT_TAMEABLE : CreatureInfoApi.getTamingInfo(entity);
    }
}
