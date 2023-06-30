package lych.soullery.util.mixin;

import it.unimi.dsi.fastutil.ints.IntSet;

public interface IClientPlayerMixin {
    float getEnhancedJumpStrength();

    IntSet getItemVanishingSlots();
}
