package lych.soullery.effect;

import lych.soullery.Soullery;
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
    public static final Effect SOUL_POLLUTION = new PollutionEffect(EffectType.HARMFUL, 0x00dddd);

    private ModEffects() {}

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event) {
        IForgeRegistry<Effect> registry = event.getRegistry();
        registry.register(make(CATASTROPHE_OMEN, ModEffectNames.CATASTROPHE_OMEN));
        registry.register(make(REVERSION, ModEffectNames.REVERSION));
        registry.register(make(SOUL_POLLUTION, ModEffectNames.SOUL_POLLUTION));
    }
}
