package com.szypxj.tlsurvivalelements.registry;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.item.WaterContainerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, TlSurvivalElements.MOD_ID);

    public static final RegistryObject<Item> WATER_BAG = REGISTER.register(
            "water_bag",
            () -> new WaterContainerItem(new Item.Properties().stacksTo(1), 200)
    );

    public static final RegistryObject<Item> WATER_CANTEEN = REGISTER.register(
            "water_canteen",
            () -> new WaterContainerItem(new Item.Properties().stacksTo(1), 500)
    );

    private ModItems() {
    }
}
