package lych.soullery.data;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlocks;
import lych.soullery.item.SEGemItem;
import lych.soullery.item.SoulBowItem;
import lych.soullery.util.SoulEnergies;
import lych.soullery.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

import static lych.soullery.data.ModDataGens.registryNameToString;
import static lych.soullery.item.ModItems.*;

public class ItemModelDataGen extends ItemModelProvider {
    private static final ModelFile GENERATED = new UncheckedModelFile("item/generated");
    private static final ModelFile HANDHELD = new UncheckedModelFile("item/handheld");
    private static final ModelFile SPAWN_EGG = new UncheckedModelFile("item/template_spawn_egg");
    private static final ResourceLocation HALF_USED_POTION_OVERLAY = Soullery.prefix("item/half_used_potion_overlay");
    private static final ResourceLocation LINGERING_POTION = new ResourceLocation("item/lingering_potion");
    private static final ResourceLocation POTION = new ResourceLocation("item/potion");
    private static final ResourceLocation SPLASH_POTION = new ResourceLocation("item/splash_potion");
    private static final String LAYER0 = "layer0";
    private static final String LAYER1 = "layer1";

    public ItemModelDataGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Soullery.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simple(registryNameToString(CHAOS_WAND), HANDHELD, prefix(CHAOS_WAND));
        simple(registryNameToString(CHAOS_WAND_II), HANDHELD, prefix(CHAOS_WAND_II));
        simple(registryNameToString(ENTITY_CARRIER), HANDHELD, prefix(ENTITY_CARRIER));
        simple(registryNameToString(ENTITY_CARRIER_II), HANDHELD, prefix(ENTITY_CARRIER_II));
        simple(registryNameToString(EXTRA_ABILITY_CARRIER), GENERATED, prefix(EXTRA_ABILITY_CARRIER));
        simple(registryNameToString(EXTRA_ABILITY_WAND), HANDHELD, prefix(EXTRA_ABILITY_WAND));
        halfUsedPotion(registryNameToString(HALF_USED_LINGERING_POTION), LINGERING_POTION);
        halfUsedPotion(registryNameToString(HALF_USED_POTION), POTION);
        halfUsedPotion(registryNameToString(HALF_USED_SPLASH_POTION), SPLASH_POTION);
        simple(registryNameToString(MIND_OPERATOR), HANDHELD, prefix(MIND_OPERATOR));
        simple(registryNameToString(MIND_OPERATOR_II), HANDHELD, prefix(MIND_OPERATOR_II));
        simple(registryNameToString(MIND_OPERATOR_III), HANDHELD, prefix(MIND_OPERATOR_III));
        simple(registryNameToString(REFINED_SOUL_METAL_AXE), HANDHELD, prefix(REFINED_SOUL_METAL_AXE));
        simple(registryNameToString(REFINED_SOUL_METAL_BOOTS), GENERATED, prefix(REFINED_SOUL_METAL_BOOTS));
        simple(registryNameToString(REFINED_SOUL_METAL_CHESTPLATE), GENERATED, prefix(REFINED_SOUL_METAL_CHESTPLATE));
        simple(registryNameToString(REFINED_SOUL_METAL_HELMET), GENERATED, prefix(REFINED_SOUL_METAL_HELMET));
        simple(registryNameToString(REFINED_SOUL_METAL_HOE), HANDHELD, prefix(REFINED_SOUL_METAL_HOE));
        simple(registryNameToString(REFINED_SOUL_METAL_HORSE_ARMOR), GENERATED, prefix(REFINED_SOUL_METAL_HORSE_ARMOR));
        simple(registryNameToString(REFINED_SOUL_METAL_INGOT), GENERATED, prefix(REFINED_SOUL_METAL_INGOT));
        simple(registryNameToString(REFINED_SOUL_METAL_LEGGINGS), GENERATED, prefix(REFINED_SOUL_METAL_LEGGINGS));
        simple(registryNameToString(REFINED_SOUL_METAL_NUGGET), GENERATED, prefix(REFINED_SOUL_METAL_NUGGET));
        simple(registryNameToString(REFINED_SOUL_METAL_PICKAXE), HANDHELD, prefix(REFINED_SOUL_METAL_PICKAXE));
        simple(registryNameToString(REFINED_SOUL_METAL_SHOVEL), HANDHELD, prefix(REFINED_SOUL_METAL_SHOVEL));
        simple(registryNameToString(REFINED_SOUL_METAL_SWORD), HANDHELD, prefix(REFINED_SOUL_METAL_SWORD));
        simple(registryNameToString(SOUL_ARROW), GENERATED, prefix(SOUL_ARROW));
        simple(registryNameToString(SOUL_BLAZE_POWDER), GENERATED, prefix(SOUL_BLAZE_POWDER));
        simple(registryNameToString(SOUL_BLAZE_ROD), HANDHELD, prefix(SOUL_BLAZE_ROD));
        getBuilder(registryNameToString(SOUL_BOW)).parent(new UncheckedModelFile(new ResourceLocation("item/" + Objects.requireNonNull(Items.BOW.getRegistryName()).getPath()))).texture(LAYER0, prefix(SOUL_BOW)).override()
                .predicate(SoulBowItem.PULLING, 1).model(new UncheckedModelFile(prefix(SOUL_BOW, "_pulling_0"))).end().override()
                .predicate(SoulBowItem.PULLING, 1).predicate(SoulBowItem.PULL, 0.65f).model(new UncheckedModelFile(prefix(SOUL_BOW, "_pulling_1"))).end().override()
                .predicate(SoulBowItem.PULLING, 1).predicate(SoulBowItem.PULL, 0.9f).model(new UncheckedModelFile(prefix(SOUL_BOW, "_pulling_2"))).end();
        simple(registryNameToString(SOUL_BOW) + "_pulling_0", new UncheckedModelFile(prefix(SOUL_BOW)), prefix(SOUL_BOW, "_pulling_0"));
        simple(registryNameToString(SOUL_BOW) + "_pulling_1", new UncheckedModelFile(prefix(SOUL_BOW)), prefix(SOUL_BOW, "_pulling_1"));
        simple(registryNameToString(SOUL_BOW) + "_pulling_2", new UncheckedModelFile(prefix(SOUL_BOW)), prefix(SOUL_BOW, "_pulling_2"));
        simple(registryNameToString(SOUL_CONTAINER), GENERATED, prefix(SOUL_CONTAINER));
        SEGem(SOUL_ENERGY_GEM);
        SEGem(SOUL_ENERGY_GEM_II);
        simple(registryNameToString(SOUL_LAVA_BUCKET), GENERATED, prefix(SOUL_LAVA_BUCKET));
        simple(registryNameToString(SOUL_METAL_INGOT), GENERATED, prefix(SOUL_METAL_INGOT));
        simple(registryNameToString(SOUL_METAL_NUGGET), GENERATED, prefix(SOUL_METAL_NUGGET));
        simple(registryNameToString(SOUL_METAL_PARTICLE), GENERATED, prefix(SOUL_METAL_PARTICLE));
        simple(registryNameToString(SOUL_PIECE), GENERATED, prefix(SOUL_PIECE));
        simple(registryNameToString(SOUL_POWDER), GENERATED, prefix(SOUL_POWDER));
        simple(registryNameToString(SOUL_PURIFIER), HANDHELD, prefix(SOUL_PURIFIER));
        simple(registryNameToString(SOUL_PURIFIER_II), HANDHELD, prefix(SOUL_PURIFIER_II));
        registerBlockItemModels();
        registerSpawnEggModels();
    }

    private void simple(String name, ModelFile model, ResourceLocation texture) {
        getBuilder(name).parent(model).texture(LAYER0, texture);
    }

    private void halfUsedPotion(String name, ResourceLocation texture) {
        getBuilder(name).parent(GENERATED).texture(LAYER0, HALF_USED_POTION_OVERLAY).texture(LAYER1, texture);
    }

    private void SEGem(SEGemItem item) {
        getBuilder(SEGemRegistryNameToString(item)).parent(GENERATED).texture(LAYER0, prefixSEGem(item, 0)).override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.1f).model(SEGemModel(item, 1)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.2f).model(SEGemModel(item, 2)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.3f).model(SEGemModel(item, 3)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.4f).model(SEGemModel(item, 4)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.5f).model(SEGemModel(item, 5)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.6f).model(SEGemModel(item, 6)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.7f).model(SEGemModel(item, 7)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.8f).model(SEGemModel(item, 8)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.9f).model(SEGemModel(item, 9)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 1).model(SEGemModel(item, 10)).end();
        simple(SEGemRegistryNameToString(item, 1), GENERATED, prefixSEGem(item, 1));
        simple(SEGemRegistryNameToString(item, 2), GENERATED, prefixSEGem(item, 2));
        simple(SEGemRegistryNameToString(item, 3), GENERATED, prefixSEGem(item, 3));
        simple(SEGemRegistryNameToString(item, 4), GENERATED, prefixSEGem(item, 4));
        simple(SEGemRegistryNameToString(item, 5), GENERATED, prefixSEGem(item, 5));
        simple(SEGemRegistryNameToString(item, 6), GENERATED, prefixSEGem(item, 6));
        simple(SEGemRegistryNameToString(item, 7), GENERATED, prefixSEGem(item, 7));
        simple(SEGemRegistryNameToString(item, 8), GENERATED, prefixSEGem(item, 8));
        simple(SEGemRegistryNameToString(item, 9), GENERATED, prefixSEGem(item, 9));
        simple(SEGemRegistryNameToString(item, 10), GENERATED, prefixSEGem(item, 10));
    }

    private void registerBlockItemModels() {
        blockItem(CHISELED_SOUL_STONE_BRICKS);
        blockItem(CRACKED_DECAYED_STONE_BRICK_SLAB);
        blockItem(CRACKED_DECAYED_STONE_BRICK_STAIRS);
        blockItem(CRACKED_DECAYED_STONE_BRICK_WALL);
        blockItem(CRACKED_DECAYED_STONE_BRICKS);
        blockItem(CRACKED_SOUL_STONE_BRICK_SLAB);
        blockItem(CRACKED_SOUL_STONE_BRICK_STAIRS);
        blockItem(CRACKED_SOUL_STONE_BRICK_WALL);
        blockItem(CRACKED_SOUL_STONE_BRICKS);
        blockItem(CRIMSON_HYPHAL_SOIL);
        blockItem(DECAYED_STONE);
        blockItem(DECAYED_STONE_BRICK_SLAB);
        blockItem(DECAYED_STONE_BRICK_STAIRS);
        blockItem(DECAYED_STONE_BRICK_WALL);
        blockItem(DECAYED_STONE_BRICKS);
        blockItem(DECAYED_STONE_SLAB);
        blockItem(DECAYED_STONE_STAIRS);
        blockItem(DECAYED_STONE_WALL);
        segenBlockItem(DEPTH_SEGEN);
        segenBlockItem(DEPTH_SEGEN_II);
        segenBlockItem(HEAT_SEGEN);
        segenBlockItem(HEAT_SEGEN_II);
        segenBlockItem(NETHER_SEGEN);
        segenBlockItem(NETHER_SEGEN_II);
        blockItem(PARCHED_SOIL);
        blockItem(REFINED_SOUL_METAL_BLOCK);
        blockItem(REFINED_SOUL_SAND);
        blockItem(REFINED_SOUL_SOIL);
        segenBlockItem(SEGEN);
        segenBlockItem(SEGEN_II);
        segenBlockItem(SKY_SEGEN);
        segenBlockItem(SKY_SEGEN_II);
        blockItem(SMOOTH_SOUL_STONE);
        blockItem(SMOOTH_SOUL_STONE_SLAB);
        blockItem(SMOOTH_SOUL_STONE_STAIRS);
        blockItem(SMOOTH_SOUL_STONE_WALL);
        segenBlockItem(SOLAR_SEGEN);
        segenBlockItem(SOLAR_SEGEN_II);
        blockItem(registryNameToString(SOUL_ENERGY_STORAGE), new UncheckedModelFile(BlockModelDataGen.prefix(Utils.getRegistryName(SOUL_ENERGY_STORAGE.getBlock()).getPath() + "_0")));
        blockItem(registryNameToString(SOUL_ENERGY_STORAGE_II), new UncheckedModelFile(BlockModelDataGen.prefix(Utils.getRegistryName(SOUL_ENERGY_STORAGE_II.getBlock()).getPath() + "_0")));
        simple(registryNameToString(SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.SOUL_METAL_BARS));
        simple(registryNameToString(CHIPPED_SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.CHIPPED_SOUL_METAL_BARS));
        simple(registryNameToString(DAMAGED_SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.DAMAGED_SOUL_METAL_BARS));
        simple(registryNameToString(REFINED_SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.REFINED_SOUL_METAL_BARS));
        simple(registryNameToString(CHIPPED_REFINED_SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.CHIPPED_REFINED_SOUL_METAL_BARS));
        simple(registryNameToString(DAMAGED_REFINED_SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.DAMAGED_REFINED_SOUL_METAL_BARS));
        simple(registryNameToString(BROKEN_REFINED_SOUL_METAL_BARS), GENERATED, BlockModelDataGen.prefix(ModBlocks.BROKEN_REFINED_SOUL_METAL_BARS));
        blockItem(SOUL_METAL_BLOCK);
        blockItem(SOUL_OBSIDIAN);
        blockItem(SOUL_REINFORCEMENT_TABLE);
        blockItem(SOUL_STONE);
        blockItem(SOUL_STONE_BRICK_SLAB);
        blockItem(SOUL_STONE_BRICK_STAIRS);
        blockItem(SOUL_STONE_BRICK_WALL);
        blockItem(SOUL_STONE_BRICKS);
        blockItem(SOUL_STONE_SLAB);
        blockItem(SOUL_STONE_STAIRS);
        blockItem(SOUL_STONE_WALL);
        simple(registryNameToString(SOUL_WART), GENERATED, prefix(SOUL_WART));
        simple(registryNameToString(SOULIFIED_BUSH), GENERATED, BlockModelDataGen.prefix(ModBlocks.SOULIFIED_BUSH));
        blockItem(WARPED_HYPHAL_SOIL);
    }

    private void registerSpawnEggModels() {
        spawnEgg(COMPUTER_SCIENTIST_SPAWN_EGG);
        spawnEgg(DARK_EVOKER_SPAWN_EGG);
        spawnEgg(ENGINEER_SPAWN_EGG);
        spawnEgg(ETHE_ARMORER_SPAWN_EGG);
        spawnEgg(ILLUSORY_HORSE_SPAWN_EGG);
        spawnEgg(REDSTONE_MORTAR_SPAWN_EGG);
        spawnEgg(REDSTONE_TURRET_SPAWN_EGG);
        spawnEgg(SOUL_RABBIT_SPAWN_EGG);
        spawnEgg(SOUL_SKELETON_SPAWN_EGG);
        spawnEgg(VOID_ALCHEMIST_SPAWN_EGG);
        spawnEgg(VOID_ARCHER_SPAWN_EGG);
        spawnEgg(VOID_DEFENDER_SPAWN_EGG);
        spawnEgg(VOIDWALKER_SPAWN_EGG);
        spawnEgg(WANDERER_SPAWN_EGG);
    }

    private void spawnEgg(Item item) {
        getBuilder(registryNameToString(item)).parent(SPAWN_EGG);
    }

    private void segenBlockItem(BlockItem segen) {
        blockItem(registryNameToString(segen), new UncheckedModelFile(BlockModelDataGen.prefix(Utils.getRegistryName(segen.getBlock()).getPath())));
    }

    private void blockItem(BlockItem item) {
        blockItem(registryNameToString(item), item.getBlock() instanceof WallBlock ? byWall((WallBlock) item.getBlock()) : byBlock(item.getBlock()));
    }

    private void blockItem(String itemPath, ModelFile blockModel) {
        getBuilder(itemPath).parent(blockModel);
    }

    static ResourceLocation prefix(Item item) {
        return prefix(item, "");
    }

    private static ResourceLocation prefix(Item item, String ex) {
        Objects.requireNonNull(item.getRegistryName(), "Registry name should be non-null");
        return Soullery.prefix("item/" + item.getRegistryName().getPath() + ex);
    }

    private static ResourceLocation prefixSEGem(SEGemItem item, int level) {
        Objects.requireNonNull(item.getRegistryName(), "Registry name should be non-null");
        return Soullery.prefix("item/" + item.getRegistryName().getPath() + "_" + level);
    }

    private static String SEGemRegistryNameToString(SEGemItem item) {
        return registryNameToString(item);
    }

    private static String SEGemRegistryNameToString(SEGemItem item, int level) {
        return registryNameToString(item) + "_" + level;
    }

    private static ModelFile SEGemModel(SEGemItem item, int level) {
        return new UncheckedModelFile(prefixSEGem(item, level));
    }

    private ModelFile byBlock(Block block) {
        return new UncheckedModelFile(BlockModelDataGen.prefix(block));
    }

    private ModelFile byWall(WallBlock block) {
        return new UncheckedModelFile(BlockModelDataGen.prefix(block) + "_inventory");
    }
}
