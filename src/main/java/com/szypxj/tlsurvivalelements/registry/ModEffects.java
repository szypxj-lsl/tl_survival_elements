package com.szypxj.tlsurvivalelements.registry;

import com.szypxj.tlsurvivalelements.TlSurvivalElements;
import com.szypxj.tlsurvivalelements.effect.ThirstEffect;
import com.szypxj.tlsurvivalelements.effect.TemperatureEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TlSurvivalElements.MOD_ID);
    public static final RegistryObject<MobEffect> THIRST = REGISTER.register("thirst", ThirstEffect::new);
    public static final RegistryObject<MobEffect> COLD = REGISTER.register("cold", () -> new TemperatureEffect(0x70C8E8));
    public static final RegistryObject<MobEffect> HEATSTROKE = REGISTER.register("heatstroke", () -> new TemperatureEffect(0xE68A39));
    public static final RegistryObject<MobEffect> HYPOTHERMIA = REGISTER.register("hypothermia", () -> new TemperatureEffect(0x7AA7D8));

    private ModEffects() {
    }
}
