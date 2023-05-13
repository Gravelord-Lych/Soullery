package lych.soullery.world.gen.biome;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;

import static lych.soullery.world.gen.biome.ModBiomes.makeKey;

public final class SWBiomes {
    public static final RegistryKey<Biome> SOUL_WASTELANDS = makeKey(ModBiomeNames.SOUL_WASTELANDS);
    public static final RegistryKey<Biome> SOUL_WASTELANDS_II = makeKey(ModBiomeNames.SOUL_WASTELANDS_II);
    public static final RegistryKey<Biome> SOUL_WASTELANDS_III = makeKey(ModBiomeNames.SOUL_WASTELANDS_III);
    public static final RegistryKey<Biome> SOUL_WASTELANDS_IV = makeKey(ModBiomeNames.SOUL_WASTELANDS_IV);
    public static final RegistryKey<Biome> SOUL_WASTELANDS_V = makeKey(ModBiomeNames.SOUL_WASTELANDS_V);
    public static final RegistryKey<Biome> STRATEGIC_PLACE = makeKey(ModBiomeNames.STRATEGIC_PLACE);

    private SWBiomes() {}
}
