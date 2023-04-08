package lych.soullery.config;

import lych.soullery.Soullery;
import lych.soullery.item.ModItemNames;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID)
public class ClientConfig {
    public static final ForgeConfigSpec CLIENT_CONFIG;
    static final ForgeConfigSpec.BooleanValue THIRD_PERSON_ON_OPERATION;
    static final ForgeConfigSpec.BooleanValue FIRST_PERSON_RESET_AFTER_OPERATION;


    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

        commonBuilder.push("Soul Controlling Settings");
        THIRD_PERSON_ON_OPERATION = commonBuilder
                .comment(String.format("If true, when you starts operating a mob using %s or logging in with an mob operated by you, your point of view will be automatically set to ThirdPerson", Soullery.prefix(ModItemNames.MIND_OPERATOR)))
                .define("autoThirdPersonOnOperation", true);
        FIRST_PERSON_RESET_AFTER_OPERATION = commonBuilder
                .comment(String.format("If true, when you stops operating a mob using %s, your point of view will be automatically reset to FirstPerson", Soullery.prefix(ModItemNames.MIND_OPERATOR)))
                .define("autoFirstPersonAfterOperation", true);
        commonBuilder.pop();

        CLIENT_CONFIG = commonBuilder.build();
    }
}
