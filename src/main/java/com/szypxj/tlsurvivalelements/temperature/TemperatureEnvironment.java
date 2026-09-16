package com.szypxj.tlsurvivalelements.temperature;

import com.szypxj.tlsurvivalelements.compat.eclipticseasons.EclipticSeasonsCompat;
import com.szypxj.tlsurvivalelements.config.SurvivalConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public final class TemperatureEnvironment {
    private TemperatureEnvironment() {
    }

    public static double currentTemperature(ServerLevel level, BlockPos playerPos) {
        if (level == null || playerPos == null) {
            return (SurvivalConfig.BASE_MINIMUM_TEMPERATURE.get() + SurvivalConfig.BASE_MAXIMUM_TEMPERATURE.get()) * 0.5D;
        }

        double biomeTemperature = SurvivalConfig.BIOME_TEMPERATURE_OFFSET.get()
                + level.getBiome(playerPos).value().getBaseTemperature() * SurvivalConfig.BIOME_TEMPERATURE_SCALE.get();
        double altitude = TemperatureMath.altitudeModifier(
                playerPos.getY(),
                SurvivalConfig.TEMPERATURE_SEA_LEVEL.get(),
                SurvivalConfig.COOLING_PER_BLOCK_ABOVE_SEA_LEVEL.get(),
                SurvivalConfig.WARMING_PER_BLOCK_BELOW_SEA_LEVEL.get()
        );
        double weather = weatherModifier(level, playerPos);
        double season = seasonTemperatureModifier(level);
        double sources = sourceModifier(level, playerPos);
        return biomeTemperature + altitude + weather + season + sources;
    }

    private static double weatherModifier(ServerLevel level, BlockPos pos) {
        if (!level.canSeeSky(pos)) return 0.0D;

        if (EclipticSeasonsCompat.isLoaded()) {
            EclipticSeasonsCompat.WeatherState weather = EclipticSeasonsCompat.weather(level, pos);
            if (weather.thundering()) return SurvivalConfig.THUNDER_TEMPERATURE_MODIFIER.get();
            if (weather.raining()) return SurvivalConfig.RAIN_TEMPERATURE_MODIFIER.get();
            return 0.0D;
        }

        if (level.isThundering() && level.isRainingAt(pos)) {
            return SurvivalConfig.THUNDER_TEMPERATURE_MODIFIER.get();
        }
        if (level.isRainingAt(pos)) {
            return SurvivalConfig.RAIN_TEMPERATURE_MODIFIER.get();
        }
        return 0.0D;
    }

    private static double seasonTemperatureModifier(ServerLevel level) {
        return EclipticSeasonsCompat.seasonTemperatureModifier(level, SurvivalConfig.BIOME_TEMPERATURE_SCALE.get());
    }

    private static double sourceModifier(ServerLevel level, BlockPos center) {
        List<TemperatureSourceRule> rules = TemperatureSourceRules.rules();
        int maximumRadius = TemperatureSourceRules.maximumRadius();
        if (maximumRadius <= 0 || rules.isEmpty()) return 0.0D;

        double total = 0.0D;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -maximumRadius; dx <= maximumRadius; dx++) {
            for (int dy = -maximumRadius; dy <= maximumRadius; dy++) {
                for (int dz = -maximumRadius; dz <= maximumRadius; dz++) {
                    double distanceSquared = dx * dx + dy * dy + dz * dz;
                    if (distanceSquared > (double) maximumRadius * maximumRadius) continue;
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    if (!level.hasChunkAt(pos)) continue;

                    BlockState blockState = level.getBlockState(pos);
                    FluidState fluidState = blockState.getFluidState();
                    double strongest = 0.0D;
                    for (TemperatureSourceRule rule : rules) {
                        if (distanceSquared > (double) rule.radius() * rule.radius()) continue;
                        boolean matches = switch (rule.kind()) {
                            case BLOCK -> isActiveBlockSource(blockState) && rule.matches(blockState);
                            case FLUID -> rule.matches(fluidState);
                        };
                        if (!matches) continue;
                        double distance = Math.sqrt(distanceSquared);
                        double contribution = rule.temperatureModifier() * (1.0D - distance / (rule.radius() + 1.0D));
                        if (Math.abs(contribution) > Math.abs(strongest)) strongest = contribution;
                    }
                    total += strongest;
                }
            }
        }

        double limit = Math.max(0.0D, SurvivalConfig.MAXIMUM_SOURCE_TEMPERATURE_MODIFIER.get());
        return Math.max(-limit, Math.min(limit, total));
    }

    private static boolean isActiveBlockSource(BlockState state) {
        return !state.hasProperty(BlockStateProperties.LIT) || state.getValue(BlockStateProperties.LIT);
    }
}
