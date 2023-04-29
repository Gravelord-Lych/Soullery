package lych.soullery.effect;

import lych.soullery.Soullery;
import lych.soullery.extension.fire.Fires;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEffects {
    public static final Effect CATASTROPHE_OMEN = new CommonEffect(EffectType.NEUTRAL, 0x441818);
    public static final Effect REVERSION = new CommonEffect(EffectType.HARMFUL, 0xf800f8);
    public static final Effect PURE_SOUL_FIRED = new InstantFireEffect(EffectType.HARMFUL, 0x0055ff, Fires.PURE_SOUL_FIRE);
    public static final Effect SOUL_FIRED = new InstantFireEffect(EffectType.HARMFUL, 0x00eeff, Fires.SOUL_FIRE);
    public static final Effect SOUL_POLLUTION = new PollutionEffect(EffectType.HARMFUL, 0x00dddd);

    private ModEffects() {}

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event) {
        IForgeRegistry<Effect> registry = event.getRegistry();
        registry.register(make(CATASTROPHE_OMEN, ModEffectNames.CATASTROPHE_OMEN));
        registry.register(make(PURE_SOUL_FIRED, ModEffectNames.PURE_SOUL_FIRED));
        registry.register(make(REVERSION, ModEffectNames.REVERSION));
        registry.register(make(SOUL_POLLUTION, ModEffectNames.SOUL_POLLUTION));
        registry.register(make(SOUL_FIRED, ModEffectNames.SOUL_FIRED));
    }
}
