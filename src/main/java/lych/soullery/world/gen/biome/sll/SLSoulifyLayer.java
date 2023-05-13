package lych.soullery.world.gen.biome.sll;

import lych.soullery.world.gen.biome.ModBiomes;
import lych.soullery.world.gen.biome.SLBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import java.util.Arrays;

public enum SLSoulifyLayer implements ICastleTransformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (SLLayer.isPure(self)) {
            return self;
        }
        return Arrays.stream(new int[]{n, e, s, w}).anyMatch(SLLayer::isPure) ? ModBiomes.getId(SLBiomes.SOUL_PLAINS) : self;
    }
}
