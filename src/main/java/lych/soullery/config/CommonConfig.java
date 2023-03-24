package lych.soullery.config;

import lych.soullery.Soullery;
import lych.soullery.util.ExtraAbilityConstants;
import lych.soullery.util.RomanNumeralGenerator;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID)
public class CommonConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;
    static final BooleanValue DISABLE_SE_BLOCKS_LOOT;
    static final BooleanValue SHOW_BOSS_TIER;
    static final BooleanValue STRICT_CHALLENGES;
    static final BooleanValue TIERED_BOSSES;
    static final IntValue ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT;
    static final BooleanValue FAILHARD;
    static final BooleanValue ROMAN_GENERATOR;
    static final IntValue ROMAN_LIMIT;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

        commonBuilder.push("Mob Settings");
        TIERED_BOSSES = commonBuilder
                .comment("If true, all mod bosses will be tiered (When you defeat them, they'll become stronger).")
                .define("tieredBosses", false);
        SHOW_BOSS_TIER = commonBuilder
                .comment("If true, all boss-tier will be shown.")
                .define("showBossTier", false);
        commonBuilder.pop();

        commonBuilder.push("Challenge Settings");
        STRICT_CHALLENGES = commonBuilder
                .comment("If true, cheats and unreasonable weapons will be disabled in the challenges.")
                .define("strictChallenges", false);
        commonBuilder.pop();

        commonBuilder.push("Block Settings");
        DISABLE_SE_BLOCKS_LOOT = commonBuilder
                .comment("If true, SE Generators and SE Storages will drop nothing when they are destroyed in Creative Mode, regardless of how much SE is inside them.")
                .define("disableSEBlocksLootIfCreative", false);
        commonBuilder.pop();

        commonBuilder.push("Extra Ability Settings");
        ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT = commonBuilder
                .comment("The additional pickup delay for items that are thrown by a player who has Ultrareach Extra Ability.")
                .defineInRange("ultrareachLengthenPickupDelayAmount(tick)", ExtraAbilityConstants.DEFAULT_ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT, 0, ExtraAbilityConstants.DEFAULT_ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT * 2);
        commonBuilder.pop();

        commonBuilder.push("Other Settings");
        FAILHARD = commonBuilder
                .comment("If true, Fail-hard behavior will be enabled for " + Soullery.MOD_NAME + " Mod.", "This behavior makes the program throw an exception instead of logging an error when something went wrong. May cause more crashes but is useful for finding out bugs.")
                .define("failhard", false);
        ROMAN_GENERATOR = commonBuilder
                .comment("If true, " + Soullery.MOD_NAME + " Mod will use Roman numeral generator instead of using translation key(\"enchantment.level.XX\") to generate Roman numbers.")
                .define("usingRomanNumeralGenerator", true);
        ROMAN_LIMIT = commonBuilder
                .comment("The maximum number that can be converted to Roman numeral by the Roman numeral generator. Numbers which are bigger than this number will not be converted to Roman numerals.")
                .defineInRange("maxConvertibleNumber", 100, 10, RomanNumeralGenerator.MAX_CONVERTIBLE);
        commonBuilder.pop();

        COMMON_CONFIG = commonBuilder.build();
    }
}
