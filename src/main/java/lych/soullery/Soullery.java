package lych.soullery;

import lych.soullery.config.ClientConfig;
import lych.soullery.config.CommonConfig;
import lych.soullery.entity.ModAttributes;
import lych.soullery.item.crafting.ModRecipeSerializers;
import lych.soullery.potion.ModPotions;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Soullery.MOD_ID)
public class Soullery {
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_BIOMES = false;
    public static final String MOD_ID = "soullery";
    public static final String MOD_NAME = "Soullery";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public Soullery() {
        Soullery.LOGGER.debug(MOD_NAME + " Initialization...");
        if (DEBUG) {
            SharedConstants.IS_RUNNING_IN_IDE = true;
            SharedConstants.CHECK_DATA_FIXER_SCHEMA = false;
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModRecipeSerializers.SERIALIZERS.register(bus);
        ModAttributes.ATTRIBUTES.register(bus);
        ModPotions.POTIONS.register(bus);
        ModSoundEvents.SOUNDS.register(bus);
    }

    public static void checkTranslation() {
//        if (DEBUG) {
//            Bootstrap.getMissingTranslations().forEach(s -> LOGGER.error(MOD_NAME + " - Missing translations: " + s));
//        }
    }

    public static <T extends ForgeRegistryEntry<T>> T make(T value, String name) {
        return value.setRegistryName(MOD_ID, name);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static ResourceLocation prefixTex(String name) {
        return new ResourceLocation(MOD_ID, "textures/" + name);
    }

    public static String prefixMsg(String name) {
        return prefixMsg("message", name);
    }

    public static String prefixMsg(String type, String name) {
        return type + "." + MOD_ID + "." + name;
    }

    public static String prefixData(String name) {
        return MOD_ID + "." + name;
    }

    public static String prefixKeyMessage(String name) {
        return String.format("key.message.%s.%s", MOD_ID, name);
    }

    public static String prefixKeyCategory(String name) {
        return String.format("key.categories.%s.%s", MOD_ID, name);
    }

    public static ResourceLocation prefixShader(String name) {
        return Soullery.prefix(String.format("shaders/post/%s.json", name));
    }
}
