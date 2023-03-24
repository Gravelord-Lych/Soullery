package lych.soullery.world.gen.biome.sll;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLExpandPurificationLayer implements ICastleTransformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        if (SLLayer.isPure(self) || SLLayer.anyOcean(n, e, s, w, self)) {
            return self;
        }
        boolean shouldPurify = false;
        int bound = 4;
        for (int id : new int[]{n, e, s, w}) {
            if (SLLayer.isPure(id)) {
                bound = Math.max(1, bound - 1);
                shouldPurify = true;
            }
        }
        if (!shouldPurify) {
            return self;
        }
        return random.nextRandom(bound) == 0 ? SLLayer.PURE : self;
    }
}
