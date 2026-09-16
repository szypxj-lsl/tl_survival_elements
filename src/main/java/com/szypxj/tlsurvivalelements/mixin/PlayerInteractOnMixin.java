package com.szypxj.tlsurvivalelements.mixin;

import com.szypxj.tldomesticatemorecreatures.api.compat.ErsCompatApi;
import com.szypxj.tlsurvivalelements.compat.TdmcCompat;
import com.szypxj.tlsurvivalelements.compat.TdmcTamingRules;
import com.szypxj.tlsurvivalelements.service.SurvivalService;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerInteractOnMixin {
    @Unique
    private int tlse$feedEntityId = -1;
    @Unique
    private boolean tlse$managedPet;
    @Unique
    private boolean tlse$tamingFood;
    @Unique
    private boolean tlse$petFood;
    @Unique
    private Item tlse$feedItem;
    @Unique
    private ItemStack tlse$feedStack = ItemStack.EMPTY;
    @Unique
    private int tlse$feedCount;
    @Unique
    private LivingEntity tlse$feedTarget;

    @Inject(method = "interactOn", at = @At("HEAD"))
    private void tlse$captureFeed(Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        tlse$clearFeed();
        Player self = (Player) (Object) this;
        if (self.level().isClientSide || hand != InteractionHand.MAIN_HAND) return;
        if (!(target instanceof LivingEntity living) || living instanceof Player || ErsCompatApi.usesNativeHunger(living)) return;

        ItemStack stack = self.getItemInHand(hand);
        boolean managedPet = TdmcCompat.isManagedPet(living);
        boolean tamingFood = TdmcTamingRules.isTamingFood(living, stack);
        boolean petFood = managedPet && stack.isEdible();
        if (!tamingFood && !petFood) return;

        tlse$feedEntityId = living.getId();
        tlse$feedTarget = living;
        tlse$managedPet = managedPet;
        tlse$tamingFood = tamingFood;
        tlse$petFood = petFood;
        tlse$feedItem = stack.getItem();
        tlse$feedStack = stack.copy();
        tlse$feedCount = stack.getCount();
        if (managedPet && petFood) SurvivalService.beginManagedPetFeeding(living);
    }

    @Inject(method = "interactOn", at = @At("RETURN"), cancellable = true)
    private void tlse$applyFeed(Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Player self = (Player) (Object) this;
        if (self.level().isClientSide || hand != InteractionHand.MAIN_HAND) {
            tlse$clearFeed();
            return;
        }
        if (!(target instanceof LivingEntity living) || living.getId() != tlse$feedEntityId) {
            tlse$clearFeed();
            return;
        }

        boolean managedPet = tlse$managedPet;
        boolean tamingFood = tlse$tamingFood;
        boolean petFood = tlse$petFood;
        Item originalItem = tlse$feedItem;
        ItemStack feedStack = tlse$feedStack.copy();
        int originalCount = tlse$feedCount;
        tlse$clearFeed();

        if (!managedPet && tamingFood && !SurvivalService.canTamingFeed(living)) return;

        boolean accepted = cir.getReturnValue().consumesAction();
        if (managedPet && petFood && SurvivalService.creatureNeedsFeeding(living)) {
            if (!self.getAbilities().instabuild) {
                ItemStack current = self.getItemInHand(hand);
                boolean alreadyConsumed = current.getItem() != originalItem || current.getCount() < originalCount;
                if (!alreadyConsumed && !current.isEmpty()) current.shrink(1);
            }
            SurvivalService.feedManagedPet(living, feedStack);
            TdmcCompat.rememberManagedPet(living);
            if (!accepted) cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        if (tamingFood && accepted) {
            SurvivalService.restoreCreatureFood(living, feedStack);
            if (TdmcCompat.isOwnedBy(living, self)) TdmcCompat.rememberManagedPet(living);
        }
    }

    @Unique
    private void tlse$clearFeed() {
        if (tlse$feedTarget != null) SurvivalService.endManagedPetFeeding(tlse$feedTarget);
        tlse$feedEntityId = -1;
        tlse$managedPet = false;
        tlse$tamingFood = false;
        tlse$petFood = false;
        tlse$feedItem = null;
        tlse$feedStack = ItemStack.EMPTY;
        tlse$feedCount = 0;
        tlse$feedTarget = null;
    }
}
