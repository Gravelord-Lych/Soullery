package lych.soullery.data;

import lych.soullery.Soullery;
import lych.soullery.util.blg.BlockGroup;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;
import java.util.function.Function;

import static lych.soullery.block.ModBlocks.*;
import static lych.soullery.data.ModDataGens.registryNameToString;
import static net.minecraft.block.Blocks.SOUL_SOIL;

public class BlockModelDataGen extends BlockModelProvider {
    static final ModelFile CUBE_ALL = new ModelFile.UncheckedModelFile("block/cube_all");
    private static final ModelFile CROSS = new ModelFile.UncheckedModelFile("block/cross");
    private static final ModelFile CUBE_COLUMN = new ModelFile.UncheckedModelFile("block/cube_column");
    private static final ModelFile FLOWER_POT_CROSS = new ModelFile.UncheckedModelFile("block/flower_pot_cross");
    static final String ALL_NAME = "all";
    private static final String CROSS_NAME = "cross";
    private static final String FLOWER_POT_CROSS_NAME = "plant";
    private static final String PARTICLE = "particle";
    private static final String MACHINE_SIDE = "soul_machine_side";
    static final String SIMPLE_MACHINE_SIDE = "simple_soul_machine_side";
    static final String L2_MACHINE_SIDE = "soul_machine_side";
    static final String SIMPLE_L2_MACHINE_SIDE = "simple_l2_soul_machine_side";

    public BlockModelDataGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Soullery.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        getBuilder(registryNameToString(CHISELED_SOUL_STONE_BRICKS)).parent(CUBE_COLUMN).texture("end", prefix(SMOOTH_SOUL_STONE)).texture("side", prefix(CHISELED_SOUL_STONE_BRICKS));
        cubeAll(CRACKED_DECAYED_STONE_BRICKS);
        wallInventory(wallInventoryToString(CRACKED_DECAYED_STONE_BRICK_WALL), prefix(CRACKED_DECAYED_STONE_BRICKS));
        cubeAll(CRACKED_SOUL_STONE_BRICKS);
        wallInventory(wallInventoryToString(CRACKED_SOUL_STONE_BRICK_WALL), prefix(CRACKED_SOUL_STONE_BRICKS));
        cubeBottomTop(registryNameToString(CRIMSON_HYPHAL_SOIL), side(CRIMSON_HYPHAL_SOIL), vanillaBottom(SOUL_SOIL), top(CRIMSON_HYPHAL_SOIL));
        cubeAll(DECAYED_STONE);
        wallInventory(wallInventoryToString(DECAYED_STONE_BRICK_WALL), prefix(DECAYED_STONE_BRICKS));
        cubeAll(DECAYED_STONE_BRICKS);
        wallInventory(wallInventoryToString(DECAYED_STONE_WALL), prefix(DECAYED_STONE));
        getBuilder(registryNameToString(PARCHED_SOIL)).parent(CUBE_ALL).texture(ALL_NAME, prefix(PARCHED_SOIL));
        pottedBlock(POTTED_SOULIFIED_BUSH, SOULIFIED_BUSH);
        cubeAll(PURIFIED_SOULIFIED_BEDROCK);
        cubeAll(REFINED_SOUL_METAL_BLOCK);
        cubeAll(REFINED_SOUL_SAND);
        cubeAll(REFINED_SOUL_SOIL);
        wallInventory(wallInventoryToString(SMOOTH_SOUL_STONE_WALL), prefix(SMOOTH_SOUL_STONE));
        getBuilder(registryNameToString(SOUL_LAVA_FLUID_BLOCK)).texture(PARTICLE, Soullery.prefix("block/soul_lava_still"));
        cubeAll(SOUL_METAL_BLOCK);
        cubeAll(SOUL_OBSIDIAN);
        cubeAll(SOUL_STONE);
        wallInventory(wallInventoryToString(SOUL_STONE_BRICK_WALL), prefix(SOUL_STONE_BRICKS));
        cubeAll(SOUL_STONE_BRICKS);
        wallInventory(wallInventoryToString(SOUL_STONE_WALL), prefix(SOUL_STONE));
        cubeAll(SOULIFIED_BEDROCK);
        getBuilder(registryNameToString(SOULIFIED_BUSH)).parent(CROSS).texture(CROSS_NAME, prefix(SOULIFIED_BUSH));
        cubeBottomTop(registryNameToString(WARPED_HYPHAL_SOIL), side(WARPED_HYPHAL_SOIL), vanillaBottom(SOUL_SOIL), top(WARPED_HYPHAL_SOIL));

        for (BlockGroup<?> group : BlockGroup.getBlockGroups()) {
            group.fillBlockModels(this);
        }
    }

    private void cubeAll(Block block) {
        cubeAll(this, block, ModDataGens::registryNameToString);
    }

    public static void cubeAll(BlockModelProvider provider, Block block, Function<? super Block, ? extends String> registryNameExtractor) {
        provider.getBuilder(registryNameExtractor.apply(block)).parent(CUBE_ALL).texture(ALL_NAME, prefix(block));
    }

    private void pottedBlock(Block pottedBlock, Block plantBlock) {
        getBuilder(registryNameToString(pottedBlock)).parent(FLOWER_POT_CROSS).texture(FLOWER_POT_CROSS_NAME, prefix(plantBlock));
    }

    private static String wallInventoryToString(WallBlock block) {
        return wallInventoryToString(block, ModDataGens::registryNameToString);
    }

    public static String wallInventoryToString(WallBlock block, Function<? super Block, ? extends String> registryNameExtractor) {
        return registryNameExtractor.apply(block) + "_inventory";
    }

    public static ResourceLocation prefix(Block block) {
        return prefix(block, "");
    }

    public static ResourceLocation prefix(Block block, String addition) {
        return prefix(block, addition, Soullery::prefix);
    }

    public static ResourceLocation prefix(Block block, String addition, Function<? super String, ? extends ResourceLocation> prefixFunction) {
        Objects.requireNonNull(block.getRegistryName(), "Registry name should be non-null");
        return prefix(block.getRegistryName().getPath() + addition, prefixFunction);
    }

    public static ResourceLocation prefix(String name) {
        return prefix(name, Soullery::prefix);
    }

    public static ResourceLocation prefix(String name, Function<? super String, ? extends ResourceLocation> prefixFunction) {
        return prefixFunction.apply("block/" + name);
    }

    static ResourceLocation side(Block block) {
        return prefix(block, "_side");
    }

    static ResourceLocation front(Block block) {
        return prefix(block, "_front");
    }

    static ResourceLocation bottom(Block block) {
        return prefix(block, "_bottom");
    }

    static ResourceLocation vanillaBottom(Block block) {
        return prefix(block, "", ResourceLocation::new);
    }

    static ResourceLocation top(Block block) {
        return prefix(block, "_top");
    }
}
