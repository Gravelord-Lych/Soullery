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
    public static final int DEFAULT_SE_COST = 200;
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
        setCustomSoulPieceDropProbability(EntityType.EVOKER, 1.5);
        setCustomSoulPieceDropProbability(EntityType.VINDICATOR, 0.7);
        setCustomSoulPieceDropProbability(EntityType.PILLAGER, 0.5);
        setCustomSoulPieceDropProbability(EntityType.SILVERFISH, 0.1);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE, 0.2);
        setCustomSoulPieceDropProbability(EntityType.HUSK, 0.2);
        setCustomSoulPieceDropProbability(EntityType.DROWNED, 0.2);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE_VILLAGER, 0.2);
        setCustomSoulPieceDropProbability(EntityType.CREEPER, 0.2);
        setCustomSoulPieceDropProbability(EntityType.SPIDER, 0.2);
        setCustomSoulPieceDropProbability(EntityType.SKELETON, 0.2);
        setCustomSoulPieceDropProbability(EntityType.STRAY, 0.2);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIFIED_PIGLIN, 0.24);
        setCustomSoulPieceDropProbability(EntityType.ENDERMAN, 0.25);
        setCustomSoulPieceDropProbability(EntityType.ENDERMITE, 0);
        setCustomSoulPieceDropProbability(EntityType.WITHER, 2.5);
        setCustomSoulPieceDropProbability(EntityType.ENDER_DRAGON, 3);
        setCustomSoulPieceDropProbability(EntityType.VILLAGER, 0);
        setCustomSoulPieceDropProbability(EntityType.PANDA, 0);
        setCustomSoulPieceDropProbability(EntityType.ELDER_GUARDIAN, 1.5);
        setCustomSoulPieceDropProbability(EntityType.GUARDIAN, 0.4);
        setCustomSoulPieceDropProbability(EntityType.PHANTOM, 0.4);
        setCustomSoulPieceDropProbability(EntityType.PARROT, 0.3);
        setCustomSoulPieceDropProbability(EntityType.POLAR_BEAR, 0.3);
        setCustomSoulPieceDropProbability(EntityType.IRON_GOLEM, 0);
        setCustomSoulPieceDropProbability(EntityType.SNOW_GOLEM, 0);
        setCustomSoulPieceDropProbability(EntityType.SLIME, 0.15);
        setCustomSoulPieceDropProbability(EntityType.VEX, 0);
        setCustomSoulPieceDropProbability(EntityType.COD, 0.1);
        setCustomSoulPieceDropProbability(EntityType.SALMON, 0.1);
        setCustomSoulPieceDropProbability(EntityType.TROPICAL_FISH, 0.15);
        setCustomSoulPieceDropProbability(EntityType.PUFFERFISH, 0.3);
        setCustomSoulPieceDropProbability(EntityType.BAT, 0.2);
        setCustomSoulPieceDropProbability(EntityType.ILLUSIONER, 0);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE_VILLAGER, 0);
        setCustomSoulPieceDropProbability(EntityType.COW, 0.2);
        setCustomSoulPieceDropProbability(EntityType.CHICKEN, 0.2);
        setCustomSoulPieceDropProbability(EntityType.SHEEP, 0.2);
        setCustomSoulPieceDropProbability(EntityType.PIG, 0.2);
        setCustomSoulPieceDropProbability(EntityType.HORSE, 0.2);
        setCustomSoulPieceDropProbability(EntityType.DONKEY, 0.2);
        setCustomSoulPieceDropProbability(EntityType.MULE, 0.2);
        setCustomSoulPieceDropProbability(EntityType.ZOMBIE_HORSE, 1);
        setCustomSoulPieceDropProbability(EntityType.SKELETON_HORSE, 1);
        setCustomSoulPieceDropProbability(EntityType.RAVAGER, 1.5);
        setCustomSoulPieceDropProbability(EntityType.STRIDER, 0.4);
        setCustomSoulPieceDropProbability(EntityType.FOX, 0.3);
        setCustomSoulPieceDropProbability(EntityType.WANDERING_TRADER, 1);
        setCustomSoulPieceDropProbability(EntityType.WITCH, 0.4);
        setCustomSoulPieceDropProbability(ModEntities.SOUL_SKELETON, 0.1);
        setCustomSoulPieceDropProbability(ModEntities.ENGINEER, 1.5);
        setCustomSoulPieceDropProbability(ModEntities.DARK_EVOKER, 1.5);
        setCustomSoulPieceDropProbability(ModEntities.REDSTONE_TURRET, 0);
        setCustomSoulPieceDropProbability(ModEntities.REDSTONE_MORTAR, 0);
        setCustomSoulPieceDropProbability(ModEntities.META8, 0);
        setCustomSoulPieceDropProbability(ModEntities.ROBOT, 0);
        setBaseEnergyCost(BAT, 200);
        setBaseEnergyCost(BEE, 300);
        setBaseEnergyCost(BLAZE, 3000);
        setBaseEnergyCost(CAT, 200);
        setBaseEnergyCost(CAVE_SPIDER, 800);
        setBaseEnergyCost(CHICKEN, 200);
        setBaseEnergyCost(COD, 200);
        setBaseEnergyCost(COW, 200);
        setBaseEnergyCost(CREEPER, 500);
        setBaseEnergyCost(DOLPHIN, 300);
        setBaseEnergyCost(DONKEY, 300);
        setBaseEnergyCost(DROWNED, 500);
        setBaseEnergyCost(ELDER_GUARDIAN, 5000);
        setBaseEnergyCost(ENDER_DRAGON, 10000);
        setBaseEnergyCost(ENDERMAN, 800);
        setBaseEnergyCost(EVOKER, 5000);
        setBaseEnergyCost(FOX, 300);
        setBaseEnergyCost(GHAST, 800);
        setBaseEnergyCost(GUARDIAN, 800);
        setBaseEnergyCost(HOGLIN, 800);
        setBaseEnergyCost(HORSE, 300);
        setBaseEnergyCost(HUSK, 800);
        setBaseEnergyCost(LLAMA, 300);
        setBaseEnergyCost(MAGMA_CUBE, 500);
        setBaseEnergyCost(MOOSHROOM, 300);
        setBaseEnergyCost(MULE, 300);
        setBaseEnergyCost(OCELOT, 300);
        setBaseEnergyCost(PARROT, 300);
        setBaseEnergyCost(PHANTOM, 800);
        setBaseEnergyCost(PIG, 500);
        setBaseEnergyCost(PIGLIN, 800);
        setBaseEnergyCost(PILLAGER, 800);
        setBaseEnergyCost(POLAR_BEAR, 300);
        setBaseEnergyCost(PUFFERFISH, 500);
        setBaseEnergyCost(RABBIT, 200);
        setBaseEnergyCost(RAVAGER, 2500);
        setBaseEnergyCost(SALMON, 200);
        setBaseEnergyCost(SHEEP, 200);
        setBaseEnergyCost(SHULKER, 800);
        setBaseEnergyCost(SKELETON, 500);
        setBaseEnergyCost(SKELETON_HORSE, 2000);
        setBaseEnergyCost(SILVERFISH, 300);
        setBaseEnergyCost(SLIME, 3000);
        setBaseEnergyCost(SPIDER, 500);
        setBaseEnergyCost(STRAY, 800);
        setBaseEnergyCost(STRIDER, 800);
        setBaseEnergyCost(SOUL_SKELETON, 3000);
        setBaseEnergyCost(SQUID, 200);
        setBaseEnergyCost(TRADER_LLAMA, 300);
        setBaseEnergyCost(TROPICAL_FISH, 200);
        setBaseEnergyCost(TURTLE, 3000);
        setBaseEnergyCost(VINDICATOR, 800);
        setBaseEnergyCost(WANDERER, 800);
        setBaseEnergyCost(WANDERING_TRADER, 800);
        setBaseEnergyCost(WITCH, 800);
        setBaseEnergyCost(WITHER, 20000);
        setBaseEnergyCost(WITHER_SKELETON, 800);
        setBaseEnergyCost(WOLF, 300);
        setBaseEnergyCost(ZOGLIN, 800);
        setBaseEnergyCost(ZOMBIE, 500);
        setBaseEnergyCost(ZOMBIE_HORSE, 2000);
        setBaseEnergyCost(ZOMBIFIED_PIGLIN, 800);
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
