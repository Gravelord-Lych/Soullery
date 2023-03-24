package lych.soullery.entity.iface;

public interface ITieredBoss extends ITieredMob, IDamageMultipliable, IHasResistance {
    @Override
    default float getResistance() {
        if (reachedTier(getFirstTierThatHasResistance())) {
            return Math.min((float) (1 - Math.pow(0.98, getTier() - 20)), 0.99f);
        }
        return 0;
    }

    default int getFirstTierThatHasResistance() {
        return 21;
    }
}
