package com.szypxj.tlsurvivalelements.item;

import com.szypxj.tlsurvivalelements.data.SurvivalData;
import com.szypxj.tlsurvivalelements.network.SurvivalNetwork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class WaterContainerItem extends Item {
    private static final String STORED_WATER = "StoredWater";
    private static final int BAR_WIDTH = 13;
    private static final int BAR_COLOR = 0x3BA7FF;

    private final int capacity;

    public WaterContainerItem(Properties properties, int capacity) {
        super(properties);
        this.capacity = Math.max(1, capacity);
    }

    public int capacity() {
        return capacity;
    }

    public double storedWater(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0.0D;
        return Mth.clamp(tag.getDouble(STORED_WATER), 0.0D, capacity);
    }

    public void storedWater(ItemStack stack, double amount) {
        double value = Mth.clamp(amount, 0.0D, capacity);
        stack.getOrCreateTag().putDouble(STORED_WATER, value);
    }

    public void fill(ItemStack stack) {
        storedWater(stack, capacity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = waterSourceHit(level, player);
        if (isWaterSource(level, hit)) {
            if (!level.isClientSide) fill(stack);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        if (storedWater(stack) <= 0.0D) return InteractionResultHolder.pass(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (level.isClientSide || !(living instanceof Player player)) return stack;

        double stored = storedWater(stack);
        if (stored <= 0.0D) return stack;

        SurvivalData data = SurvivalData.of(player);
        double missing = Math.max(0.0D, data.maxWater() - data.water());
        if (missing <= 0.0D) return stack;

        double restored = Math.min(missing, stored);
        data.water(data.water() + restored);
        storedWater(stack, stored - restored);
        if (player instanceof ServerPlayer serverPlayer) {
            SurvivalNetwork.sendState(serverPlayer);
        }
        return stack;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float) (BAR_WIDTH * storedWater(stack) / capacity));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public static BlockHitResult waterSourceHit(Level level, Player player) {
        return getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
    }

    public static boolean isWaterSource(Level level, BlockHitResult hit) {
        if (hit.getType() != HitResult.Type.BLOCK) return false;
        FluidState fluid = level.getFluidState(hit.getBlockPos());
        return fluid.is(FluidTags.WATER) && fluid.isSource();
    }
}
