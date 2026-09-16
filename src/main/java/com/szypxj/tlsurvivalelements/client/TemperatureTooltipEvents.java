package com.szypxj.tlsurvivalelements.client;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.service.HydrationRules;
import com.szypxj.tlsurvivalelements.service.SurvivalService;
import com.szypxj.tlsurvivalelements.temperature.EquipmentTemperatureService;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID, value = Dist.CLIENT)
public final class TemperatureTooltipEvents {
    private TemperatureTooltipEvents() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        EquipmentTemperatureService.Resistance resistance = EquipmentTemperatureService.forStack(event.getItemStack(), ClientTemperatureConfig.equipmentRules());
        if (resistance.heat() > 0.0D) {
            event.getToolTip().add(Component.translatable(
                    "tooltip.tl_survival_elements.heat_resistance",
                    format(resistance.heat())
            ));
        }
        if (resistance.cold() > 0.0D) {
            event.getToolTip().add(Component.translatable(
                    "tooltip.tl_survival_elements.cold_resistance",
                    format(resistance.cold())
            ));
        }

        double foodRestore = SurvivalService.foodValue(event.getItemStack(), event.getEntity());
        if (foodRestore > 0.0D) {
            event.getToolTip().add(Component.translatable(
                    "tooltip.tl_survival_elements.food_restore",
                    format(foodRestore)
            ));
        }

        double waterRestore = HydrationRules.hydrationFor(
                event.getItemStack(),
                ClientTemperatureConfig.hydrationRules(),
                ClientTemperatureConfig.defaultPotionRestore()
        );
        if (waterRestore > 0.0D) {
            event.getToolTip().add(Component.translatable(
                    "tooltip.tl_survival_elements.water_restore",
                    format(waterRestore)
            ));
        }
    }

    private static String format(double value) {
        if (!Double.isFinite(value)) return "0";
        double rounded = Math.rint(value);
        if (Math.abs(value - rounded) < 0.05D) return Long.toString(Math.round(rounded));
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
