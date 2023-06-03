package lych.soullery.data;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlockStateProperties;
import lych.soullery.block.entity.SEStorageTileEntity;
import lych.soullery.util.blg.BlockGroup;
import net.minecraft.block.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static lych.soullery.block.ModBlocks.*;
import static lych.soullery.data.BlockModelDataGen.*;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStateDataGen extends BlockStateProvider {
    public BlockStateDataGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Soullery.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(SOUL_REINFORCEMENT_TABLE, models().orientableWithBottom(name(SOUL_REINFORCEMENT_TABLE),
                side(SOUL_REINFORCEMENT_TABLE),
                side(SOUL_REINFORCEMENT_TABLE),
                prefix(SOUL_STONE),
                top(SOUL_REINFORCEMENT_TABLE)));
        simpleBlock(CHISELED_SOUL_STONE_BRICKS, modelFromBlock(CHISELED_SOUL_STONE_BRICKS));
        simpleBlock(CRACKED_DECAYED_STONE_BRICKS);
        slabBlock(CRACKED_DECAYED_STONE_BRICK_SLAB, prefix(CRACKED_DECAYED_STONE_BRICKS), prefix(CRACKED_DECAYED_STONE_BRICKS));
        stairsBlock(CRACKED_DECAYED_STONE_BRICK_STAIRS, prefix(CRACKED_DECAYED_STONE_BRICKS));
        wallBlock(CRACKED_DECAYED_STONE_BRICK_WALL, prefix(CRACKED_DECAYED_STONE_BRICKS));
        simpleBlock(CRACKED_SOUL_STONE_BRICKS);
        slabBlock(CRACKED_SOUL_STONE_BRICK_SLAB, prefix(CRACKED_SOUL_STONE_BRICKS), prefix(CRACKED_SOUL_STONE_BRICKS));
        stairsBlock(CRACKED_SOUL_STONE_BRICK_STAIRS, prefix(CRACKED_SOUL_STONE_BRICKS));
        wallBlock(CRACKED_SOUL_STONE_BRICK_WALL, prefix(CRACKED_SOUL_STONE_BRICKS));
        simpleBlock(CRIMSON_HYPHAL_SOIL, modelFromBlock(CRIMSON_HYPHAL_SOIL));
        simpleBlock(DECAYED_STONE);
        slabBlock(DECAYED_STONE_BRICK_SLAB, prefix(DECAYED_STONE_BRICKS), prefix(DECAYED_STONE_BRICKS));
        stairsBlock(DECAYED_STONE_BRICK_STAIRS, prefix(DECAYED_STONE_BRICKS));
        wallBlock(DECAYED_STONE_BRICK_WALL, prefix(DECAYED_STONE_BRICKS));
        simpleBlock(DECAYED_STONE_BRICKS);
        slabBlock(DECAYED_STONE_SLAB, prefix(DECAYED_STONE), prefix(DECAYED_STONE));
        stairsBlock(DECAYED_STONE_STAIRS, prefix(DECAYED_STONE));
        wallBlock(DECAYED_STONE_WALL, prefix(DECAYED_STONE));
        fire(INFERNO);
        simpleBlock(PARCHED_SOIL);
        fire(POISONOUS_FIRE);
        simpleBlock(POTTED_SOULIFIED_BUSH, modelFromBlock(POTTED_SOULIFIED_BUSH));
        fire(PURE_SOUL_FIRE);
        simpleBlock(PURIFIED_SOULIFIED_BEDROCK);
        simpleBlock(REFINED_SOUL_METAL_BLOCK);
        simpleBlock(REFINED_SOUL_SAND);
        simpleBlock(REFINED_SOUL_SOIL);
        simpleBlock(SMOOTH_SOUL_STONE);
        slabBlock(SMOOTH_SOUL_STONE_SLAB, prefix(SMOOTH_SOUL_STONE), prefix(SMOOTH_SOUL_STONE));
        stairsBlock(SMOOTH_SOUL_STONE_STAIRS, prefix(SMOOTH_SOUL_STONE));
        wallBlock(SMOOTH_SOUL_STONE_WALL, prefix(SMOOTH_SOUL_STONE));
        for (int i = 0; i <= SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL; i++) {
            getVariantBuilder(SOUL_ENERGY_STORAGE)
                    .partialState()
                    .with(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i)
                    .addModels(new ConfiguredModel(models().cubeColumn(
                            String.format("%s_%d", prefix(SOUL_ENERGY_STORAGE), i),
                            prefix(String.format("%s_%d", name(SOUL_ENERGY_STORAGE), i)),
                            prefix(BlockModelDataGen.SIMPLE_MACHINE_SIDE))));
        }
        for (int i = 0; i <= SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL; i++) {
            getVariantBuilder(SOUL_ENERGY_STORAGE_II)
                    .partialState()
                    .with(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i)
                    .addModels(new ConfiguredModel(models().cubeColumn(
                            String.format("%s_%d", prefix(SOUL_ENERGY_STORAGE_II), i),
                            prefix(String.format("%s_%d", name(SOUL_ENERGY_STORAGE_II), i)),
                            prefix(BlockModelDataGen.SIMPLE_L2_MACHINE_SIDE))));
        }
        segens();
        simpleBlock(SOUL_LAVA_FLUID_BLOCK, modelFromBlock(SOUL_LAVA_FLUID_BLOCK));
        ironBarBlock(SOUL_METAL_BARS, prefix(SOUL_METAL_BARS));
        ironBarBlock(CHIPPED_SOUL_METAL_BARS, prefix(CHIPPED_SOUL_METAL_BARS));
        ironBarBlock(DAMAGED_SOUL_METAL_BARS, prefix(DAMAGED_SOUL_METAL_BARS));
        ironBarBlock(REFINED_SOUL_METAL_BARS, prefix(REFINED_SOUL_METAL_BARS));
        ironBarBlock(CHIPPED_REFINED_SOUL_METAL_BARS, prefix(CHIPPED_REFINED_SOUL_METAL_BARS));
        ironBarBlock(DAMAGED_REFINED_SOUL_METAL_BARS, prefix(DAMAGED_REFINED_SOUL_METAL_BARS));
        ironBarBlock(BROKEN_REFINED_SOUL_METAL_BARS, prefix(BROKEN_REFINED_SOUL_METAL_BARS));
        simpleBlock(SOUL_METAL_BLOCK);
        simpleBlock(SOUL_OBSIDIAN);
        simpleBlock(SOUL_STONE);
        slabBlock(SOUL_STONE_BRICK_SLAB, prefix(SOUL_STONE_BRICKS), prefix(SOUL_STONE_BRICKS));
        stairsBlock(SOUL_STONE_BRICK_STAIRS, prefix(SOUL_STONE_BRICKS));
        wallBlock(SOUL_STONE_BRICK_WALL, prefix(SOUL_STONE_BRICKS));
        simpleBlock(SOUL_STONE_BRICKS);
        slabBlock(SOUL_STONE_SLAB, prefix(SOUL_STONE), prefix(SOUL_STONE));
        stairsBlock(SOUL_STONE_STAIRS, prefix(SOUL_STONE));
        wallBlock(SOUL_STONE_WALL, prefix(SOUL_STONE));
        cropsBlock(SOUL_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
        simpleBlock(SOULIFIED_BEDROCK);
        simpleBlock(SOULIFIED_BUSH, modelFromBlock(SOULIFIED_BUSH));
        simpleBlock(WARPED_HYPHAL_SOIL, modelFromBlock(WARPED_HYPHAL_SOIL));
        tfbBlock(MAGNETIC_FIELD_GENERATOR);
        simpleBlock(INSTANT_SPAWNER, modelFromBlock(Blocks.SPAWNER));

        for (BlockGroup<?> group : BlockGroup.getBlockGroups()) {
            group.fillBlockStates(this);
        }
    }

    private void segens() {
        sidedSegen(DEPTH_SEGEN, "depth_segen_side");
        sidedSegen(DEPTH_SEGEN_II, "depth_segen_l2_side");
        segen(HEAT_SEGEN);
        segen(HEAT_SEGEN_II);
        segen(NETHER_SEGEN);
        segen(NETHER_SEGEN_II);
        segen(SEGEN);
        segen(SEGEN_II);
        segen(SKY_SEGEN);
        segen(SKY_SEGEN_II);
        sidedSegen(SOLAR_SEGEN, false);
        sidedSegen(SOLAR_SEGEN_II, true);
    }

    private void segen(Block segen) {
        getVariantBuilder(segen)
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, false)
                .addModels(new ConfiguredModel(models().singleTexture(prefix(segen).toString(), BlockModelDataGen.CUBE_ALL.getLocation(), BlockModelDataGen.ALL_NAME, prefix(segen))))
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, true)
                .addModels(new ConfiguredModel(models().singleTexture(prefix(segen) + "_gs", BlockModelDataGen.CUBE_ALL.getLocation(), BlockModelDataGen.ALL_NAME, prefix(name(segen) + "_gs"))));
    }

    private void sidedSegen(Block segen, boolean l2) {
        sidedSegen(segen, l2 ? BlockModelDataGen.SIMPLE_L2_MACHINE_SIDE : BlockModelDataGen.SIMPLE_MACHINE_SIDE);
    }

    private void sidedSegen(Block segen, String sideTex) {
        getVariantBuilder(segen)
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, false)
                .addModels(new ConfiguredModel(models().orientable(prefix(segen).toString(),
                        prefix(sideTex),
                        prefix(sideTex),
                        prefix(segen))))
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, true)
                .addModels(new ConfiguredModel(models().orientable(prefix(segen) + "_gs",
                        prefix(sideTex),
                        prefix(sideTex),
                        prefix(name(segen) + "_gs"))));
    }

    public void tfbBlock(Block block) {
        ResourceLocation side = prefix(block, "_side");
        ResourceLocation front = prefix(block, "_front");
        ResourceLocation bottom = prefix(block, "_bottom");
        ResourceLocation top = prefix(block, "_top");
        tfbBlock(block, side, front, bottom, top);
    }

    public void tfbBlock(Block block, ResourceLocation side, ResourceLocation front, ResourceLocation bottom, ResourceLocation top) {
        BlockModelBuilder model = models().orientableWithBottom(prefix(block).toString(), side, front, bottom, top);
        getVariantBuilder(block)
                .partialState()
                .with(HorizontalBlock.FACING, Direction.EAST)
                .addModels(ConfiguredModel.builder().modelFile(model).rotationY(90).buildLast())
                .partialState()
                .with(HorizontalBlock.FACING, Direction.NORTH)
                .addModels(new ConfiguredModel(model))
                .partialState()
                .with(HorizontalBlock.FACING, Direction.SOUTH)
                .addModels(ConfiguredModel.builder().modelFile(model).rotationY(180).buildLast())
                .partialState()
                .with(HorizontalBlock.FACING, Direction.WEST)
                .addModels(ConfiguredModel.builder().modelFile(model).rotationY(270).buildLast());
    }

    public void ironBarBlock(PaneBlock block, ResourceLocation tex) {
        ironBarBlock(block, block.getRegistryName().getPath(), tex, tex, tex);
    }

    public void ironBarBlock(PaneBlock block, String baseName, ResourceLocation particle, ResourceLocation bars, ResourceLocation edge) {
        ModelFile postEnds = ironBarPostEnds(baseName + "_post_ends", particle, edge);
        ModelFile post = ironBarPost(baseName + "_post", particle, bars);
        ModelFile cap = ironBarCap(baseName + "_cap", particle, edge, bars);
        ModelFile capAlt = ironBarCapAlt(baseName + "_cap_alt", particle, edge, bars);
        ModelFile side = ironBarSide(baseName + "_side", particle, edge, bars);
        ModelFile sideAlt = ironBarSideAlt(baseName + "_side_alt", particle, edge, bars);
        ironBarBlock(block, postEnds, post, cap, capAlt, side, sideAlt);
    }

    public ModelFile ironBarPostEnds(String name, ResourceLocation particle, ResourceLocation edge) {
        return models().withExistingParent(name, BLOCK_FOLDER + "/" + "iron_bars_post_ends")
                .texture("particle", particle)
                .texture("edge", edge);
    }

    public ModelFile ironBarPost(String name, ResourceLocation particle, ResourceLocation bars) {
        return models().withExistingParent(name, BLOCK_FOLDER + "/" + "iron_bars_post")
                .texture("particle", particle)
                .texture("bars", bars);
    }

    public ModelFile ironBarCap(String name, ResourceLocation particle, ResourceLocation bars, ResourceLocation edge) {
        return models().withExistingParent(name, BLOCK_FOLDER + "/" + "iron_bars_cap")
                .texture("particle", particle)
                .texture("bars", bars)
                .texture("edge", edge);
    }

    public ModelFile ironBarCapAlt(String name, ResourceLocation particle, ResourceLocation bars, ResourceLocation edge) {
        return models().withExistingParent(name, BLOCK_FOLDER + "/" + "iron_bars_cap_alt")
                .texture("particle", particle)
                .texture("bars", bars)
                .texture("edge", edge);
    }

    public ModelFile ironBarSide(String name, ResourceLocation particle, ResourceLocation bars, ResourceLocation edge) {
        return models().withExistingParent(name, BLOCK_FOLDER + "/" + "iron_bars_side")
                .texture("particle", particle)
                .texture("bars", bars)
                .texture("edge", edge);
    }

    public ModelFile ironBarSideAlt(String name, ResourceLocation particle, ResourceLocation bars, ResourceLocation edge) {
        return models().withExistingParent(name, BLOCK_FOLDER + "/" + "iron_bars_side_alt")
                .texture("particle", particle)
                .texture("bars", bars)
                .texture("edge", edge);
    }

    public void ironBarBlock(PaneBlock block, ModelFile postEnds, ModelFile post, ModelFile cap, ModelFile capAlt, ModelFile side, ModelFile sideAlt) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block)
                .part().modelFile(postEnds).addModel().end();

        MultiPartBlockStateBuilder.PartBuilder postModel = builder.part().modelFile(post).addModel();
        SixWayBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
            if (dir.getAxis().isHorizontal()) {
                postModel.condition(value, false);
            }
        });
        postModel.end();

        SixWayBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
            if (dir.getAxis().isHorizontal()) {
                boolean alt = dir == Direction.SOUTH || dir == Direction.WEST;
                boolean r90 = dir.getAxis() == Direction.Axis.X;
                MultiPartBlockStateBuilder.PartBuilder capModel = builder.part().modelFile(alt ? capAlt : cap).rotationY(r90 ? 90 : 0).addModel();
                SixWayBlock.PROPERTY_BY_DIRECTION.forEach((dirIn, valueIn) -> {
                    if (dirIn.getAxis().isHorizontal()) {
                        capModel.condition(valueIn, dirIn == dir);
                    }
                });
                capModel.end();
            }
        });

        SixWayBlock.PROPERTY_BY_DIRECTION.forEach((dir, value) -> {
            if (dir.getAxis().isHorizontal()) {
                boolean alt = dir == Direction.SOUTH || dir == Direction.WEST;
                boolean r90 = dir.getAxis() == Direction.Axis.X;
                builder.part().modelFile(alt ? sideAlt : side).rotationY(r90 ? 90 : 0).addModel()
                        .condition(value, true).end();
            }
        });
    }

    protected ConfiguredModel modelFromBlock(Block block) {
        return new ConfiguredModel(new UncheckedModelFile(blockTexture(block)));
    }

    protected void rotatedBlock(Block block) {
        simpleBlock(block, new ConfiguredModel(cubeAll(block)), ConfiguredModel.builder().modelFile(cubeAll(block)).rotationY(90).buildLast(), ConfiguredModel.builder().modelFile(cubeAll(block)).rotationY(180).buildLast(), ConfiguredModel.builder().modelFile(cubeAll(block)).rotationY(270).buildLast());
    }

    protected void cropsBlock(Block block, IntegerProperty ageProperty, int... serialNumbers) {
        List<Integer> possibleValues = ageProperty.getPossibleValues().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        if (possibleValues.size() != serialNumbers.length) {
            throw new IllegalArgumentException(String.format("Illegal serialNumbers length, length required: %d, provided: %d", possibleValues.size(), serialNumbers.length));
        }
        for (int i = 0; i < possibleValues.size(); i++) {
            int value = possibleValues.get(i);
            getVariantBuilder(block).partialState().with(ageProperty, value).addModels(new ConfiguredModel(models().crop(String.format("block/%s_stage%d", name(block), serialNumbers[i]), Soullery.prefix(String.format("block/%s_stage%d", name(block), serialNumbers[i])))));
        }
    }

    protected void fire(Block block) {
        fire(block,
                fireFloorModel(block, 0),
                fireFloorModel(block, 1),
                fireSideModel(block, 0, false),
                fireSideModel(block, 1, false),
                fireSideModel(block, 0, true),
                fireSideModel(block, 1, true));
    }

    protected void fire(Block block, ModelFile floor0, ModelFile floor1, ModelFile side0, ModelFile side1, ModelFile sideAlt0, ModelFile sideAlt1) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
