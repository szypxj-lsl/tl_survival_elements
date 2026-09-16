package com.szypxj.tlsurvivalelements.client;

import com.szypxj.tlsurvivalelements.network.packet.S2CSurvivalSyncPacket;
import com.szypxj.tlsurvivalelements.temperature.TemperatureStage;

public final class ClientSurvivalState {
    private static volatile S2CSurvivalSyncPacket snapshot = new S2CSurvivalSyncPacket(
            100.0F,
            100.0F,
            0,
            100.0D,
            100.0D,
            100.0D,
            100.0D,
            100.0D,
            100.0D,
            20.0D,
            0.0D,
            0.0D,
            5.0D,
            35.0D,
            TemperatureStage.NORMAL,
            -1,
            false,
            0.0F,
            0.0F,
            0.0D,
            0.0D,
            0.0D,
            0.0D
    );

    private ClientSurvivalState() {
    }

    public static void accept(S2CSurvivalSyncPacket packet) {
        snapshot = packet;
    }

    public static S2CSurvivalSyncPacket snapshot() {
        return snapshot;
    }
}
