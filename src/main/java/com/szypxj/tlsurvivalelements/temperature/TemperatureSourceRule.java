package com.szypxj.tlsurvivalelements.temperature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;

public record TemperatureSourceRule(
        Kind kind,
        ResourceLocation id,
        boolean tag,
        double temperatureModifier,
        int radius
) {
    public enum Kind {
        BLOCK,
        FLUID
    }

    public boolean matches(BlockState state) {
        if (kind != Kind.BLOCK || state == null || id == null) return false;
        if (tag) {
            TagKey<Block> key = TagKey.create(Registries.BLOCK, id);
            return state.is(key);
        }
        return id.equals(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
    }

    public boolean matches(FluidState state) {
        if (kind != Kind.FLUID || state == null || state.isEmpty() || id == null) return false;
        if (tag) {
            TagKey<Fluid> key = TagKey.create(Registries.FLUID, id);
            return state.is(key);
        }
        return id.equals(ForgeRegistries.FLUIDS.getKey(state.getType()));
    }

    public static TemperatureSourceRule parse(String text) {
        if (text == null) return null;
        int colon = text.indexOf(':');
        int equals = text.lastIndexOf('=');
        int at = text.lastIndexOf('@');
        if (colon <= 0 || equals <= colon + 1 || at <= equals + 1 || at >= text.length() - 1) return null;

        Kind kind = switch (text.substring(0, colon).trim()) {
            case "block" -> Kind.BLOCK;
            case "fluid" -> Kind.FLUID;
            default -> null;
        };
        if (kind == null) return null;

        String selector = text.substring(colon + 1, equals).trim();
        boolean tag = selector.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(tag ? selector.substring(1) : selector);
        if (id == null) return null;

        try {
            double modifier = Double.parseDouble(text.substring(equals + 1, at).trim());
            int radius = Integer.parseInt(text.substring(at + 1).trim());
            if (!Double.isFinite(modifier) || radius <= 0) return null;
            return new TemperatureSourceRule(kind, id, tag, modifier, radius);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
