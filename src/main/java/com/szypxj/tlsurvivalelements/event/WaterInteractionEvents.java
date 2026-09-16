package com.szypxj.tlsurvivalelements.event;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.item.WaterContainerItem;
import com.szypxj.tlsurvivalelements.network.SurvivalNetwork;
import com.szypxj.tlsurvivalelements.network.packet.C2SDrinkWaterSourcePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID)
public final class WaterInteractionEvents {
    private WaterInteractionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        tryDirectDrink(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        BlockHitResult waterHit = WaterContainerItem.waterSourceHit(player.level(), player);
        if (!WaterContainerItem.isWaterSource(player.level(), waterHit)) return;

        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof WaterContainerItem) {
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.ALLOW);
            return;
        }

        if (!event.getLevel().isClientSide() || !tryDirectDrink(event)) return;
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean tryDirectDrink(PlayerInteractEvent event) {
        if (!event.getLevel().isClientSide()) return false;
        Player player = event.getEntity();
        if (event.getHand() != InteractionHand.MAIN_HAND
                || !player.isShiftKeyDown()
                || !player.getMainHandItem().isEmpty()) {
            return false;
        }
        BlockHitResult hit = WaterContainerItem.waterSourceHit(player.level(), player);
        if (!WaterContainerItem.isWaterSource(player.level(), hit)) return false;
        SurvivalNetwork.sendToServer(new C2SDrinkWaterSourcePacket());
        return true;
    }
}
