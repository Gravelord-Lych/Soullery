package lych.soullery.world.gen.chunkgen;

import lych.soullery.Soullery;
import lych.soullery.world.gen.dimension.ModDimensionNames;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static lych.soullery.Soullery.prefix;
import static net.minecraft.util.registry.Registry.register;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModChunkGenerators {
    public static final ResourceLocation DYNAMIC_SEED_CHUNKGEN = prefix("dynamic_seed_noise");

    private ModChunkGenerators() {}

    @SubscribeEvent
    public static void registerChunkGenerators(FMLCommonSetupEvent event) {
        register(Registry.CHUNK_GENERATOR, DYNAMIC_SEED_CHUNKGEN, DynamicSeedNoiseChunkGenerator.CODEC);
        register(Registry.CHUNK_GENERATOR, prefix(ModDimensionNames.ESV), ESVChunkGenerator.CODEC);
        register(Registry.CHUNK_GENERATOR, prefix(ModDimensionNames.ETHEREAL), EtherealChunkGenerator.CODEC);
        register(Registry.CHUNK_GENERATOR, prefix(ModDimensionNames.SUBWORLD), SubworldChunkGenerator.CODEC);
    }
}
