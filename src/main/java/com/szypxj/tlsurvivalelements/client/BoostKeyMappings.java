package com.szypxj.tlsurvivalelements.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BoostKeyMappings {
    public static final KeyMapping BOOST = new KeyMapping(
            "key.tl_survival_elements.boost",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "key.categories.tl_survival_elements"
    );

    private BoostKeyMappings() {
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(BOOST);
    }
}
