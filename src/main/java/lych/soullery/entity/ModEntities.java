package lych.soullery.entity;

import lych.soullery.Soullery;
import lych.soullery.entity.functional.*;
import lych.soullery.entity.monster.*;
import lych.soullery.entity.monster.boss.*;
import lych.soullery.entity.monster.boss.esv.SoulControllerEntity;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.entity.monster.raider.DarkEvokerEntity;
import lych.soullery.entity.monster.raider.EngineerEntity;
import lych.soullery.entity.monster.raider.RedstoneMortarEntity;
import lych.soullery.entity.monster.raider.RedstoneTurretEntity;
import lych.soullery.entity.monster.voidwalker.*;
import lych.soullery.entity.passive.IllusoryHorseEntity;
import lych.soullery.entity.passive.SoulRabbitEntity;
import lych.soullery.entity.projectile.*;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;
import static net.minecraft.entity.EntitySpawnPlacementRegistry.register;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntities {
    public static final EntityType<SkeletonKingEntity.Cloned> CLONED_SKELETON_KING = Builder.of(SkeletonKingEntity.Cloned::new, EntityClassification.MONSTER).sized(0.7f, 2.4f).noSummon().clientTrackingRange(8).build(ModEntityNames.CLONED_SKELETON_KING);
    public static final EntityType<ComputerScientistEntity> COMPUTER_SCIENTIST = Builder.of(ComputerScientistEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.COMPUTER_SCIENTIST);
    public static final EntityType<DarkEvokerEntity> DARK_EVOKER = Builder.of(DarkEvokerEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8).build(ModEntityNames.DARK_EVOKER);
    public static final EntityType<DroppingMortarShellEntity> DROPPING_MORTAR_SHELL = Builder.<DroppingMortarShellEntity>of(DroppingMortarShellEntity::new, EntityClassification.MISC).sized(1.2f, 1.2f).clientTrackingRange(12).updateInterval(10).build(ModEntityNames.DROPPING_MORTAR_SHELL);
    public static final EntityType<EnergizedBlazeEntity> ENERGIZED_BLAZE = Builder.of(EnergizedBlazeEntity::new, EntityClassification.MONSTER).fireImmune().sized(0.6f, 1.8f).clientTrackingRange(10).build(ModEntityNames.ENERGIZED_BLAZE);
    public static final EntityType<EngineerEntity> ENGINEER = Builder.of(EngineerEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.ENGINEER);
    public static final EntityType<EtheArmorerEntity> ETHE_ARMORER = Builder.of(EtheArmorerEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.ETHE_ARMORER);
    public static final EntityType<EtherealArrowEntity> ETHEREAL_ARROW = Builder.<EtherealArrowEntity>of(EtherealArrowEntity::new, EntityClassification.MISC).sized(0.5f, 0.5f).clientTrackingRange(8).updateInterval(10).build(ModEntityNames.ETHEREAL_ARROW);
    public static final EntityType<FangsEntity> FANGS = Builder.<FangsEntity>of(FangsEntity::new, EntityClassification.MISC).sized(0.5f, 0.8f).clientTrackingRange(6).updateInterval(2).build(ModEntityNames.FANGS);
    public static final EntityType<FangsSummonerEntity> FANGS_SUMMONER = Builder.<FangsSummonerEntity>of(FangsSummonerEntity::new, EntityClassification.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build(ModEntityNames.FANGS_SUMMONER);
    public static final EntityType<FortifiedSoulCrystalEntity> FORTIFIED_SOUL_CRYSTAL = Builder.<FortifiedSoulCrystalEntity>of(FortifiedSoulCrystalEntity::new, EntityClassification.MISC).sized(2, 2).fireImmune().clientTrackingRange(24).updateInterval(Integer.MAX_VALUE).build(ModEntityNames.FORTIFIED_SOUL_CRYSTAL);
    public static final EntityType<GiantXEntity> GIANT_X = Builder.of(GiantXEntity::new, EntityClassification.MONSTER).sized(3.6f, 12).clientTrackingRange(10).build(ModEntityNames.GIANT_X);
    public static final EntityType<GravitationalDragonFireballEntity> GRAVITATIONAL_DRAGON_FIREBALL = Builder.<GravitationalDragonFireballEntity>of(GravitationalDragonFireballEntity::new, EntityClassification.MISC).sized(0.8f, 0.8f).clientTrackingRange(4).updateInterval(10).build(ModEntityNames.GRAVITATIONAL_DRAGON_FIREBALL);
    public static final EntityType<HorcruxEntity> HORCRUX = Builder.of(HorcruxEntity::new, EntityClassification.CREATURE).sized(0.6f, 1.95f).clientTrackingRange(12).updateInterval(8).build(ModEntityNames.HORCRUX);
    public static final EntityType<IllusoryHorseEntity> ILLUSORY_HORSE = Builder.of(IllusoryHorseEntity::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10).build(ModEntityNames.ILLUSORY_HORSE);
    public static final EntityType<Meta08Entity> META8 = Builder.of(Meta08Entity::new, EntityClassification.MONSTER).sized(1.2f, 3.9f).fireImmune().clientTrackingRange(12).build(ModEntityNames.META8);
    public static final EntityType<PursuerEntity> PURSUER = Builder.<PursuerEntity>of(PursuerEntity::new, EntityClassification.MISC).sized(0.3125f, 0.3125f).clientTrackingRange(24).build(ModEntityNames.PURSUER);
    public static final EntityType<RedstoneBombEntity> REDSTONE_BOMB = Builder.<RedstoneBombEntity>of(RedstoneBombEntity::new, EntityClassification.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(8).build(ModEntityNames.REDSTONE_BOMB);
    public static final EntityType<RedstoneMortarEntity> REDSTONE_MORTAR = Builder.of(RedstoneMortarEntity::new, EntityClassification.MONSTER).sized(1, 3).clientTrackingRange(16).build(ModEntityNames.REDSTONE_MORTAR);
    public static final EntityType<RedstoneTurretEntity> REDSTONE_TURRET = Builder.of(RedstoneTurretEntity::new, EntityClassification.MONSTER).sized(1, 3).clientTrackingRange(8).build(ModEntityNames.REDSTONE_TURRET);
    public static final EntityType<RisingMortarShellEntity> RISING_MORTAR_SHELL = Builder.<RisingMortarShellEntity>of(RisingMortarShellEntity::new, EntityClassification.MISC).sized(1.2f, 1.2f).clientTrackingRange(12).updateInterval(10).build(ModEntityNames.RISING_MORTAR_SHELL);
    public static final EntityType<RobotEntity> ROBOT = Builder.of(RobotEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8).build(ModEntityNames.ROBOT);
    public static final EntityType<SkeletonFollowerEntity> SKELETON_FOLLOWER = Builder.of(SkeletonFollowerEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.99f).clientTrackingRange(10).build(ModEntityNames.SKELETON_FOLLOWER);
    public static final EntityType<SkeletonKingEntity> SKELETON_KING = Builder.of(SkeletonKingEntity::new, EntityClassification.MONSTER).sized(0.7f, 2.4f).clientTrackingRange(8).build(ModEntityNames.SKELETON_KING);
    public static final EntityType<SoulballEntity> SOULBALL = Builder.<SoulballEntity>of(SoulballEntity::new, EntityClassification.MISC).sized(1, 1).clientTrackingRange(4).updateInterval(10).build(ModEntityNames.SOULBALL);
    public static final EntityType<SoulArrowEntity> SOUL_ARROW = Builder.<SoulArrowEntity>of(SoulArrowEntity::new, EntityClassification.MISC).sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build(ModEntityNames.SOUL_ARROW);
    public static final EntityType<SoulBoltEntity> SOUL_BOLT = Builder.of(SoulBoltEntity::new, EntityClassification.MISC).sized(0.0F, 0.0F).clientTrackingRange(24).updateInterval(Integer.MAX_VALUE).build(ModEntityNames.SOUL_BOLT);
    public static final EntityType<SoulControllerEntity> SOUL_CONTROLLER = Builder.of(SoulControllerEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(24).build(ModEntityNames.SOUL_CONTROLLER);
    public static final EntityType<SoulCrystalEntity> SOUL_CRYSTAL = Builder.<SoulCrystalEntity>of(SoulCrystalEntity::new, EntityClassification.MISC).sized(2, 2).fireImmune().clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build(ModEntityNames.SOUL_CRYSTAL);
    public static final EntityType<SoulDragonEntity> SOUL_DRAGON = Builder.of(SoulDragonEntity::new, EntityClassification.MONSTER).fireImmune().sized(16.0F, 8.0F).clientTrackingRange(10).build(ModEntityNames.SOUL_DRAGON);
    public static final EntityType<SoulRabbitEntity> SOUL_RABBIT = Builder.of(SoulRabbitEntity::new, EntityClassification.CREATURE).sized(0.4F, 0.5F).fireImmune().clientTrackingRange(8).build(ModEntityNames.SOUL_RABBIT);
    public static final EntityType<SoulSkeletonEntity> SOUL_SKELETON = Builder.of(SoulSkeletonEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.99f).fireImmune().clientTrackingRange(8).build(ModEntityNames.SOUL_SKELETON);
    public static final EntityType<SoulSkeletonKingEntity> SOUL_SKELETON_KING = Builder.of(SoulSkeletonKingEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.99f).fireImmune().clientTrackingRange(10).build(ModEntityNames.SOUL_SKELETON_KING);
    public static final EntityType<SoulifiedEnderPearlEntity> SOULIFIED_ENDER_PEARL = Builder.<SoulifiedEnderPearlEntity>of(SoulifiedEnderPearlEntity::new, EntityClassification.MISC).sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10).build(ModEntityNames.SOULIFIED_ENDER_PEARL);
    public static final EntityType<SubZombieEntity> SUB_ZOMBIE = Builder.of(SubZombieEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(8).build(ModEntityNames.SUB_ZOMBIE);
    public static final EntityType<VoidAlchemistEntity> VOID_ALCHEMIST = Builder.of(VoidAlchemistEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.VOID_ALCHEMIST);
    public static final EntityType<VoidArcherEntity> VOID_ARCHER = Builder.of(VoidArcherEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.VOID_ARCHER);
    public static final EntityType<VoidDefenderEntity> VOID_DEFENDER = Builder.of(VoidDefenderEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.VOID_DEFENDER);
    public static final EntityType<VoidwalkerEntity> VOIDWALKER = Builder.of(VoidwalkerEntity::new, EntityClassification.MONSTER).sized(0.6f, 1.95f).clientTrackingRange(10).build(ModEntityNames.VOIDWALKER);
    public static final EntityType<WandererEntity> WANDERER = Builder.of(WandererEntity::new, EntityClassification.MONSTER).sized(0.75f, 2.34f).clientTrackingRange(10).build(ModEntityNames.WANDERER);

    private ModEntities() {}

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        registry.register(make(CLONED_SKELETON_KING, ModEntityNames.CLONED_SKELETON_KING));
        registry.register(make(COMPUTER_SCIENTIST, ModEntityNames.COMPUTER_SCIENTIST));
        registry.register(make(DARK_EVOKER, ModEntityNames.DARK_EVOKER));
        registry.register(make(DROPPING_MORTAR_SHELL, ModEntityNames.DROPPING_MORTAR_SHELL));
        registry.register(make(ENERGIZED_BLAZE, ModEntityNames.ENERGIZED_BLAZE));
        registry.register(make(ENGINEER, ModEntityNames.ENGINEER));
        registry.register(make(ETHE_ARMORER, ModEntityNames.ETHE_ARMORER));
        registry.register(make(ETHEREAL_ARROW, ModEntityNames.ETHEREAL_ARROW));
        registry.register(make(FANGS, ModEntityNames.FANGS));
        registry.register(make(FANGS_SUMMONER, ModEntityNames.FANGS_SUMMONER));
        registry.register(make(FORTIFIED_SOUL_CRYSTAL, ModEntityNames.FORTIFIED_SOUL_CRYSTAL));
        registry.register(make(GIANT_X, ModEntityNames.GIANT_X));
        registry.register(make(GRAVITATIONAL_DRAGON_FIREBALL, ModEntityNames.GRAVITATIONAL_DRAGON_FIREBALL));
        registry.register(make(HORCRUX, ModEntityNames.HORCRUX));
        registry.register(make(ILLUSORY_HORSE, ModEntityNames.ILLUSORY_HORSE));
        registry.register(make(META8, ModEntityNames.META8));
        registry.register(make(PURSUER, ModEntityNames.PURSUER));
        registry.register(make(REDSTONE_BOMB, ModEntityNames.REDSTONE_BOMB));
        registry.register(make(REDSTONE_MORTAR, ModEntityNames.REDSTONE_MORTAR));
        registry.register(make(REDSTONE_TURRET, ModEntityNames.REDSTONE_TURRET));
        registry.register(make(RISING_MORTAR_SHELL, ModEntityNames.RISING_MORTAR_SHELL));
        registry.register(make(ROBOT, ModEntityNames.ROBOT));
        registry.register(make(SKELETON_FOLLOWER, ModEntityNames.SKELETON_FOLLOWER));
        registry.register(make(SKELETON_KING, ModEntityNames.SKELETON_KING));
        registry.register(make(SOULBALL, ModEntityNames.SOULBALL));
        registry.register(make(SOUL_ARROW, ModEntityNames.SOUL_ARROW));
        registry.register(make(SOUL_BOLT, ModEntityNames.SOUL_BOLT));
        registry.register(make(SOUL_CONTROLLER, ModEntityNames.SOUL_CONTROLLER));
        registry.register(make(SOUL_CRYSTAL, ModEntityNames.SOUL_CRYSTAL));
        registry.register(make(SOUL_DRAGON, ModEntityNames.SOUL_DRAGON));
        registry.register(make(SOUL_RABBIT, ModEntityNames.SOUL_RABBIT));
        registry.register(make(SOUL_SKELETON, ModEntityNames.SOUL_SKELETON));
        registry.register(make(SOUL_SKELETON_KING, ModEntityNames.SOUL_SKELETON_KING));
        registry.register(make(SOULIFIED_ENDER_PEARL, ModEntityNames.SOULIFIED_ENDER_PEARL));
        registry.register(make(SUB_ZOMBIE, ModEntityNames.SUB_ZOMBIE));
        registry.register(make(VOID_ALCHEMIST, ModEntityNames.VOID_ALCHEMIST));
        registry.register(make(VOID_ARCHER, ModEntityNames.VOID_ARCHER));
        registry.register(make(VOID_DEFENDER, ModEntityNames.VOID_DEFENDER));
        registry.register(make(VOIDWALKER, ModEntityNames.VOIDWALKER));
        registry.register(make(WANDERER, ModEntityNames.WANDERER));
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(CLONED_SKELETON_KING, SkeletonKingEntity.createAttributes().build());
        event.put(COMPUTER_SCIENTIST, ComputerScientistEntity.createAttributes().build());
        event.put(DARK_EVOKER, EvokerEntity.createAttributes().build());
        event.put(ENERGIZED_BLAZE, EnergizedBlazeEntity.createAttributes().build());
        event.put(ENGINEER, EngineerEntity.createAttributes().build());
        event.put(ETHE_ARMORER, AbstractVoidwalkerEntity.createVoidwalkerAttributes().build());
        event.put(GIANT_X, GiantXEntity.createAttributes().build());
        event.put(HORCRUX, HorcruxEntity.createAttributes().build());
        event.put(ILLUSORY_HORSE, IllusoryHorseEntity.createAttributes().build());
        event.put(META8, Meta08Entity.createAttributes().build());
        event.put(REDSTONE_MORTAR, RedstoneMortarEntity.createAttributes().build());
        event.put(REDSTONE_TURRET, RedstoneTurretEntity.createAttributes().build());
        event.put(ROBOT, RobotEntity.createAttributes().build());
        event.put(SKELETON_FOLLOWER, SkeletonFollowerEntity.createAttributes().build());
        event.put(SKELETON_KING, SkeletonKingEntity.createAttributes().build());
        event.put(SOUL_CONTROLLER, SoulControllerEntity.createAttributes().build());
        event.put(SOUL_DRAGON, SoulDragonEntity.createAttributes().build());
        event.put(SOUL_RABBIT, RabbitEntity.createAttributes().build());
        event.put(SOUL_SKELETON, SoulSkeletonEntity.createAttributes().build());
        event.put(SOUL_SKELETON_KING, SoulSkeletonKingEntity.createAttributes().build());
        event.put(SUB_ZOMBIE, ZombieEntity.createAttributes().build());
        event.put(VOID_ALCHEMIST, VoidAlchemistEntity.createAttributes().build());
        event.put(VOID_ARCHER, AbstractVoidwalkerEntity.createVoidwalkerAttributes().build());
        event.put(VOID_DEFENDER, VoidDefenderEntity.createAttributes().build());
        event.put(VOIDWALKER, VoidwalkerEntity.createAttributes().build());
        event.put(WANDERER, WandererEntity.createAttributes().build());
    }

    public static void registerEntitySpawnPlacements() {
        register(COMPUTER_SCIENTIST, PlacementType.NO_RESTRICTIONS, Type.MOTION_BLOCKING, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(DARK_EVOKER, PlacementType.NO_RESTRICTIONS, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(ENERGIZED_BLAZE, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(ENGINEER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(ETHE_ARMORER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(ILLUSORY_HORSE, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, AnimalEntity::checkAnimalSpawnRules);
        register(REDSTONE_MORTAR, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(REDSTONE_TURRET, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(ROBOT, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(SKELETON_FOLLOWER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(SOUL_CONTROLLER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(SOUL_RABBIT, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, SoulRabbitEntity::checkSoulRabbitSpawnRules);
        register(SOUL_SKELETON, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(SOUL_SKELETON_KING, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(SUB_ZOMBIE, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
        register(VOID_ALCHEMIST, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(VOID_ARCHER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(VOID_DEFENDER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(VOIDWALKER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
        register(WANDERER, PlacementType.ON_GROUND, Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkMonsterSpawnRules);
    }
}
