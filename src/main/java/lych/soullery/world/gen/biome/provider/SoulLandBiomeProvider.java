package lych.soullery.world.gen.biome.provider;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.Soullery;
import lych.soullery.world.SeedHelper;
import lych.soullery.world.gen.biome.sll.SLLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.layer.Layer;

public class SoulLandBiomeProvider extends BiomeProvider {
    public static final Codec<SoulLandBiomeProvider> CODEC = RecordCodecBuilder.create(SoulLandBiomeProvider::makeCodec);
    public static final ResourceLocation SOUL_LAND = Soullery.prefix("soul_land");
    private final long seed;
    private final Registry<Biome> registry;
    private final Layer noiseBiomeLayer;

    public SoulLandBiomeProvider(long seed, Registry<Biome> registry) {
        super(SLLayer.getAllBiomes().map(key -> () -> registry.getOrThrow(key)));
        this.registry = registry;
        this.seed = seed;
        this.noiseBiomeLayer = SLLayer.getDefaultLayer(seed, 4, 4);
    }

    @Override
    protected Codec<? extends BiomeProvider> codec() {
        return CODEC;
    }

    @Override
    public BiomeProvider withSeed(long seed) {
        return new SoulLandBiomeProvider(seed, registry);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return noiseBiomeLayer.get(registry, x, z);
    }

    private long getSeed() {
        return seed;
    }

    private Registry<Biome> getRegistry() {
        return registry;
    }

    private static App<RecordCodecBuilder.Mu<SoulLandBiomeProvider>, SoulLandBiomeProvider> makeCodec(RecordCodecBuilder.Instance<SoulLandBiomeProvider> instance) {
        return instance.group(Codec.LONG.fieldOf("seed").stable().orElseGet(SeedHelper::getSeed).forGetter(SoulLandBiomeProvider::getSeed),
                RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(SoulLandBiomeProvider::getRegistry)).apply(instance, instance.stable(SoulLandBiomeProvider::new));
    }
}
