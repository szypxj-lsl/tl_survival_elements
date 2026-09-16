package com.szypxj.tlsurvivalelements.compat;

import com.szypxj.tldomesticatemorecreatures.api.riding.RideAction;
import com.szypxj.tldomesticatemorecreatures.api.riding.RideActionApi;
import com.szypxj.tldomesticatemorecreatures.api.riding.RideActionGuard;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public final class TdmcRideStaminaGuard implements RideActionGuard {
    private static final ResourceLocation ID = Objects.requireNonNull(
            ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, "stamina_ride_action_guard")
    );
    private static boolean registered;

    private TdmcRideStaminaGuard() {
    }

    public static synchronized void register() {
        if (registered) {
            return;
        }
        RideActionApi.register(ID, new TdmcRideStaminaGuard());
        registered = true;
    }

    @Override
    public boolean allows(Player rider, LivingEntity mount, RideAction action) {
        if (mount == null || (!TdmcCompat.isOwnedBy(mount, rider) && !TdmcCompat.isManagedPet(mount))) {
            return true;
        }
        return SurvivalData.of(mount).stamina() > 0.0D;
    }
}
