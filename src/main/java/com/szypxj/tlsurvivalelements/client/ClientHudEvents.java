package com.szypxj.tlsurvivalelements.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.network.packet.S2CSurvivalSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = TlSurvivalElements.MOD_ID, value = Dist.CLIENT)
public final class ClientHudEvents {
    private static final int ICON_SIZE = 16;
    private static final int TOP_ICON_SIZE = 20;
    private static final int TOP_ICON_GAP = 4;
    private static final int BAR_ICON_GAP = 2;
    private static final int TOP_TO_BAR_OFFSET = -1;
    private static final int BAR_ROW_ADVANCE = 18;
    private static final int BOTTOM_MARGIN = 12;
    private static final int BAR_WIDTH = 150;
    private static final int BAR_NATIVE_WIDTH = 108;
    private static final int BAR_NATIVE_INNER_WIDTH = 96;
    private static final int BAR_TEXTURE_HEIGHT = 20;
    private static final float BAR_SCALE_X = BAR_WIDTH / (float) BAR_NATIVE_WIDTH;
    private static final int HUD_WIDTH = ICON_SIZE + BAR_ICON_GAP + BAR_WIDTH;
    private static final float MOUNT_HUD_SCALE = 0.75F;

    private static final ResourceLocation HEALTH_ICON = texture("health_cross.png");
    private static final ResourceLocation STAMINA_ICON = texture("stamina_bolt.png");
    private static final ResourceLocation WATER_ICON = texture("water_full.png");
    private static final ResourceLocation WATER_EMPTY_ICON = texture("water_empty.png");
    private static final ResourceLocation FOOD_ICON = texture("food_full.png");
    private static final ResourceLocation FOOD_EMPTY_ICON = texture("food_empty.png");
    private static final ResourceLocation ARMOR_ICON = texture("armor.png");
    private static final ResourceLocation BAR_BORDER = texture("resource_bar_border.png");
    private static final ResourceLocation BAR_GRAYSCALE = texture("resource_bar_grayscale.png");
    private static final ResourceLocation BAR_MANA = texture("resource_bar_mana.png");
    private static final ResourceLocation MANA_ICON = texture("mana_orb.png");
    private static final ResourceLocation ARS_MANA_OVERLAY = Objects.requireNonNull(
            ResourceLocation.tryBuild("ars_nouveau", "mana_hud")
    );

    private ClientHudEvents() {
    }