//      Floor
        builder.part()
                .modelFile(floor0)
                .nextModel()
                .modelFile(floor1)
                .addModel();
//      North
        builder.part()
                .modelFile(side0)
                .nextModel()
                .modelFile(side1)
                .nextModel()
                .modelFile(sideAlt0)
                .nextModel()
                .modelFile(sideAlt1)
                .addModel();
//      East
        builder.part()
                .modelFile(side0)
                .rotationY(90)
                .nextModel()
                .modelFile(side1)
                .rotationY(90)
                .nextModel()
                .modelFile(sideAlt0)
                .rotationY(90)
                .nextModel()
                .modelFile(sideAlt1)
                .rotationY(90)
                .addModel()
                .useOr();
//      South
        builder.part()
                .modelFile(side0)
                .rotationY(180)
                .nextModel()
                .modelFile(side1)
                .rotationY(180)
                .nextModel()
                .modelFile(sideAlt0)
                .rotationY(180)
                .nextModel()
                .modelFile(sideAlt1)
                .rotationY(180)
                .addModel();
//      West
        builder.part()
                .modelFile(side0)
                .rotationY(270)
                .nextModel()
                .modelFile(side1)
                .rotationY(270)
                .nextModel()
                .modelFile(sideAlt0)
                .rotationY(270)
                .nextModel()
                .modelFile(sideAlt1)
                .rotationY(270)
                .addModel();
    }

    private BlockModelBuilder fireFloorModel(Block fireBlock, int texId) {
        return models().withExistingParent(name(fireBlock) + "_floor" + texId, BLOCK_FOLDER + "/template_fire_floor").texture("fire", prefix(fireBlock) + "_" + texId);
    }

    private BlockModelBuilder fireSideModel(Block fireBlock, int texId, boolean alt) {
        return models().withExistingParent(name(fireBlock) + "_side" + (alt ? "_alt" : "") + texId, BLOCK_FOLDER + "/template_fire_side" + (alt ? "_alt" : "")).texture("fire", prefix(fireBlock) + "_" + texId);
    }

    private BlockModelBuilder fireUpModel(Block fireBlock, int texId, boolean alt) {
        return models().withExistingParent(name(fireBlock) + "_up" + (alt ? "_alt" : "") + texId, BLOCK_FOLDER + "/template_fire_up" + (alt ? "_alt" : "")).texture("fire", prefix(fireBlock) + "_" + texId);
    }

    private static String name(Block block) {
        return Objects.requireNonNull(block.getRegistryName(), "Registry name should be non-null").getPath();
    }
}
