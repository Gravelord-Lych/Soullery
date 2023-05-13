package lych.soullery.world.gen.biome.swl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lych.soullery.util.ArrayUtils;
import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SWBiomes;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public enum SWSurroundLayer implements ICastleTransformer {
    I(1, 0),
    II(2, 1),
    III(3, 2),
    IV(4, 3),
    V(5, 4),
    PRE_ENLARGER(5, 5);

    public static final List<SWSurroundLayer> TRANSFORM_ORDER = Arrays.stream(ArrayUtils.reversed(values())).skip(1).collect(ImmutableList.toImmutableList());
    public static final List<RegistryKey<Biome>> STEPS = ImmutableList.of(SWBiomes.SOUL_WASTELANDS, SWBiomes.SOUL_WASTELANDS_II, SWBiomes.SOUL_WASTELANDS_III, SWBiomes.SOUL_WASTELANDS_IV, SWBiomes.SOUL_WASTELANDS_V, SWBiomes.STRATEGIC_PLACE);
    public static final Set<RegistryKey<Biome>> STEPS_SET = STEPS.stream().skip(1).collect(ImmutableSet.toImmutableSet());

    private final int targetId;
    private final int transformId;

    SWSurroundLayer(int targetId, int transformId) {
        this.targetId = targetId;
        this.transformId = transformId;
    }

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (!STEPS_SET.contains(ModBiomes.byId(self)) && IntStream.of(n, e, s, w).mapToObj(ModBiomes::byId).anyMatch(biome -> STEPS.indexOf(biome) == targetId)) {
            return redirect(transformId);
        }
        return self;
    }

    private static int redirect(int rId) {
        return ModBiomes.getId(STEPS.get(rId));
    }
}
