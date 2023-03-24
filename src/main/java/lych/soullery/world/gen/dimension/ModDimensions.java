package lych.soullery.world.gen.dimension;

import lych.soullery.Soullery;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ModDimensions {
    public static final RegistryKey<World> ESV = RegistryKey.create(Registry.DIMENSION_REGISTRY, Soullery.prefix(ModDimensionNames.ESV));
    public static final RegistryKey<World> ETHEREAL = RegistryKey.create(Registry.DIMENSION_REGISTRY, Soullery.prefix(ModDimensionNames.ETHEREAL));
    public static final RegistryKey<World> SOUL_LAND = RegistryKey.create(Registry.DIMENSION_REGISTRY, Soullery.prefix(ModDimensionNames.SOUL_LAND));
    public static final RegistryKey<World> SUBWORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, Soullery.prefix(ModDimensionNames.SUBWORLD));

    private ModDimensions() {}
}
