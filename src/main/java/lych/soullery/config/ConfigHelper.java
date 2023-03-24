package lych.soullery.config;

import lych.soullery.Soullery;
import net.minecraft.util.SharedConstants;

public final class ConfigHelper {
    public static final String FAILHARD_MESSAGE = Soullery.MOD_NAME + ": This Throwable has been thrown because of Failhard --- ";

    private ConfigHelper() {}

    public static boolean isBossesTiered() {
        return CommonConfig.TIERED_BOSSES.get();
    }

    public static boolean shouldShowBossTier() {
        return CommonConfig.SHOW_BOSS_TIER.get();
    }

    public static boolean strictChallengesEnabled() {
        return CommonConfig.STRICT_CHALLENGES.get();
    }

    public static boolean canSEBlocksLoot() {
        return !CommonConfig.DISABLE_SE_BLOCKS_LOOT.get();
    }

    public static int getUltrareachLengthenPickupDelayAmount() {
        return CommonConfig.ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT.get();
    }

    public static boolean shouldFailhard() {
        return SharedConstants.IS_RUNNING_IN_IDE || CommonConfig.FAILHARD.get();
    }

    public static boolean shouldUseRomanNumeralGenerator() {
        return CommonConfig.ROMAN_GENERATOR.get();
    }

    public static int getRomanLimit() {
        return CommonConfig.ROMAN_LIMIT.get();
    }
}
