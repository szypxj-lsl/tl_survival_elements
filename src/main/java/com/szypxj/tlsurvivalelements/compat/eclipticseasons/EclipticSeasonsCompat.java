package com.szypxj.tlsurvivalelements.compat.eclipticseasons;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.ModList;

public final class EclipticSeasonsCompat {
    private static final boolean LOADED = ModList.get().isLoaded("eclipticseasons");

    private EclipticSeasonsCompat() {
    }

    public static boolean isLoaded() {
        return LOADED;
    }

    public static double seasonTemperatureModifier(ServerLevel level, double biomeTemperatureScale) {
        if (!LOADED || level == null) return 0.0D;
        return Loaded.seasonTemperatureModifier(level, biomeTemperatureScale);
    }

    public static WeatherState weather(ServerLevel level, BlockPos pos) {
        if (!LOADED || level == null || pos == null) return WeatherState.NONE;
        return Loaded.weather(level, pos);
    }

    public record WeatherState(boolean raining, boolean thundering) {
        public static final WeatherState NONE = new WeatherState(false, false);
    }

    private static final class Loaded {
        private Loaded() {
        }

        private static double seasonTemperatureModifier(ServerLevel level, double biomeTemperatureScale) {
            EclipticSeasonsApi api = EclipticSeasonsApi.getInstance();
            if (!api.isSeasonEnabled(level)) return 0.0D;
            return api.getSolarTerm(level).getTemperatureChange() * biomeTemperatureScale;
        }

        private static WeatherState weather(ServerLevel level, BlockPos pos) {
            EclipticSeasonsApi api = EclipticSeasonsApi.getInstance();
            boolean precipitation = api.isRainAt(level, pos) || api.isSnowAt(level, pos);
            return new WeatherState(precipitation, api.isThunderAt(level, pos));
        }
    }
}
