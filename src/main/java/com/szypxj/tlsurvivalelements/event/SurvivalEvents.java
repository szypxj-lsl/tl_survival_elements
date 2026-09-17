package com.szypxj.tlsurvivalelements.event;

import com.szypxj.tldomesticatemorecreatures.api.compat.ErsCompatApi;
import com.szypxj.tldomesticatemorecreatures.api.riding.RideStateApi;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.compat.TdmcCompat;
import com.szypxj.tlsurvivalelements.compat.TdmcTamingRules;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.diet.FoodReserveService;
import com.szypxj.tlsurvivalelements.diet.WildForagingService;
import com.szypxj.tlsurvivalelements.diet.WildPredationService;
import com.szypxj.tlsurvivalelements.network.SurvivalNetwork;
import com.szypxj.tlsurvivalelements.service.CtrlBoostService;
import com.szypxj.tlsurvivalelements.service.SurvivalService;
import com.szypxj.tlsurvivalelements.temperature.TemperatureConsequences;
import com.szypxj.tlsurvivalelements.temperature.TemperatureService;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID)
public final class SurvivalEvents {
    private SurvivalEvents() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        TdmcTamingRules.reload();
    }


    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SurvivalService.initializePlayer(player);
            SurvivalNetwork.sendTemperatureConfig(player);
            SurvivalNetwork.sendState(player);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            CtrlBoostService.clear(player);
            TemperatureService.invalidate(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            TemperatureService.invalidate(player);
            SurvivalService.initializePlayer(player);
            SurvivalData.of(player).refillAll();
            player.setHealth(player.getMaxHealth());
            SurvivalNetwork.sendTemperatureConfig(player);
            SurvivalNetwork.sendState(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            TemperatureService.invalidate(player);
            SurvivalService.initializePlayer(player);
            SurvivalData.of(player).refillAll();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;
        Player player = event.player;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        SurvivalData data = SurvivalData.of(player);
        if (player.tickCount % 20 == 0) data.syncCapacity();
        SurvivalService.updateThirst(player);

        boolean ctrlBoosting = CtrlBoostService.tick(serverPlayer);
        boolean playerCtrlBoosting = CtrlBoostService.isActivelyBoosting(player);
        boolean sprinting = player.isSprinting() && player.getVehicle() == null;
        boolean moving = player.getDeltaMovement().horizontalDistanceSqr() > 0.0004D;
        if (sprinting && !playerCtrlBoosting) {
            boolean enough = SurvivalService.consumeBoostStamina(player, player.isInWaterOrBubble() ? SurvivalService.BoostMode.SWIM : SurvivalService.BoostMode.GROUND);
            if (!enough) player.setSprinting(false);
        }
        boolean playerActive = sprinting || playerCtrlBoosting;
        SurvivalService.tickStaminaRegeneration(player, moving, playerActive);

        if (player.tickCount % 20 == 0) {
            TemperatureConsequences.tick(serverPlayer);
            SurvivalService.tickPlayerFastHealing(serverPlayer);
            SurvivalService.drainNaturalFood(player, SurvivalService.hasConfiguredAnesthesia(player));
            SurvivalService.drainNaturalWater(player, sprinting || ctrlBoosting);
            SurvivalService.tickStarvationDamage(player);
        }
        if (player.tickCount % 20 == 10) {
            SurvivalService.tickDehydrationDamage(player);
        }

        if (player.tickCount % 10 == 0) SurvivalNetwork.sendState(serverPlayer);
        data.clamp();
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || entity instanceof Player) return;
        boolean tracked = entity.getPersistentData().contains("tl_survival_elements");
        if (!tracked) {
            if (entity.tickCount % 200 != 0) return;
            if (!TdmcTamingRules.hasRule(entity) && !TdmcCompat.isManagedPet(entity) && !WildForagingService.shouldTrack(entity)) return;
            SurvivalData.of(entity);
        }

        SurvivalData data = SurvivalData.of(entity);
        if (entity.tickCount % 20 == 0) data.syncCapacity();
        SurvivalService.tickPetNaturalHealing(entity);
        if (entity.tickCount % 20 == 0) SurvivalService.tickPetFastHealing(entity);
        boolean moving = entity.getDeltaMovement().lengthSqr() > 0.0004D;
        boolean riddenFlying = RideStateApi.isRiddenFlying(entity);
        if (riddenFlying) {
            SurvivalService.consumePassiveFlightStamina(entity);
        }
        boolean ctrlBoosting = CtrlBoostService.isActivelyBoosting(entity);
        SurvivalService.tickStaminaRegeneration(entity, moving, ctrlBoosting || riddenFlying);
        WildForagingService.tick(entity);
        WildPredationService.tick(entity);
        if (entity.tickCount % 20 == 0) {
            SurvivalService.drainNaturalFood(entity, SurvivalService.hasConfiguredAnesthesia(entity));
            FoodReserveService.tickTransfer(entity);
            SurvivalService.tickPetBackpackAutoFeed(entity);
            SurvivalService.tickStarvationDamage(entity);
        }
        data.clamp();
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        WildPredationService.onLivingDeath(event);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onLivingDamage(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.getAmount() <= 0.0F) return;

        if (entity instanceof Player player) {
            SurvivalService.recordPlayerCombat(player);
        } else {
            SurvivalService.recordPetCombat(entity);
        }

        if (event.getSource().getEntity() instanceof Player attacker) {
            SurvivalService.recordPlayerCombat(attacker);
            SurvivalService.consumeAttackStamina(attacker);
        } else if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            SurvivalService.recordPetCombat(attacker);
            SurvivalService.consumeAttackStamina(attacker);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onManagedPetFeedHeal(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (SurvivalService.isManagedPetFeedingHealing(event.getEntity())) {
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        if (!SurvivalService.isVanillaNaturalHealing(player)) return;
        event.setAmount(SurvivalService.adjustVanillaNaturalHealing(player, event.getAmount()));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackGate(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker) || attacker instanceof Player) return;
        if (TdmcCompat.isManagedPet(attacker) && SurvivalData.of(attacker).stamina() <= 0.0D) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onDrink(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        double hydration = SurvivalService.hydrationFor(event.getItem());
        if (hydration <= 0.0D) return;
        SurvivalData data = SurvivalData.of(player);
        data.water(data.water() + hydration);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onTamingFeedGate(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide() || event.getHand() != InteractionHand.MAIN_HAND) return;
        if (!(event.getTarget() instanceof LivingEntity target) || target instanceof Player || ErsCompatApi.usesNativeHunger(target)) return;
        boolean managedPet = TdmcCompat.isManagedPet(target);
        boolean tamingFood = TdmcTamingRules.isTamingFood(target, event.getItemStack());
        boolean petFood = managedPet && event.getItemStack().isEdible();
        if (!tamingFood && !petFood) return;

        if (managedPet && petFood && !SurvivalService.creatureNeedsFeeding(target)) {
            event.setCanceled(true);
            return;
        }

        if (!managedPet && tamingFood && !SurvivalService.canTamingFeed(target)) {
            event.setCanceled(true);
            int thresholdPercent = (int) Math.round(SurvivalConfig.TAME_FEED_THRESHOLD.get() * 100.0D);
            event.getEntity().sendSystemMessage(Component.translatable("msg.tl_survival_elements.taming_food_too_high", thresholdPercent));
        }
    }
}
