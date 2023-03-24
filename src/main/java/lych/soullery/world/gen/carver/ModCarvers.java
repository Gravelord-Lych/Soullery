package lych.soullery.world.gen.carver;

import lych.soullery.Soullery;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCarvers {
    public static final WorldCarver<ProbabilityConfig> SOUL_CAVE = new SoulCaveCarver(ProbabilityConfig.CODEC, 256);
    public static final WorldCarver<ProbabilityConfig> SOUL_CANYON = new SoulCanyonCarver(ProbabilityConfig.CODEC);

    private ModCarvers() {}

    @SubscribeEvent
    public static void registerWorldCarvers(RegistryEvent.Register<WorldCarver<?>> event) {
        IForgeRegistry<WorldCarver<?>> registry = event.getRegistry();
        registry.register(make(SOUL_CAVE, ModCarverNames.SOUL_CAVE));
        registry.register(make(SOUL_CANYON, ModCarverNames.SOUL_CANYON));
    }
}
