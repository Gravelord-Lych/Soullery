package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlockNames;
import lych.soullery.block.ModBlocks;
import lych.soullery.block.entity.SEStorageTileEntity;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ModEntityNames;
import lych.soullery.entity.projectile.SoulArrowEntity;
import lych.soullery.fluid.ModFluids;
import lych.soullery.item.ModMaterials.Armor;
import lych.soullery.item.ModMaterials.Tool;
import lych.soullery.item.potion.HalfUsedLingeringPotionItem;
import lych.soullery.item.potion.HalfUsedPotionItem;
import lych.soullery.item.potion.HalfUsedSplashPotionItem;
import lych.soullery.util.ModConstants;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

import static lych.soullery.Soullery.make;
import static lych.soullery.block.entity.AbstractSEGeneratorTileEntity.getCapacity;
import static lych.soullery.util.ModConstants.VOIDWALKER_SPAWN_EGG_BACKGROUND_COLOR;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModItems {
    public static final String SPAWN_EGG_SUFFIX = "_spawn_egg";
    public static final Item CHAOS_WAND = new ChaosWandItem(common().stacksTo(1), 1);
    public static final Item CHAOS_WAND_II = new ChaosWandItem(common().stacksTo(1).fireResistant().rarity(Rarity.RARE), 2);
    public static final Item ENTITY_CARRIER = new EntityCarrierItem(common().stacksTo(1), 1);
    public static final Item ENTITY_CARRIER_II = new EntityCarrierItem(common().stacksTo(1).fireResistant(), 8);
    public static final Item EXTRA_ABILITY_CARRIER = new ExtraAbilityCarrierItem(common().stacksTo(1));
    public static final Item EXTRA_ABILITY_WAND = new ExtraAbilityWandItem(common().stacksTo(1), 2);
    public static final Item HALF_USED_LINGERING_POTION = new HalfUsedLingeringPotionItem(common().stacksTo(1).tab(ItemGroup.TAB_BREWING));
    public static final Item HALF_USED_POTION = new HalfUsedPotionItem(common().stacksTo(1).tab(ItemGroup.TAB_BREWING));
    public static final Item HALF_USED_SPLASH_POTION = new HalfUsedSplashPotionItem(common().stacksTo(1).tab(ItemGroup.TAB_BREWING));
    public static final Item HORCRUX_CARRIER = new HorcruxCarrierItem(common().stacksTo(1));
    public static final Item MIND_OPERATOR = new MindOperatorItem(common().stacksTo(1), 1);
    public static final Item MIND_OPERATOR_II = new MindOperatorItem(common().stacksTo(1).fireResistant().rarity(Rarity.RARE), 2);
    public static final Item MIND_OPERATOR_III = new MindOperatorItem(common().stacksTo(1).fireResistant().rarity(Rarity.EPIC), 3);
    public static final Item REFINED_SOUL_METAL_AXE = new AxeItem(Tool.REFINED_SOUL_METAL, 5, -3, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_BOOTS = new ArmorItem(Armor.REFINED_SOUL_METAL, EquipmentSlotType.FEET, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_CHESTPLATE = new ArmorItem(Armor.REFINED_SOUL_METAL, EquipmentSlotType.CHEST, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_HELMET = new ArmorItem(Armor.REFINED_SOUL_METAL, EquipmentSlotType.HEAD, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_HOE = new HoeItem(Tool.REFINED_SOUL_METAL, -4, 0, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_HORSE_ARMOR = new HorseArmorItem(14, new ResourceLocation(String.format("textures/models/armor/horse_armor_%s.png", Armor.REFINED_SOUL_METAL.getName())), common().fireResistant());
    public static final Item REFINED_SOUL_METAL_INGOT = new Item(common().fireResistant());
    public static final Item REFINED_SOUL_METAL_LEGGINGS = new ArmorItem(Armor.REFINED_SOUL_METAL, EquipmentSlotType.LEGS, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_NUGGET = new Item(common().fireResistant());
    public static final Item REFINED_SOUL_METAL_PICKAXE = new PickaxeItem(Tool.REFINED_SOUL_METAL, 1, -2.8f, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_SHOVEL = new ShovelItem(Tool.REFINED_SOUL_METAL, 1.5f, -3, common().fireResistant());
    public static final Item REFINED_SOUL_METAL_SWORD = new SwordItem(Tool.REFINED_SOUL_METAL, 3, -2.4f, common().fireResistant());
    public static final ArrowItem SOUL_ARROW = new SimpleArrowItem(SoulArrowEntity::new, common());
    public static final Item SOUL_BLAZE_POWDER = new Item(common());
    public static final Item SOUL_BLAZE_ROD = new Item(common());
    public static final SoulBowItem SOUL_BOW = new SoulBowItem(common());
    public static final Item SOUL_CONTAINER = new SoulContainerItem(common().stacksTo(16));
    public static final SEGemItem SOUL_ENERGY_GEM = new SEGemItem(common(), SoulEnergies.DEFAULT_CAPACITY, false);
    public static final SEGemItem SOUL_ENERGY_GEM_II = new SEGemItem(common().fireResistant().rarity(Rarity.RARE), SoulEnergies.DEFAULT_CAPACITY * 4, true);
    public static final Item SOUL_LAVA_BUCKET = new BucketItem(() -> ModFluids.SOUL_LAVA, common().craftRemainder(Items.BUCKET));
    public static final Item SOUL_METAL_INGOT = new Item(common());
    public static final Item SOUL_METAL_NUGGET = new Item(common());
    public static final Item SOUL_METAL_PARTICLE = new Item(common());
    public static final Item SOUL_PIECE = new SoulPieceItem(common().stacksTo(16));
    public static final Item SOUL_POWDER = new SoulPowderItem(common());
    public static final Item SOUL_PURIFIER = new SoulPurifierItem(common().stacksTo(1), 1);
    public static final Item SOUL_PURIFIER_II = new SoulPurifierItem(common().stacksTo(1).fireResistant().rarity(Rarity.RARE), 2);

    public static final BlockItem BROKEN_REFINED_SOUL_METAL_BARS = new BlockItem(ModBlocks.BROKEN_REFINED_SOUL_METAL_BARS, common());
    public static final BlockItem CHIPPED_REFINED_SOUL_METAL_BARS = new BlockItem(ModBlocks.CHIPPED_REFINED_SOUL_METAL_BARS, common());
    public static final BlockItem CHIPPED_SOUL_METAL_BARS = new BlockItem(ModBlocks.CHIPPED_SOUL_METAL_BARS, common());
    public static final BlockItem CHISELED_SOUL_STONE_BRICKS = new BlockItem(ModBlocks.CHISELED_SOUL_STONE_BRICKS, common());
    public static final BlockItem CRACKED_DECAYED_STONE_BRICK_SLAB = new BlockItem(ModBlocks.CRACKED_DECAYED_STONE_BRICK_SLAB, common());
    public static final BlockItem CRACKED_DECAYED_STONE_BRICK_STAIRS = new BlockItem(ModBlocks.CRACKED_DECAYED_STONE_BRICK_STAIRS, common());
    public static final BlockItem CRACKED_DECAYED_STONE_BRICK_WALL = new BlockItem(ModBlocks.CRACKED_DECAYED_STONE_BRICK_WALL, common());
    public static final BlockItem CRACKED_DECAYED_STONE_BRICKS = new BlockItem(ModBlocks.CRACKED_DECAYED_STONE_BRICKS, common());
    public static final BlockItem CRACKED_SOUL_STONE_BRICK_SLAB = new BlockItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_SLAB, common());
    public static final BlockItem CRACKED_SOUL_STONE_BRICK_STAIRS = new BlockItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_STAIRS, common());
    public static final BlockItem CRACKED_SOUL_STONE_BRICK_WALL = new BlockItem(ModBlocks.CRACKED_SOUL_STONE_BRICK_WALL, common());
    public static final BlockItem CRACKED_SOUL_STONE_BRICKS = new BlockItem(ModBlocks.CRACKED_SOUL_STONE_BRICKS, common());
    public static final BlockItem CRIMSON_HYPHAL_SOIL = new BlockItem(ModBlocks.CRIMSON_HYPHAL_SOIL, common());
    public static final BlockItem DAMAGED_REFINED_SOUL_METAL_BARS = new BlockItem(ModBlocks.DAMAGED_REFINED_SOUL_METAL_BARS, common());
    public static final BlockItem DAMAGED_SOUL_METAL_BARS = new BlockItem(ModBlocks.DAMAGED_SOUL_METAL_BARS, common());
    public static final BlockItem DECAYED_STONE = new BlockItem(ModBlocks.DECAYED_STONE, common());
    public static final BlockItem DECAYED_STONE_BRICK_SLAB = new BlockItem(ModBlocks.DECAYED_STONE_BRICK_SLAB, common());
    public static final BlockItem DECAYED_STONE_BRICK_STAIRS = new BlockItem(ModBlocks.DECAYED_STONE_BRICK_STAIRS, common());
    public static final BlockItem DECAYED_STONE_BRICK_WALL = new BlockItem(ModBlocks.DECAYED_STONE_BRICK_WALL, common());
    public static final BlockItem DECAYED_STONE_BRICKS = new BlockItem(ModBlocks.DECAYED_STONE_BRICKS, common());
    public static final BlockItem DECAYED_STONE_SLAB = new BlockItem(ModBlocks.DECAYED_STONE_SLAB, common());
    public static final BlockItem DECAYED_STONE_STAIRS = new BlockItem(ModBlocks.DECAYED_STONE_STAIRS, common());
    public static final BlockItem DECAYED_STONE_WALL = new BlockItem(ModBlocks.DECAYED_STONE_WALL, common());
    public static final SEGeneratorBlockItem DEPTH_SEGEN = new SEGeneratorBlockItem(ModBlocks.DEPTH_SEGEN, getCapacity(1), se());
    public static final SEGeneratorBlockItem DEPTH_SEGEN_II = new SEGeneratorBlockItem(ModBlocks.DEPTH_SEGEN_II, getCapacity(2), se());
    public static final SEGeneratorBlockItem HEAT_SEGEN = new SEGeneratorBlockItem(ModBlocks.HEAT_SEGEN, getCapacity(1), se());
    public static final SEGeneratorBlockItem HEAT_SEGEN_II = new SEGeneratorBlockItem(ModBlocks.HEAT_SEGEN_II, getCapacity(2), se());
    public static final BlockItem MAGNETIC_FIELD_GENERATOR = new BlockItem(ModBlocks.MAGNETIC_FIELD_GENERATOR, common().rarity(Rarity.EPIC));
    public static final SEGeneratorBlockItem NETHER_SEGEN = new SEGeneratorBlockItem(ModBlocks.NETHER_SEGEN, getCapacity(1), se());
    public static final SEGeneratorBlockItem NETHER_SEGEN_II = new SEGeneratorBlockItem(ModBlocks.NETHER_SEGEN_II, getCapacity(2), se());
    public static final BlockItem PARCHED_SOIL = new BlockItem(ModBlocks.PARCHED_SOIL, common());
    public static final BlockItem PURIFIED_SOULIFIED_BEDROCK = new BlockItem(ModBlocks.PURIFIED_SOULIFIED_BEDROCK, common());
    public static final BlockItem REFINED_SOUL_METAL_BARS = new BlockItem(ModBlocks.REFINED_SOUL_METAL_BARS, common());
    public static final BlockItem REFINED_SOUL_METAL_BLOCK = new BlockItem(ModBlocks.REFINED_SOUL_METAL_BLOCK, common().fireResistant());
    public static final BlockItem REFINED_SOUL_SAND = new BlockItem(ModBlocks.REFINED_SOUL_SAND, common());
    public static final BlockItem REFINED_SOUL_SOIL = new BlockItem(ModBlocks.REFINED_SOUL_SOIL, common());
    public static final SEGeneratorBlockItem SEGEN = new SEGeneratorBlockItem(ModBlocks.SEGEN, getCapacity(1), se());
    public static final SEGeneratorBlockItem SEGEN_II = new SEGeneratorBlockItem(ModBlocks.SEGEN_II, getCapacity(2), se());
    public static final SEGeneratorBlockItem SKY_SEGEN = new SEGeneratorBlockItem(ModBlocks.SKY_SEGEN, getCapacity(1), se());
    public static final SEGeneratorBlockItem SKY_SEGEN_II = new SEGeneratorBlockItem(ModBlocks.SKY_SEGEN_II, getCapacity(2), se());
    public static final BlockItem SMOOTH_SOUL_STONE = new BlockItem(ModBlocks.SMOOTH_SOUL_STONE, common());
    public static final BlockItem SMOOTH_SOUL_STONE_SLAB = new BlockItem(ModBlocks.SMOOTH_SOUL_STONE_SLAB, common());
    public static final BlockItem SMOOTH_SOUL_STONE_STAIRS = new BlockItem(ModBlocks.SMOOTH_SOUL_STONE_STAIRS, common());
    public static final BlockItem SMOOTH_SOUL_STONE_WALL = new BlockItem(ModBlocks.SMOOTH_SOUL_STONE_WALL, common());
    public static final SEGeneratorBlockItem SOLAR_SEGEN = new SEGeneratorBlockItem(ModBlocks.SOLAR_SEGEN, getCapacity(1), se());
    public static final SEGeneratorBlockItem SOLAR_SEGEN_II = new SEGeneratorBlockItem(ModBlocks.SOLAR_SEGEN_II, getCapacity(2), se());
    public static final SEStorageBlockItem SOUL_ENERGY_STORAGE = new SEStorageBlockItem(ModBlocks.SOUL_ENERGY_STORAGE, SEStorageTileEntity.CAPACITY, se());
    public static final SEStorageBlockItem SOUL_ENERGY_STORAGE_II = new SEStorageBlockItem(ModBlocks.SOUL_ENERGY_STORAGE_II, SEStorageTileEntity.CAPACITY_II, se());
    public static final BlockItem SOUL_METAL_BARS = new BlockItem(ModBlocks.SOUL_METAL_BARS, common());
    public static final BlockItem SOUL_METAL_BLOCK = new BlockItem(ModBlocks.SOUL_METAL_BLOCK, common());
    public static final BlockItem SOUL_OBSIDIAN = new BlockItem(ModBlocks.SOUL_OBSIDIAN, common());
    public static final BlockItem SOUL_REINFORCEMENT_TABLE = new BlockItem(ModBlocks.SOUL_REINFORCEMENT_TABLE, common());
    public static final BlockItem SOUL_STONE = new BlockItem(ModBlocks.SOUL_STONE, common());
    public static final BlockItem SOUL_STONE_BRICK_SLAB = new BlockItem(ModBlocks.SOUL_STONE_BRICK_SLAB, common());
    public static final BlockItem SOUL_STONE_BRICK_STAIRS = new BlockItem(ModBlocks.SOUL_STONE_BRICK_STAIRS, common());
    public static final BlockItem SOUL_STONE_BRICK_WALL = new BlockItem(ModBlocks.SOUL_STONE_BRICK_WALL, common());
    public static final BlockItem SOUL_STONE_BRICKS = new BlockItem(ModBlocks.SOUL_STONE_BRICKS, common());
    public static final BlockItem SOUL_STONE_SLAB = new BlockItem(ModBlocks.SOUL_STONE_SLAB, common());
    public static final BlockItem SOUL_STONE_STAIRS = new BlockItem(ModBlocks.SOUL_STONE_STAIRS, common());
    public static final BlockItem SOUL_STONE_WALL = new BlockItem(ModBlocks.SOUL_STONE_WALL, common());
    public static final BlockNamedItem SOUL_WART = new BlockNamedItem(ModBlocks.SOUL_WART, common());
    public static final BlockItem SOULIFIED_BEDROCK = new BlockItem(ModBlocks.SOULIFIED_BEDROCK, common());
    public static final BlockItem SOULIFIED_BUSH = new BlockItem(ModBlocks.SOULIFIED_BUSH, common());
    public static final BlockItem WARPED_HYPHAL_SOIL = new BlockItem(ModBlocks.WARPED_HYPHAL_SOIL, common());

    public static final Item COMPUTER_SCIENTIST_SPAWN_EGG = makeVoidwalkerSpawnEgg(ModEntities.COMPUTER_SCIENTIST, 0x159415);
    public static final Item DARK_EVOKER_SPAWN_EGG = makeSpawnEgg(ModEntities.DARK_EVOKER, 0x959b9b, 0xd62fd6);
    public static final Item ENGINEER_SPAWN_EGG = makeSpawnEgg(ModEntities.ENGINEER, 0x959b9b, 0xff0000);
    public static final Item ILLUSORY_HORSE_SPAWN_EGG = makeSpawnEgg(ModEntities.ILLUSORY_HORSE, VOIDWALKER_SPAWN_EGG_BACKGROUND_COLOR, 0x49dfea);
    public static final Item REDSTONE_MORTAR_SPAWN_EGG = makeSpawnEgg(ModEntities.REDSTONE_MORTAR, 0x452d15, 0x474747);
    public static final Item REDSTONE_TURRET_SPAWN_EGG = makeSpawnEgg(ModEntities.REDSTONE_TURRET, 0x452d15, 0x7e7e7e);
    public static final Item SOUL_RABBIT_SPAWN_EGG = makeSpawnEgg(ModEntities.SOUL_RABBIT, 0x277df6, 0x12dada);
    public static final Item SOUL_SKELETON_SPAWN_EGG = makeSpawnEgg(ModEntities.SOUL_SKELETON, 0x79eef2, 0x00797d);
    public static final Item VOID_ALCHEMIST_SPAWN_EGG = makeVoidwalkerSpawnEgg(ModEntities.VOID_ALCHEMIST, 0xe8d1ff);
    public static final Item VOID_ARCHER_SPAWN_EGG = makeVoidwalkerSpawnEgg(ModEntities.VOID_ARCHER, 0x906248);
    public static final Item ETHE_ARMORER_SPAWN_EGG = makeVoidwalkerSpawnEgg(ModEntities.ETHE_ARMORER, 0x797979);
    public static final Item VOID_DEFENDER_SPAWN_EGG = makeVoidwalkerSpawnEgg(ModEntities.VOID_DEFENDER, 0x7881e5);
    public static final Item VOIDWALKER_SPAWN_EGG = makeVoidwalkerSpawnEgg(ModEntities.VOIDWALKER, 0x346b94);
    public static final Item WANDERER_SPAWN_EGG = makeSpawnEgg(ModEntities.WANDERER, 0x022330, 0x53feff);

    private ModItems() {}

    private static ForgeSpawnEggItem makeVoidwalkerSpawnEgg(EntityType<?> type, int highlightColor) {
        return makeVoidwalkerSpawnEgg(() -> type, highlightColor);
    }

    private static ForgeSpawnEggItem makeSpawnEgg(EntityType<?> type, int backgroundColor, int highlightColor) {
        return makeSpawnEgg(() -> type, backgroundColor, highlightColor);
    }

    private static ForgeSpawnEggItem makeVoidwalkerSpawnEgg(Supplier<EntityType<?>> typeSupplier, int highlightColor) {
        return new VoidwalkerSpawnEggItem(typeSupplier, ModConstants.VOIDWALKER_SPAWN_EGG_BACKGROUND_COLOR, highlightColor, spawnEgg());
    }

    private static ForgeSpawnEggItem makeSpawnEgg(Supplier<EntityType<?>> typeSupplier, int backgroundColor, int highlightColor) {
        return new ForgeSpawnEggItem(typeSupplier, backgroundColor, highlightColor, spawnEgg());
    }

    private static Item.Properties common() {
        return new Item.Properties().tab(ModItemGroups.DEFAULT);
    }

    private static Item.Properties se() {
        return new Item.Properties().tab(ModItemGroups.MACHINE);
    }

    private static Item.Properties spawnEgg() {
        return common();
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(make(CHAOS_WAND, ModItemNames.CHAOS_WAND));
        registry.register(make(CHAOS_WAND_II, ModItemNames.CHAOS_WAND_II));
        registry.register(make(ENTITY_CARRIER, ModItemNames.ENTITY_CARRIER));
        registry.register(make(ENTITY_CARRIER_II, ModItemNames.ENTITY_CARRIER_II));
        registry.register(make(EXTRA_ABILITY_CARRIER, ModItemNames.EXTRA_ABILITY_CARRIER));
        registry.register(make(EXTRA_ABILITY_WAND, ModItemNames.EXTRA_ABILITY_WAND));
        registry.register(make(HALF_USED_LINGERING_POTION, ModItemNames.HALF_USED_LINGERING_POTION));
        registry.register(make(HALF_USED_POTION, ModItemNames.HALF_USED_POTION));
        registry.register(make(HALF_USED_SPLASH_POTION, ModItemNames.HALF_USED_SPLASH_POTION));
        registry.register(make(HORCRUX_CARRIER, ModItemNames.HORCRUX_CARRIER));
        registry.register(make(MIND_OPERATOR, ModItemNames.MIND_OPERATOR));
        registry.register(make(MIND_OPERATOR_II, ModItemNames.MIND_OPERATOR_II));
        registry.register(make(MIND_OPERATOR_III, ModItemNames.MIND_OPERATOR_III));
        registry.register(make(REFINED_SOUL_METAL_AXE, ModItemNames.REFINED_SOUL_METAL_AXE));
        registry.register(make(REFINED_SOUL_METAL_BOOTS, ModItemNames.REFINED_SOUL_METAL_BOOTS));
        registry.register(make(REFINED_SOUL_METAL_CHESTPLATE, ModItemNames.REFINED_SOUL_METAL_CHESTPLATE));
        registry.register(make(REFINED_SOUL_METAL_HELMET, ModItemNames.REFINED_SOUL_METAL_HELMET));
        registry.register(make(REFINED_SOUL_METAL_HOE, ModItemNames.REFINED_SOUL_METAL_HOE));
        registry.register(make(REFINED_SOUL_METAL_HORSE_ARMOR, ModItemNames.REFINED_SOUL_METAL_HORSE_ARMOR));
        registry.register(make(REFINED_SOUL_METAL_INGOT, ModItemNames.REFINED_SOUL_METAL_INGOT));
        registry.register(make(REFINED_SOUL_METAL_LEGGINGS, ModItemNames.REFINED_SOUL_METAL_LEGGINGS));
        registry.register(make(REFINED_SOUL_METAL_NUGGET, ModItemNames.REFINED_SOUL_METAL_NUGGET));
        registry.register(make(REFINED_SOUL_METAL_PICKAXE, ModItemNames.REFINED_SOUL_METAL_PICKAXE));
        registry.register(make(REFINED_SOUL_METAL_SHOVEL, ModItemNames.REFINED_SOUL_METAL_SHOVEL));
        registry.register(make(REFINED_SOUL_METAL_SWORD, ModItemNames.REFINED_SOUL_METAL_SWORD));
        registry.register(make(SOUL_ARROW, ModItemNames.SOUL_ARROW));
        registry.register(make(SOUL_BLAZE_POWDER, ModItemNames.SOUL_BLAZE_POWDER));
        registry.register(make(SOUL_BLAZE_ROD, ModItemNames.SOUL_BLAZE_ROD));
        registry.register(make(SOUL_BOW, ModItemNames.SOUL_BOW));
        registry.register(make(SOUL_CONTAINER, ModItemNames.SOUL_CONTAINER));
        registry.register(make(SOUL_ENERGY_GEM, ModItemNames.SOUL_ENERGY_GEM));
        registry.register(make(SOUL_ENERGY_GEM_II, ModItemNames.SOUL_ENERGY_GEM_II));
        registry.register(make(SOUL_LAVA_BUCKET, ModItemNames.SOUL_LAVA_BUCKET));
        registry.register(make(SOUL_METAL_INGOT, ModItemNames.SOUL_METAL_INGOT));
        registry.register(make(SOUL_METAL_NUGGET, ModItemNames.SOUL_METAL_NUGGET));
        registry.register(make(SOUL_METAL_PARTICLE, ModItemNames.SOUL_METAL_PARTICLE));
        registry.register(make(SOUL_PIECE, ModItemNames.SOUL_PIECE));
        registry.register(make(SOUL_POWDER, ModItemNames.SOUL_POWDER));
        registry.register(make(SOUL_PURIFIER, ModItemNames.SOUL_PURIFIER));
        registry.register(make(SOUL_PURIFIER_II, ModItemNames.SOUL_PURIFIER_II));
        registerBlockItems(registry);
        registerSpawnEggs(registry);
    }

    private static void registerBlockItems(IForgeRegistry<Item> registry) {
        registry.register(make(BROKEN_REFINED_SOUL_METAL_BARS, ModBlockNames.BROKEN_REFINED_SOUL_METAL_BARS));
        registry.register(make(CHIPPED_REFINED_SOUL_METAL_BARS, ModBlockNames.CHIPPED_REFINED_SOUL_METAL_BARS));
        registry.register(make(CHIPPED_SOUL_METAL_BARS, ModBlockNames.CHIPPED_SOUL_METAL_BARS));
        registry.register(make(CHISELED_SOUL_STONE_BRICKS, ModBlockNames.CHISELED_SOUL_STONE_BRICKS));
        registry.register(make(CRACKED_DECAYED_STONE_BRICK_SLAB, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_SLAB));
        registry.register(make(CRACKED_DECAYED_STONE_BRICK_STAIRS, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_STAIRS));
        registry.register(make(CRACKED_DECAYED_STONE_BRICK_WALL, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_WALL));
        registry.register(make(CRACKED_DECAYED_STONE_BRICKS, ModBlockNames.CRACKED_DECAYED_STONE_BRICKS));
        registry.register(make(CRACKED_SOUL_STONE_BRICK_SLAB, ModBlockNames.CRACKED_SOUL_STONE_BRICK_SLAB));
        registry.register(make(CRACKED_SOUL_STONE_BRICK_STAIRS, ModBlockNames.CRACKED_SOUL_STONE_BRICK_STAIRS));
        registry.register(make(CRACKED_SOUL_STONE_BRICK_WALL, ModBlockNames.CRACKED_SOUL_STONE_BRICK_WALL));
        registry.register(make(CRACKED_SOUL_STONE_BRICKS, ModBlockNames.CRACKED_SOUL_STONE_BRICKS));
        registry.register(make(CRIMSON_HYPHAL_SOIL, ModBlockNames.CRIMSON_HYPHAL_SOIL));
        registry.register(make(DAMAGED_REFINED_SOUL_METAL_BARS, ModBlockNames.DAMAGED_REFINED_SOUL_METAL_BARS));
        registry.register(make(DAMAGED_SOUL_METAL_BARS, ModBlockNames.DAMAGED_SOUL_METAL_BARS));
        registry.register(make(DECAYED_STONE, ModBlockNames.DECAYED_STONE));
        registry.register(make(DECAYED_STONE_BRICK_SLAB, ModBlockNames.DECAYED_STONE_BRICK_SLAB));
        registry.register(make(DECAYED_STONE_BRICK_STAIRS, ModBlockNames.DECAYED_STONE_BRICK_STAIRS));
        registry.register(make(DECAYED_STONE_BRICK_WALL, ModBlockNames.DECAYED_STONE_BRICK_WALL));
        registry.register(make(DECAYED_STONE_BRICKS, ModBlockNames.DECAYED_STONE_BRICKS));
        registry.register(make(DECAYED_STONE_SLAB, ModBlockNames.DECAYED_STONE_SLAB));
        registry.register(make(DECAYED_STONE_STAIRS, ModBlockNames.DECAYED_STONE_STAIRS));
        registry.register(make(DECAYED_STONE_WALL, ModBlockNames.DECAYED_STONE_WALL));
        registry.register(make(DEPTH_SEGEN, ModBlockNames.DEPTH_SEGEN));
        registry.register(make(DEPTH_SEGEN_II, ModBlockNames.DEPTH_SEGEN_II));
        registry.register(make(HEAT_SEGEN, ModBlockNames.HEAT_SEGEN));
        registry.register(make(HEAT_SEGEN_II, ModBlockNames.HEAT_SEGEN_II));
        registry.register(make(MAGNETIC_FIELD_GENERATOR, ModBlockNames.MAGNETIC_FIELD_GENERATOR));
        registry.register(make(NETHER_SEGEN, ModBlockNames.NETHER_SEGEN));
        registry.register(make(NETHER_SEGEN_II, ModBlockNames.NETHER_SEGEN_II));
        registry.register(make(PARCHED_SOIL, ModBlockNames.PARCHED_SOIL));
        registry.register(make(PURIFIED_SOULIFIED_BEDROCK, ModBlockNames.PURIFIED_SOULIFIED_BEDROCK));
        registry.register(make(REFINED_SOUL_METAL_BARS, ModBlockNames.REFINED_SOUL_METAL_BARS));
        registry.register(make(REFINED_SOUL_METAL_BLOCK, ModBlockNames.REFINED_SOUL_METAL_BLOCK));
        registry.register(make(REFINED_SOUL_SAND, ModBlockNames.REFINED_SOUL_SAND));
        registry.register(make(REFINED_SOUL_SOIL, ModBlockNames.REFINED_SOUL_SOIL));
        registry.register(make(SEGEN, ModBlockNames.SEGEN));
        registry.register(make(SEGEN_II, ModBlockNames.SEGEN_II));
        registry.register(make(SKY_SEGEN, ModBlockNames.SKY_SEGEN));
        registry.register(make(SKY_SEGEN_II, ModBlockNames.SKY_SEGEN_II));
        registry.register(make(SMOOTH_SOUL_STONE, ModBlockNames.SMOOTH_SOUL_STONE));
        registry.register(make(SMOOTH_SOUL_STONE_SLAB, ModBlockNames.SMOOTH_SOUL_STONE_SLAB));
        registry.register(make(SMOOTH_SOUL_STONE_STAIRS, ModBlockNames.SMOOTH_SOUL_STONE_STAIRS));
        registry.register(make(SMOOTH_SOUL_STONE_WALL, ModBlockNames.SMOOTH_SOUL_STONE_WALL));
        registry.register(make(SOLAR_SEGEN, ModBlockNames.SOLAR_SEGEN));
        registry.register(make(SOLAR_SEGEN_II, ModBlockNames.SOLAR_SEGEN_II));
        registry.register(make(SOUL_ENERGY_STORAGE, ModBlockNames.SOUL_ENERGY_STORAGE));
        registry.register(make(SOUL_ENERGY_STORAGE_II, ModBlockNames.SOUL_ENERGY_STORAGE_II));
        registry.register(make(SOUL_METAL_BARS, ModBlockNames.SOUL_METAL_BARS));
        registry.register(make(SOUL_METAL_BLOCK, ModBlockNames.SOUL_METAL_BLOCK));
        registry.register(make(SOUL_OBSIDIAN, ModBlockNames.SOUL_OBSIDIAN));
        registry.register(make(SOUL_REINFORCEMENT_TABLE, ModBlockNames.SOUL_REINFORCEMENT_TABLE));
        registry.register(make(SOUL_STONE, ModBlockNames.SOUL_STONE));
        registry.register(make(SOUL_STONE_BRICK_SLAB, ModBlockNames.SOUL_STONE_BRICK_SLAB));
        registry.register(make(SOUL_STONE_BRICK_STAIRS, ModBlockNames.SOUL_STONE_BRICK_STAIRS));
        registry.register(make(SOUL_STONE_BRICK_WALL, ModBlockNames.SOUL_STONE_BRICK_WALL));
        registry.register(make(SOUL_STONE_BRICKS, ModBlockNames.SOUL_STONE_BRICKS));
        registry.register(make(SOUL_STONE_SLAB, ModBlockNames.SOUL_STONE_SLAB));
        registry.register(make(SOUL_STONE_STAIRS, ModBlockNames.SOUL_STONE_STAIRS));
        registry.register(make(SOUL_STONE_WALL, ModBlockNames.SOUL_STONE_WALL));
        registry.register(make(SOUL_WART, ModBlockNames.SOUL_WART));
        registry.register(make(SOULIFIED_BEDROCK, ModBlockNames.SOULIFIED_BEDROCK));
        registry.register(make(SOULIFIED_BUSH, ModBlockNames.SOULIFIED_BUSH));
        registry.register(make(WARPED_HYPHAL_SOIL, ModBlockNames.WARPED_HYPHAL_SOIL));
    }

    private static void registerSpawnEggs(IForgeRegistry<Item> registry) {
        registerSpawnEgg(registry, COMPUTER_SCIENTIST_SPAWN_EGG, ModEntityNames.COMPUTER_SCIENTIST);
        registerSpawnEgg(registry, DARK_EVOKER_SPAWN_EGG, ModEntityNames.DARK_EVOKER);
        registerSpawnEgg(registry, ENGINEER_SPAWN_EGG, ModEntityNames.ENGINEER);
        registerSpawnEgg(registry, ETHE_ARMORER_SPAWN_EGG, ModEntityNames.ETHE_ARMORER);
        registerSpawnEgg(registry, ILLUSORY_HORSE_SPAWN_EGG, ModEntityNames.ILLUSORY_HORSE);
        registerSpawnEgg(registry, REDSTONE_MORTAR_SPAWN_EGG, ModEntityNames.REDSTONE_MORTAR);
        registerSpawnEgg(registry, REDSTONE_TURRET_SPAWN_EGG, ModEntityNames.REDSTONE_TURRET);
        registerSpawnEgg(registry, SOUL_RABBIT_SPAWN_EGG, ModEntityNames.SOUL_RABBIT);
        registerSpawnEgg(registry, SOUL_SKELETON_SPAWN_EGG, ModEntityNames.SOUL_SKELETON);
        registerSpawnEgg(registry, VOID_ALCHEMIST_SPAWN_EGG, ModEntityNames.VOID_ALCHEMIST);
        registerSpawnEgg(registry, VOID_ARCHER_SPAWN_EGG, ModEntityNames.VOID_ARCHER);
        registerSpawnEgg(registry, VOID_DEFENDER_SPAWN_EGG, ModEntityNames.VOID_DEFENDER);
        registerSpawnEgg(registry, VOIDWALKER_SPAWN_EGG, ModEntityNames.VOIDWALKER);
        registerSpawnEgg(registry, WANDERER_SPAWN_EGG, ModEntityNames.WANDERER);
    }

    private static void registerSpawnEgg(IForgeRegistry<Item> registry, Item spawnEgg, String entityName) {
        registry.register(make(spawnEgg, entityName + SPAWN_EGG_SUFFIX));
    }

    public static Rarity next(Rarity rarity, int count) {
        for (int i = 0; i < count; i++) {
            rarity = next(rarity);
        }
        return rarity;
    }

    public static Rarity next(Rarity rarity) {
        switch (rarity) {
            case COMMON:
                return Rarity.UNCOMMON;
            case UNCOMMON:
                return Rarity.RARE;
            case RARE:
                return Rarity.EPIC;
            case EPIC:
                return ModRarities.LEGENDARY;
            default:
                return rarity == ModRarities.LEGENDARY ? ModRarities.MAX : rarity;
        }
    }
}
