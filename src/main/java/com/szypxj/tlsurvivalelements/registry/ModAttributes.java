package com.szypxj.tlsurvivalelements.registry;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.compat.TdmcAttributeDefinitions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModAttributes {
    public static final DeferredRegister<Attribute> REGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TlSurvivalElements.MOD_ID);
    public static final RegistryObject<Attribute> FOOD = REGISTER.register("food", () -> new RangedAttribute("attribute.tl_survival_elements.food", 0.0D, 0.0D, 10000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> WATER = REGISTER.register("water", () -> new RangedAttribute("attribute.tl_survival_elements.water", 0.0D, 0.0D, 10000.0D).setSyncable(true));
    public static final RegistryObject<Attribute> STAMINA = REGISTER.register("stamina", () -> new RangedAttribute("attribute.tl_survival_elements.stamina", 0.0D, 0.0D, 10000.0D).setSyncable(true));

    private ModAttributes() {
    }

    public static void addAttributes(EntityAttributeModificationEvent event) {
        for (var type : event.getTypes()) {
            event.add(type, FOOD.get());
            event.add(type, STAMINA.get());
            if (type == EntityType.PLAYER) event.add(type, WATER.get());
        }
    }

    public static int foodPoints(LivingEntity entity) {
        return TdmcAttributeDefinitions.foodPoints(entity);
    }

    public static int waterPoints(LivingEntity entity) {
        return TdmcAttributeDefinitions.waterPoints(entity);
    }

    public static int staminaPoints(LivingEntity entity) {
        return TdmcAttributeDefinitions.staminaPoints(entity);
    }

}
