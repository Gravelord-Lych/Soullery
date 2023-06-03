package lych.soullery.world.gen.structure;

import lych.soullery.Soullery;
import lych.soullery.world.gen.config.SoulTowerConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModStructures {
    public static final FireTempleStructure FIRE_TEMPLE = new FireTempleStructure(NoFeatureConfig.CODEC);
    public static final SoulTowerStructure SOUL_TOWER = new SoulTowerStructure(SoulTowerConfig.CODEC);

    private ModStructures() {}

    @SubscribeEvent
    public static void registerStructures(RegistryEvent.Register<Structure<?>> event) {
        IForgeRegistry<Structure<?>> registry = event.getRegistry();
        register(registry, ModStructureNames.FIRE_TEMPLE, FIRE_TEMPLE);
        register(registry, ModStructureNames.SOUL_TOWER, SOUL_TOWER);
    }

    private static <T extends Structure<C>, C extends IFeatureConfig> void register(IForgeRegistry<Structure<?>> registry, String name, T structure) {
        registry.register(Soullery.make(structure, name));
        Structure.STRUCTURES_REGISTRY.put(name, structure);
    }
}
