package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableMap;
import lych.soullery.Soullery;
import lych.soullery.entity.ModEntities;
import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class Reinforcements {
    public static final Reinforcement BAT = new BatReinforcement();
    public static final Reinforcement BEE = new BeeReinforcement();
    public static final Reinforcement BLAZE = new BlazeReinforcement();
    public static final Reinforcement CAT = new CatReinforcement(EntityType.CAT);
    public static final Reinforcement CAVE_SPIDER = new CaveSpiderReinforcement();
    public static final Reinforcement CHICKEN = new ChickenReinforcement();
    public static final Reinforcement COD = new FishReinforcement(EntityType.COD);
    public static final Reinforcement COW = new CowReinforcement();
    public static final Reinforcement CREEPER = new CreeperReinforcement();
    public static final Reinforcement DOLPHIN = new DolphinReinforcement();
    public static final Reinforcement DONKEY = new HorseReinforcement(EntityType.DONKEY);
    public static final Reinforcement DROWNED = new DrownedReinforcement();
    public static final Reinforcement ELDER_GUARDIAN = new ElderGuardianReinforcement();
    public static final Reinforcement ENDER_DRAGON = new EnderDragonReinforcement();
    public static final Reinforcement ENDERMAN = new EndermanReinforcement();
    public static final Reinforcement EVOKER = new EvokerReinforcement();
    public static final Reinforcement FOX = new FoxReinforcement();
    public static final Reinforcement GHAST = new GhastReinforcement();
    public static final Reinforcement GUARDIAN = new GuardianReinforcement();
    public static final Reinforcement HOGLIN = new HoglinReinforcement();
    public static final Reinforcement HORSE = new HorseReinforcement(EntityType.HORSE);
    public static final Reinforcement HUSK = new HuskReinforcement();
    public static final Reinforcement LLAMA = new HorseReinforcement(EntityType.LLAMA);
    public static final Reinforcement MAGMA_CUBE = new MagmaCubeReinforcement();
    public static final Reinforcement MOOSHROOM = new MooshroomReinforcement();
    public static final Reinforcement MULE = new HorseReinforcement(EntityType.MULE);
    public static final Reinforcement OCELOT = new CatReinforcement(EntityType.OCELOT);
    public static final Reinforcement PARROT = new ParrotReinforcement();
    public static final Reinforcement PHANTOM = new PhantomReinforcement();
    public static final Reinforcement PIG = new PigReinforcement();
    public static final Reinforcement PIGLIN = new PiglinReinforcement();
    public static final Reinforcement PILLAGER = new PillagerReinforcement();
    public static final Reinforcement POLAR_BEAR = new PolarBearReinforcement();
    public static final Reinforcement PUFFERFISH = new PufferfishReinforcement();
    public static final Reinforcement RABBIT = new RabbitReinforcement();
    public static final Reinforcement RAVAGER = new RavagerReinforcement();
    public static final Reinforcement SALMON = new FishReinforcement(EntityType.SALMON);
    public static final Reinforcement SHEEP = new SheepReinforcement();
    public static final Reinforcement SHULKER = new ShulkerReinforcement();
    public static final Reinforcement SKELETON = new SkeletonReinforcement();
    public static final Reinforcement SKELETON_HORSE = new HorseReinforcement(EntityType.SKELETON_HORSE).setSpecial();
    public static final Reinforcement SILVERFISH = new SilverfishReinforcement();
    public static final Reinforcement SLIME = new SlimeReinforcement();
    public static final Reinforcement SOUL_SKELETON = new SoulSkeletonReinforcement();
    public static final Reinforcement SPIDER = new SpiderReinforcement();
    public static final Reinforcement STRAY = new StrayReinforcement();
    public static final Reinforcement STRIDER = new StriderReinforcement();
    public static final Reinforcement SQUID = new SquidReinforcement();
    public static final Reinforcement TRADER_LLAMA = new HorseReinforcement(EntityType.TRADER_LLAMA);
    public static final Reinforcement TROPICAL_FISH = new FishReinforcement(EntityType.TROPICAL_FISH);
    public static final Reinforcement TURTLE = new TurtleReinforcement();
    public static final Reinforcement VINDICATOR = new VindicatorReinforcement();
    public static final Reinforcement WANDERER = new WandererReinforcement();
    public static final Reinforcement WANDERING_TRADER = new WanderingTraderReinforcement();
    public static final Reinforcement WITCH = new WitchReinforcement();
    public static final Reinforcement WITHER = new WitherReinforcement();
    public static final Reinforcement WITHER_SKELETON = new WitherSkeletonReinforcement();
    public static final Reinforcement WOLF = new WolfReinforcement();
    public static final Reinforcement ZOGLIN = new ZoglinReinforcement();
    public static final Reinforcement ZOMBIE = new ZombieReinforcement();
    public static final Reinforcement ZOMBIE_HORSE = new HorseReinforcement(EntityType.ZOMBIE_HORSE).setSpecial();
    public static final Reinforcement ZOMBIFIED_PIGLIN = new ZombifiedPiglinReinforcement();

    public static final double DEFAULT_SOUL_PIECE_DROP_PROBABILITY = 0.3;
    public static final double NO_DROP = 0;
    private static final double LOWER_SOUL_PIECE_DROP_PROBABILITY = 0.2;
    private static final double LOWEST_SOUL_PIECE_DROP_PROBABILITY = 0.15;
    private static final double HIGH_SOUL_PIECE_DROP_PROBABILITY = 0.45;
    private static final double HIGHER_SOUL_PIECE_DROP_PROBABILITY = 0.8;
    private static final double HIGHEST_SOUL_PIECE_DROP_PROBABILITY = 2;
    private static final double BOSS_SOUL_PIECE_DROP_PROBABILITY = 3;
    public static final int DEFAULT_SE_COST = 1000;
    private static final int LOW = 1000;
    private static final int MID = 1600;
    private static final int HIGH = 4000;
    private static final int BOSS = 15000;
    private static final int SINGLE_TIER_MUL = 5;
    private static final int SPECIAL_TIER_MUL = 3;
    private static final int DOUBLE_TIER_MUL = 2;
    public static final Marker REINFORCEMENT = MarkerManager.getMarker("Reinforcements");
    private static final Map<EntityType<?>, Reinforcement> REINFORCEMENTS;
    private static final Map<EntityType<?>, Double> PROBABILITY_MAP;
    private static final Map<Reinforcement, Integer> SE_COST_MAP;

    private Reinforcements() {}

    static {
        REINFORCEMENTS = new HashMap<>(64);
        PROBABILITY_MAP = new HashMap<>(64);
        SE_COST_MAP = new HashMap<>(64);
        for (Field field : Reinforcements.class.getFields()) {
            if (Reinforcement.class.isAssignableFrom(field.getType())) {
                try {
                    register((Reinforcement) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        setCustomSoulPieceDropProbability(EntityType.EVOKER, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.VINDICATOR, HIGHER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SHULKER, HIGH_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.PILLAGER, HIGH_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SILVERFISH, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.HUSK, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.DROWNED, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE_VILLAGER, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.CREEPER, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SPIDER, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SKELETON, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.STRAY, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIFIED_PIGLIN, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ENDERMAN, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ENDERMITE, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.WITHER, BOSS_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ENDER_DRAGON, BOSS_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.VILLAGER, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.PANDA, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.ELDER_GUARDIAN, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.GUARDIAN, HIGH_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.PHANTOM, HIGH_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.PARROT, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.POLAR_BEAR, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.IRON_GOLEM, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.SNOW_GOLEM, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.SLIME, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.MAGMA_CUBE, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.VEX, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.COD, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SALMON, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.TROPICAL_FISH, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.PUFFERFISH, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.BAT, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ILLUSIONER, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE_VILLAGER, NO_DROP);
        setCustomSoulPieceDropProbability(EntityType.COW, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.CHICKEN, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SHEEP, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.PIG, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.HORSE, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.DONKEY, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.MULE, LOWER_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE_HORSE, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.SKELETON_HORSE, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.RAVAGER, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.STRIDER, HIGH_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.FOX, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.WANDERING_TRADER, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(EntityType.WITCH, HIGH_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(ModEntities.SOUL_SKELETON, LOWEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(ModEntities.ENGINEER, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(ModEntities.DARK_EVOKER, HIGHEST_SOUL_PIECE_DROP_PROBABILITY);
        setCustomSoulPieceDropProbability(ModEntities.REDSTONE_TURRET, NO_DROP);
        setCustomSoulPieceDropProbability(ModEntities.REDSTONE_MORTAR, NO_DROP);
        setCustomSoulPieceDropProbability(ModEntities.META8, NO_DROP);
        setCustomSoulPieceDropProbability(ModEntities.ROBOT, NO_DROP);
        setBaseEnergyCost(BAT, LOW);
        setBaseEnergyCost(BEE, LOW);
        setBaseEnergyCost(BLAZE, MID * SINGLE_TIER_MUL);
        setBaseEnergyCost(CAT, LOW);
        setBaseEnergyCost(CAVE_SPIDER, MID);
        setBaseEnergyCost(CHICKEN, LOW);
        setBaseEnergyCost(COD, LOW);
        setBaseEnergyCost(COW, LOW);
        setBaseEnergyCost(CREEPER, MID);
        setBaseEnergyCost(DOLPHIN, LOW);
        setBaseEnergyCost(DONKEY, LOW);
        setBaseEnergyCost(DROWNED, MID);
        setBaseEnergyCost(ELDER_GUARDIAN, HIGH);
        setBaseEnergyCost(ENDER_DRAGON, BOSS);
        setBaseEnergyCost(ENDERMAN, MID);
        setBaseEnergyCost(EVOKER, HIGH);
        setBaseEnergyCost(FOX, LOW);
        setBaseEnergyCost(GHAST, MID);
        setBaseEnergyCost(GUARDIAN, MID);
        setBaseEnergyCost(HOGLIN, MID);
        setBaseEnergyCost(HORSE, LOW);
        setBaseEnergyCost(HUSK, MID);
        setBaseEnergyCost(LLAMA, LOW);
        setBaseEnergyCost(MAGMA_CUBE, MID);
        setBaseEnergyCost(MOOSHROOM, LOW);
        setBaseEnergyCost(MULE, LOW);
        setBaseEnergyCost(OCELOT, LOW);
        setBaseEnergyCost(PARROT, LOW);
        setBaseEnergyCost(PHANTOM, MID);
        setBaseEnergyCost(PIG, LOW * DOUBLE_TIER_MUL);
        setBaseEnergyCost(PIGLIN, MID);
        setBaseEnergyCost(PILLAGER, MID);
        setBaseEnergyCost(POLAR_BEAR, LOW);
        setBaseEnergyCost(PUFFERFISH, LOW);
        setBaseEnergyCost(RABBIT, LOW);
        setBaseEnergyCost(RAVAGER, MID * DOUBLE_TIER_MUL);
        setBaseEnergyCost(SALMON, LOW);
        setBaseEnergyCost(SHEEP, LOW);
        setBaseEnergyCost(SHULKER, MID);
        setBaseEnergyCost(SKELETON, LOW);
        setBaseEnergyCost(SKELETON_HORSE, LOW * SPECIAL_TIER_MUL);
        setBaseEnergyCost(SILVERFISH, LOW);
        setBaseEnergyCost(SLIME, MID * SINGLE_TIER_MUL);
        setBaseEnergyCost(SPIDER, MID);
        setBaseEnergyCost(STRAY, MID);
        setBaseEnergyCost(STRIDER, MID);
        setBaseEnergyCost(SOUL_SKELETON, MID * SINGLE_TIER_MUL);
        setBaseEnergyCost(SQUID, LOW);
        setBaseEnergyCost(TRADER_LLAMA, LOW);
        setBaseEnergyCost(TROPICAL_FISH, LOW);
        setBaseEnergyCost(TURTLE, LOW * SINGLE_TIER_MUL);
        setBaseEnergyCost(VINDICATOR, MID);
        setBaseEnergyCost(WANDERER, MID);
        setBaseEnergyCost(WANDERING_TRADER, MID);
        setBaseEnergyCost(WITCH, MID);
        setBaseEnergyCost(WITHER, BOSS * SINGLE_TIER_MUL);
        setBaseEnergyCost(WITHER_SKELETON, MID);
        setBaseEnergyCost(WOLF, LOW);
        setBaseEnergyCost(ZOGLIN, MID);
        setBaseEnergyCost(ZOMBIE, MID);
        setBaseEnergyCost(ZOMBIE_HORSE, LOW * SPECIAL_TIER_MUL);
        setBaseEnergyCost(ZOMBIFIED_PIGLIN, MID);
        postRegistration();
        selfCheck();
    }

    public static void register(Reinforcement reinforcement) {
        bind(reinforcement.getType(), reinforcement);
    }

    @SuppressWarnings("unused")
    public static <T extends Reinforcement & OptionalReinforcement> void registerOptional(T reinforcement) {
        if (reinforcement.isPresent()) {
            bind(reinforcement.getType(), reinforcement);
        }
    }

    public static ImmutableMap<EntityType<?>, Reinforcement> getReinforcements() {
        return ImmutableMap.copyOf(REINFORCEMENTS);
    }

    public static double getDropProbability(EntityType<?> type) {
        return PROBABILITY_MAP.getOrDefault(type, DEFAULT_SOUL_PIECE_DROP_PROBABILITY);
    }

    public static void setCustomSoulPieceDropProbability(EntityType<?> type, double value) {
        PROBABILITY_MAP.put(type, value);
    }

    public static void setBaseEnergyCost(Reinforcement reinforcement, int cost) {
        SE_COST_MAP.put(reinforcement, cost);
    }

    public static int getBaseEnergyCost(Reinforcement reinforcement) {
        return SE_COST_MAP.getOrDefault(reinforcement, DEFAULT_SE_COST);
    }

    public static int getEnergyCost(Reinforcement reinforcement, int oldLevel, int newLevel) {
        return getBaseEnergyCost(reinforcement) * reinforcement.getCost(oldLevel, newLevel);
    }

    private static void bind(EntityType<?> type, Reinforcement reinforcement) {
        REINFORCEMENTS.put(type, reinforcement);
        if (reinforcement.hasEvents()) {
            MinecraftForge.EVENT_BUS.register(reinforcement);
        }
    }

    private static void selfCheck() {
        for (Reinforcement reinforcement : REINFORCEMENTS.values()) {
            if (!SE_COST_MAP.containsKey(reinforcement)) {
                Soullery.LOGGER.warn(REINFORCEMENT, "Reinforcement of {} has no SE cost, used default value {}", Utils.getRegistryName(reinforcement.getType()), DEFAULT_SE_COST);
            }
        }
    }

    public static void postRegistration() {
//        TODO
    }

    @Nullable
    public static Reinforcement get(EntityType<?> type) {
        return REINFORCEMENTS.get(type);
    }

    public static void init() {
        Soullery.LOGGER.info(REINFORCEMENT, "Initializing reinforcements...");
    }
}
