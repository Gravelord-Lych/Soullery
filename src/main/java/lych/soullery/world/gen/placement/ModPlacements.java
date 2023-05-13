package lych.soullery.world.gen.placement;

import lych.soullery.Soullery;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModPlacements {
    public static final Placement<FeatureSpreadConfig> HIGHER_FIRE = new HigherFirePlacement(FeatureSpreadConfig.CODEC);
    public static final Placement<ChanceConfig> LAKE_SOUL_LAVA = new LakeSoulLava(ChanceConfig.CODEC);

    @SubscribeEvent
    public static void registerPlacements(RegistryEvent.Register<Placement<?>> event) {
        IForgeRegistry<Placement<?>> registry = event.getRegistry();
        registry.register(make(HIGHER_FIRE, "higher_fire"));
        registry.register(make(LAKE_SOUL_LAVA, "lake_soul_lava"));
    }

    private ModPlacements() {}
}
