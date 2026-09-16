package com.szypxj.tlsurvivalelements;

import com.szypxj.tlsurvivalelements.compat.TdmcAttributeDefinitions;
import com.szypxj.tlsurvivalelements.compat.TdmcBackpackFoodProvider;
import com.szypxj.tlsurvivalelements.compat.TdmcTorporCompat;
import com.szypxj.tlsurvivalelements.compat.TdmcRideStaminaGuard;
import com.szypxj.tldomesticatemorecreatures.api.backpack.PetBackpackFoodApi;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.network.SurvivalNetwork;
import com.szypxj.tlsurvivalelements.registry.ModAttributes;
import com.szypxj.tlsurvivalelements.registry.ModEffects;
import com.szypxj.tlsurvivalelements.registry.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Objects;

@Mod(TlSurvivalElements.MOD_ID)
public final class TlSurvivalElements {
    public static final String MOD_ID = "tl_survival_elements";
    private static final ResourceKey<CreativeModeTab> TDMC_MAIN_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            Objects.requireNonNull(ResourceLocation.tryBuild("tl_domesticate_more_creatures", "main"))
    );

    public TlSurvivalElements(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModEffects.REGISTER.register(modBus);
        ModAttributes.REGISTER.register(modBus);
        ModItems.REGISTER.register(modBus);
        modBus.addListener(ModAttributes::addAttributes);
        modBus.addListener(TlSurvivalElements::addCreativeItems);
        context.registerConfig(ModConfig.Type.COMMON, SurvivalConfig.SPEC);
        TdmcAttributeDefinitions.register();
        PetBackpackFoodApi.register(new TdmcBackpackFoodProvider());
        TdmcTorporCompat.register();
        TdmcRideStaminaGuard.register();
        modBus.addListener(TlSurvivalElements::commonSetup);
        SurvivalNetwork.register();
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(TdmcAttributeDefinitions::ensureRegistered);
    }

    private static void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(TDMC_MAIN_TAB)) return;
        event.accept(ModItems.WATER_BAG.get());
        event.accept(ModItems.WATER_CANTEEN.get());
    }
}
