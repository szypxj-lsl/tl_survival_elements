package com.szypxj.tlsurvivalelements.client;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.network.SurvivalNetwork;
import com.szypxj.tlsurvivalelements.network.packet.C2SBoostStatePacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID, value = Dist.CLIENT)
public final class BoostKeyHandler {
    private static boolean lastSentActive;
    private static boolean lastSentMovingInput;

    private BoostKeyHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            lastSentActive = false;
            lastSentMovingInput = false;
            return;
        }

        boolean active = minecraft.screen == null && BoostKeyMappings.BOOST.isDown();
        boolean directionalInput = minecraft.options.keyUp.isDown()
                || minecraft.options.keyDown.isDown()
                || minecraft.options.keyLeft.isDown()
                || minecraft.options.keyRight.isDown();
        boolean vehicleMoving = minecraft.player.getVehicle() != null
                && minecraft.player.getVehicle().getDeltaMovement().lengthSqr() > 0.0004D;
        boolean movingInput = active && (directionalInput || vehicleMoving);
        if (active == lastSentActive && movingInput == lastSentMovingInput) return;
        lastSentActive = active;
        lastSentMovingInput = movingInput;
        SurvivalNetwork.sendToServer(new C2SBoostStatePacket(active, movingInput));
    }
}