    @SubscribeEvent
    public static void hideVanilla(RenderGuiOverlayEvent.Pre event) {
        ResourceLocation id = event.getOverlay().id();
        if (id.equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
                || id.equals(VanillaGuiOverlay.ARMOR_LEVEL.id())
                || id.equals(VanillaGuiOverlay.FOOD_LEVEL.id())
                || id.equals(VanillaGuiOverlay.MOUNT_HEALTH.id())
                || (id.equals(ARS_MANA_OVERLAY) && ArsNouveauClientCompat.available())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void render(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        S2CSurvivalSyncPacket state = ClientSurvivalState.snapshot();
        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        boolean showMana = ArsNouveauClientCompat.available();

        int playerHeight = hudHeight(showMana ? 3 : 2);
        int playerX = 8;
        int playerY = screenHeight - BOTTOM_MARGIN - playerHeight;
        drawPlayerHud(graphics, playerX, playerY, state, showMana);

        if (state.mountId() >= 0 && state.mountMaxHealth() > 0.0F) {
            int mountWidth = Math.round(HUD_WIDTH * MOUNT_HUD_SCALE);
            int mountHeight = Math.round(hudHeight(2) * MOUNT_HUD_SCALE);
            int mountX = screenWidth - mountWidth - 8;
            int mountY = screenHeight - BOTTOM_MARGIN - mountHeight;
            graphics.pose().pushPose();
            graphics.pose().translate(mountX, mountY, 0.0F);
            graphics.pose().scale(MOUNT_HUD_SCALE, MOUNT_HUD_SCALE, 1.0F);
            drawMountHud(graphics, 0, 0, state);
            graphics.pose().popPose();
        }
    }

    private static void drawPlayerHud(
            GuiGraphics graphics,
            int x,
            int y,
            S2CSurvivalSyncPacket state,
            boolean showMana
    ) {
        int topIconStep = TOP_ICON_SIZE + TOP_ICON_GAP;
        drawPercentIcon(graphics, WATER_EMPTY_ICON, WATER_ICON, x, y, state.water(), state.maxWater());
        drawPercentIcon(graphics, FOOD_EMPTY_ICON, FOOD_ICON, x + topIconStep, y, state.food(), state.maxFood());
        drawArmorIcon(graphics, x + topIconStep * 2, y, state.armor());

        int rowY = y + TOP_ICON_SIZE + TOP_TO_BAR_OFFSET;
        drawResourceRow(graphics, HEALTH_ICON, x, rowY, state.health(), state.maxHealth(), ResourceStyle.HEALTH, 0.0D, 0.0D);
        rowY += BAR_ROW_ADVANCE;
        drawResourceRow(graphics, STAMINA_ICON, x, rowY, state.stamina(), state.maxStamina(), ResourceStyle.STAMINA, 0.0D, 0.0D);

        if (showMana) {
            rowY += BAR_ROW_ADVANCE;
            Minecraft minecraft = Minecraft.getInstance();
            double currentMana = ArsNouveauClientCompat.currentMana(minecraft.player);
            double maxMana = ArsNouveauClientCompat.maxMana(minecraft.player);
            drawResourceRow(
                    graphics,
                    MANA_ICON,
                    x,
                    rowY,
                    currentMana,
                    maxMana,
                    ResourceStyle.MANA,
                    ArsNouveauClientCompat.reservedRatio(),
                    ArsNouveauClientCompat.redOverlayRatio(minecraft.player)
            );
        }
    }

    private static void drawMountHud(GuiGraphics graphics, int x, int y, S2CSurvivalSyncPacket state) {
        drawPercentIcon(graphics, FOOD_EMPTY_ICON, FOOD_ICON, x, y, state.mountFood(), state.mountMaxFood());

        int rowY = y + TOP_ICON_SIZE + TOP_TO_BAR_OFFSET;
        drawResourceRow(graphics, HEALTH_ICON, x, rowY, state.mountHealth(), state.mountMaxHealth(), ResourceStyle.HEALTH, 0.0D, 0.0D);
        rowY += BAR_ROW_ADVANCE;
        drawResourceRow(graphics, STAMINA_ICON, x, rowY, state.mountStamina(), state.mountMaxStamina(), ResourceStyle.STAMINA, 0.0D, 0.0D);
    }

    private static void drawResourceRow(
            GuiGraphics graphics,
            ResourceLocation icon,
            int x,
            int y,
            double value,
            double max,
            ResourceStyle style,
            double reservedRatio,
            double redOverlayRatio
    ) {
        drawResourceIcon(graphics, icon, x, y + 2);
        int barX = x + ICON_SIZE + BAR_ICON_GAP;
        drawOrnateBar(graphics, barX, y, value, max, style, reservedRatio, redOverlayRatio);
        drawBarValue(graphics, barX, y, value, max);
    }

    private static void drawOrnateBar(
            GuiGraphics graphics,
            int x,
            int y,
            double value,
            double max,
            ResourceStyle style,
            double reservedRatio,
            double redOverlayRatio
    ) {
        int animationV = animationV();
        int nativeFillWidth = (int) Math.round(BAR_NATIVE_INNER_WIDTH * ratio(value, max));
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(BAR_SCALE_X, 1.0F, 1.0F);
        graphics.blit(BAR_BORDER, 0, 0, 0.0F, 0.0F, BAR_NATIVE_WIDTH, 18, 256, 256);
        if (nativeFillWidth > 0) {
            if (style == ResourceStyle.MANA) {
                graphics.blit(BAR_MANA, 9, 9, 0.0F, animationV, nativeFillWidth, 6, 256, 256);
            } else {
                float[] tint = style.tint();
                RenderSystem.setShaderColor(tint[0], tint[1], tint[2], 1.0F);
                graphics.blit(BAR_GRAYSCALE, 9, 9, 0.0F, animationV, nativeFillWidth, 6, 256, 256);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
        graphics.blit(BAR_BORDER, 0, 1, 0.0F, 18.0F, BAR_NATIVE_WIDTH, BAR_TEXTURE_HEIGHT, 256, 256);
        graphics.pose().popPose();

        if (style == ResourceStyle.MANA) {
            int fillX = x + Math.round(9.0F * BAR_SCALE_X);
            int fillY = y + 9;
            int innerWidth = Math.round(BAR_NATIVE_INNER_WIDTH * BAR_SCALE_X);
            int actualFillWidth = Math.round(nativeFillWidth * BAR_SCALE_X);
            int reservedWidth = (int) Math.round(innerWidth * clamp01(reservedRatio));
            if (reservedWidth > 0) {
                graphics.fill(
                        fillX + innerWidth - reservedWidth,
                        fillY,
                        fillX + innerWidth,
                        fillY + 6,
                        0xB8000000
                );
            }
            int redWidth = (int) Math.round(innerWidth * clamp01(redOverlayRatio));
            int redStart = Math.min(innerWidth, actualFillWidth);
            int redEnd = Math.min(innerWidth, redStart + redWidth);
            if (redEnd > redStart) {
                graphics.fill(fillX + redStart, fillY, fillX + redEnd, fillY + 6, 0xB8E34D59);
            }
        }
    }

    private static void drawPercentIcon(
            GuiGraphics graphics,
            ResourceLocation emptyTexture,
            ResourceLocation fullTexture,
            int x,
            int y,
            double value,
            double max
    ) {
        drawResourceIcon(graphics, emptyTexture, x, y, TOP_ICON_SIZE);
        int filledHeight = (int) Math.round(TOP_ICON_SIZE * ratio(value, max));
        if (filledHeight <= 0) {
            return;
        }

        graphics.enableScissor(x, y + TOP_ICON_SIZE - filledHeight, x + TOP_ICON_SIZE, y + TOP_ICON_SIZE);
        drawResourceIcon(graphics, fullTexture, x, y, TOP_ICON_SIZE);
        graphics.disableScissor();
    }

    private static void drawArmorIcon(GuiGraphics graphics, int x, int y, double armorValue) {
        drawResourceIcon(graphics, ARMOR_ICON, x, y, TOP_ICON_SIZE);

        String text = Integer.toString((int) Math.round(Math.max(0.0D, armorValue)));
        Font font = Minecraft.getInstance().font;
        int textX = x + (TOP_ICON_SIZE - font.width(text)) / 2;
        int textY = y + (TOP_ICON_SIZE - font.lineHeight) / 2 + 1;
        graphics.drawString(font, text, textX, textY, 0xFFFFFFFF, true);
    }

    private static void drawBarValue(GuiGraphics graphics, int barX, int rowY, double value, double max) {
        Font font = Minecraft.getInstance().font;
        String text = formatHudValue(value) + "/" + formatHudValue(max);
        int textX = barX + BAR_WIDTH - font.width(text) - 6;
        int textY = rowY + (BAR_TEXTURE_HEIGHT - font.lineHeight) / 2 + 3;
        graphics.drawString(font, text, textX, textY, 0xFFFFFFFF, true);
    }

    private static String formatHudValue(double value) {
        double safe = Double.isFinite(value) ? Math.max(0.0D, value) : 0.0D;
        long rounded = Math.round(safe);
        if (Math.abs(safe - rounded) < 0.05D) {
            return Long.toString(rounded);
        }
        return String.format(Locale.ROOT, "%.1f", safe);
    }

    private static void drawResourceIcon(GuiGraphics graphics, ResourceLocation texture, int x, int y) {
        drawResourceIcon(graphics, texture, x, y, ICON_SIZE);
    }

    private static void drawResourceIcon(GuiGraphics graphics, ResourceLocation texture, int x, int y, int size) {
        float scale = size / (float) ICON_SIZE;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        graphics.blit(texture, 0, 0, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        graphics.pose().popPose();
    }

    private static int hudHeight(int resourceRows) {
        if (resourceRows <= 0) {
            return TOP_ICON_SIZE;
        }
        int lastRowY = TOP_ICON_SIZE + TOP_TO_BAR_OFFSET + Math.max(0, resourceRows - 1) * BAR_ROW_ADVANCE;
        return Math.max(TOP_ICON_SIZE, lastRowY + BAR_TEXTURE_HEIGHT);
    }

    private static int animationV() {
        Minecraft minecraft = Minecraft.getInstance();
        int tick = minecraft.player == null ? 0 : minecraft.player.tickCount;
        return ((tick / 3) % 33) * 6;
    }

    private static double ratio(double value, double max) {
        if (!Double.isFinite(max) || max <= 0.0D) {
            return 0.0D;
        }
        double safeValue = Double.isFinite(value) ? value : 0.0D;
        return clamp01(safeValue / max);
    }

    private static double clamp01(double value) {
        return Math.max(0.0D, Math.min(1.0D, value));
    }

    private static ResourceLocation texture(String fileName) {
        return Objects.requireNonNull(
                ResourceLocation.tryBuild(TlSurvivalElements.MOD_ID, "textures/gui/hud/" + fileName)
        );
    }

    private enum ResourceStyle {
        HEALTH(new float[]{1.0F, 0.27F, 0.31F}),
        STAMINA(new float[]{1.0F, 0.82F, 0.18F}),
        MANA(new float[]{1.0F, 1.0F, 1.0F});

        private final float[] tint;

        ResourceStyle(float[] tint) {
            this.tint = tint;
        }

        private float[] tint() {
            return tint;
        }
    }
}
