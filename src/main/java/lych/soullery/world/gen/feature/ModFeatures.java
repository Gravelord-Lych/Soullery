package lych.soullery.world.gen.feature;

import lych.soullery.Soullery;
import lych.soullery.world.gen.config.PlateauSpikeConfig;
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
    public static final Feature<PlateauSpikeConfig> PLATEAU_SPIKE = new PlateauSpikeFeature(PlateauSpikeConfig.CODEC);
    public static final Feature<SLSpikeConfig> SL_SPIKE = new SLSpikeFeature(SLSpikeConfig.CODEC);
    public static final Feature<NoFeatureConfig> SL_TWISTING_VINE = new SLTwistingVineFeature(NoFeatureConfig.CODEC);
    public static final Feature<NoFeatureConfig> SL_WEEPING_WINE = new SLWeepingVineFeature(NoFeatureConfig.CODEC);

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();
        registry.register(make(CENTRAL_CRYSTAL, "central_crystal"));
        registry.register(make(PLATEAU_SPIKE, "plateau_spike"));
        registry.register(make(SL_SPIKE, "sl_spike"));
        registry.register(make(SL_TWISTING_VINE, "sl_twisting_vine"));
        registry.register(make(SL_WEEPING_WINE, "sl_weeping_vine"));
    }

    private ModFeatures() {}
}
