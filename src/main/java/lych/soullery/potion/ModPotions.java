package lych.soullery.potion;

import lych.soullery.Soullery;
import lych.soullery.effect.ModEffectNames;
import lych.soullery.effect.ModEffects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Objects;

public final class ModPotions {
    public static final Marker MARKER = MarkerManager.getMarker("Potions");
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTION_TYPES, Soullery.MOD_ID);
    public static final RegistryObject<Potion> DECAY = POTIONS.register(ModPotionCustomNames.DECAY, () -> new Potion(new EffectInstance(Effects.WITHER, 900)));
    public static final RegistryObject<Potion> LONG_DECAY = POTIONS.register(toLong(ModPotionCustomNames.DECAY), () -> new Potion(ModPotionCustomNames.DECAY, new EffectInstance(Effects.WITHER, 1800)));
    public static final RegistryObject<Potion> STRONG_DECAY = POTIONS.register(toStrong(ModPotionCustomNames.DECAY), () -> new Potion(ModPotionCustomNames.DECAY, new EffectInstance(Effects.WITHER, 450, 1)));
    public static final RegistryObject<Potion> LEVITATION = POTIONS.register(ModPotionCustomNames.LEVITATION, () -> new Potion(new EffectInstance(Effects.LEVITATION, 200)));
    public static final RegistryObject<Potion> LONG_LEVITATION = POTIONS.register(toLong(ModPotionCustomNames.LEVITATION), () -> new Potion(ModPotionCustomNames.LEVITATION, new EffectInstance(Effects.LEVITATION, 400)));
    public static final RegistryObject<Potion> STRONG_LEVITATION = POTIONS.register(toStrong(ModPotionCustomNames.LEVITATION), () -> new Potion(ModPotionCustomNames.LEVITATION, new EffectInstance(Effects.LEVITATION, 100, 2)));
    public static final RegistryObject<Potion> REVERSION = POTIONS.register(ModEffectNames.REVERSION, () -> new Potion(new EffectInstance(ModEffects.REVERSION, 900)));
    public static final RegistryObject<Potion> LONG_REVERSION = POTIONS.register(toLong(ModEffectNames.REVERSION), () -> new Potion(ModEffectNames.REVERSION, new EffectInstance(ModEffects.REVERSION, 1800)));
    public static final RegistryObject<Potion> STARVATION = POTIONS.register(ModPotionCustomNames.STARVATION, () -> new Potion(new EffectInstance(Effects.HUNGER, 900)));
    public static final RegistryObject<Potion> LONG_STARVATION = POTIONS.register(toLong(ModPotionCustomNames.STARVATION), () -> new Potion(ModPotionCustomNames.STARVATION, new EffectInstance(Effects.HUNGER, 1800)));
    public static final RegistryObject<Potion> STRONG_STARVATION = POTIONS.register(toStrong(ModPotionCustomNames.STARVATION), () -> new Potion(ModPotionCustomNames.STARVATION, new EffectInstance(Effects.HUNGER, 450, 2), new EffectInstance(Effects.DIG_SLOWDOWN, 450)));
    public static final RegistryObject<Potion> ULTRABOOST = POTIONS.register(ModPotionCustomNames.ULTRABOOST, () -> new Potion(new EffectInstance(Effects.MOVEMENT_SPEED, 200, 19), new EffectInstance(Effects.JUMP, 200, 9)));
    public static final RegistryObject<Potion> LONG_ULTRABOOST = POTIONS.register(toLong(ModPotionCustomNames.ULTRABOOST), () -> new Potion(ModPotionCustomNames.ULTRABOOST, new EffectInstance(Effects.MOVEMENT_SPEED, 400, 19), new EffectInstance(Effects.JUMP, 400, 9)));
    public static final RegistryObject<Potion> STRONG_ULTRABOOST = POTIONS.register(toStrong(ModPotionCustomNames.ULTRABOOST), () -> new Potion(ModPotionCustomNames.ULTRABOOST, new EffectInstance(Effects.MOVEMENT_SPEED, 200, 39), new EffectInstance(Effects.JUMP, 200, 19)));
    public static final RegistryObject<Potion> STRONG_WEAKNESS = POTIONS.register(toStrong(Objects.requireNonNull(Potions.WEAKNESS.getRegistryName()).getPath()), () -> new Potion(Objects.requireNonNull(Potions.WEAKNESS.getRegistryName()).getPath(), new EffectInstance(Effects.WEAKNESS, 900, 1)));

    private ModPotions() {}

    private static String toLong(String name) {
        return "long_" + name;
    }

    private static String toStrong(String name) {
        return "strong_" + name;
    }

    public static void registerBrewingRecipes() {
        Soullery.LOGGER.info(MARKER, "Registering mod brewing recipes...");
    }
}
