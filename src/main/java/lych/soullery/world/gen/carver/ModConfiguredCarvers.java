package lych.soullery.world.gen.carver;

import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public final class ModConfiguredCarvers {
    public static final ConfiguredCarver<ProbabilityConfig> SOUL_CAVE = ModCarvers.SOUL_CAVE.configured(new ProbabilityConfig(0.1f));
    public static final ConfiguredCarver<ProbabilityConfig> RARE_SOUL_CAVE = ModCarvers.SOUL_CAVE.configured(new ProbabilityConfig(0.05f));
    public static final ConfiguredCarver<ProbabilityConfig> SOUL_CANYON = ModCarvers.SOUL_CANYON.configured(new ProbabilityConfig(0.008f));

    private ModConfiguredCarvers() {}
}