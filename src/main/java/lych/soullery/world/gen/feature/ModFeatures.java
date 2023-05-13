package lych.soullery.world.gen.feature;

import lych.soullery.Soullery;
import lych.soullery.world.gen.config.PlateauSpikeConfig;
import lych.soullery.world.gen.config.StreetlightConfig;
import lych.soullery.world.gen.config.WatchtowerConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModFeatures {
    public static final Feature<NoFeatureConfig> CENTRAL_CRYSTAL = new CentralSoulCrystalFeature(NoFeatureConfig.CODEC);
    public static final Feature<WatchtowerConfig> LARGE_WATCHTOWER = new LargeWatchtowerFeature(WatchtowerConfig.CODEC, 2);
    public static final Feature<PlateauSpikeConfig> PLATEAU_SPIKE = new PlateauSpikeFeature(PlateauSpikeConfig.CODEC);
    public static final Feature<SLSpikeConfig> SL_SPIKE = new SLSpikeFeature(SLSpikeConfig.CODEC);
    public static final Feature<NoFeatureConfig> SL_TWISTING_VINE = new SLTwistingVineFeature(NoFeatureConfig.CODEC);
    public static final Feature<NoFeatureConfig> SL_WEEPING_WINE = new SLWeepingVineFeature(NoFeatureConfig.CODEC);
    public static final Feature<WatchtowerConfig> SMALL_WATCHTOWER = new SmallWatchtowerFeature(WatchtowerConfig.CODEC, 1);
    public static final Feature<StreetlightConfig> STREETLIGHT = new StreetlightFeature(StreetlightConfig.CODEC);

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();
        registry.register(make(CENTRAL_CRYSTAL, "central_crystal"));
        registry.register(make(LARGE_WATCHTOWER, "large_watchtower"));
        registry.register(make(PLATEAU_SPIKE, "plateau_spike"));
        registry.register(make(SL_SPIKE, "sl_spike"));
        registry.register(make(SL_TWISTING_VINE, "sl_twisting_vine"));
        registry.register(make(SL_WEEPING_WINE, "sl_weeping_vine"));
        registry.register(make(SMALL_WATCHTOWER, "small_watchtower"));
        registry.register(make(STREETLIGHT, "streetlight"));
    }

    private ModFeatures() {}
}
