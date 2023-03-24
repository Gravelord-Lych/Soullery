package lych.soullery.world.gen.biome.sll;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

public enum SLEnlargeIslandLayer implements IBishopTransformer {
    INSTANCE;

    @Override
    public int apply(INoiseRandom random, int sw, int se, int nw, int ne, int self) {
        if (!SLLayer.isOcean(self) || SLLayer.allOcean(sw, se, nw, ne)) {
            if (!SLLayer.isOcean(self) && SLLayer.anyOcean(sw, se, nw, ne) && random.nextRandom(5) == 0) {
                if (SLLayer.isOcean(ne)) {
                    return self == SLLayer.LAND ? SLLayer.LAND : ne;
                }
                if (SLLayer.isOcean(sw)) {
                    return self == SLLayer.LAND ? SLLayer.LAND : sw;
                }
                if (SLLayer.isOcean(nw)) {
                    return self == SLLayer.LAND ? SLLayer.LAND : nw;
                }
                if (SLLayer.isOcean(se)) {
                    return self == SLLayer.LAND ? SLLayer.LAND : se;
                }
            }
            return self;
        } else {
            int bound = 1;
            int res = 1;
            if (!SLLayer.isOcean(ne) && random.nextRandom(bound++) == 0) {
                res = ne;
            }
            if (!SLLayer.isOcean(nw) && random.nextRandom(bound++) == 0) {
                res = nw;
            }
            if (!SLLayer.isOcean(sw) && random.nextRandom(bound++) == 0) {
                res = sw;
            }
            if (!SLLayer.isOcean(se) && random.nextRandom(bound) == 0) {
                res = se;
            }
            if (shouldModify(random)) {
                return res;
            } else {
                return res == SLLayer.LAND ? SLLayer.LAND : self;
            }
        }
    }

    private static boolean shouldModify(INoiseRandom random) {
        return random.nextRandom(3) == 0;
    }
}
