package com.szypxj.tlsurvivalelements.client;

import com.szypxj.tldomesticatemorecreatures.api.client.PanelSummaryProvider;
import com.szypxj.tldomesticatemorecreatures.api.client.PanelSummaryRegistry;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TdmcTemperaturePanelSummary {
    private TdmcTemperaturePanelSummary() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> PanelSummaryRegistry.register(
                ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, "temperature"),
                TdmcTemperaturePanelSummary::lines
        ));
    }

    private static List<Component> lines(PanelSummaryProvider.Context context) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!context.player() || minecraft.player == null || context.entityId() != minecraft.player.getId()) {
            return List.of();
        }

        var temperature = ClientSurvivalState.snapshot();
        return List.of(
                Component.translatable(
                        "gui.tl_survival_elements.temperature.current",
                        format(temperature.currentTemperature())
                ),
                Component.translatable(
                        "gui.tl_survival_elements.temperature.heat",
                        format(temperature.heatResistance()),
                        format(temperature.maximumSafeTemperature())
                ),
                Component.translatable(
                        "gui.tl_survival_elements.temperature.cold",
                        format(temperature.coldResistance()),
                        format(temperature.minimumSafeTemperature())
                )
        );
    }

    private static String format(double value) {
        if (!Double.isFinite(value)) return "0";
        double rounded = Math.rint(value);
        if (Math.abs(value - rounded) < 0.05D) return Long.toString(Math.round(rounded));
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
