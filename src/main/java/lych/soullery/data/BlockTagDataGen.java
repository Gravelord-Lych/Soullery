package lych.soullery.data;

import lych.soullery.Soullery;
import lych.soullery.tag.ModBlockTags;
import lych.soullery.util.blg.BlockGroup;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import static lych.soullery.block.ModBlocks.*;
import static net.minecraft.block.Blocks.*;

public class BlockTagDataGen extends BlockTagsProvider {
    public BlockTagDataGen(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, Soullery.MOD_ID, existingFileHelper);
    }

    @Override
    public void addTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS).add(REFINED_SOUL_METAL_BLOCK, SOUL_METAL_BLOCK);
        tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(
                CHIPPED_SOUL_METAL_BARS,
                CRACKED_SOUL_STONE_BRICKS,
                DAMAGED_SOUL_METAL_BARS,
                DECAYED_STONE,
                DECAYED_STONE_BRICKS,
                SMOOTH_SOUL_STONE,
                SOUL_METAL_BARS,
                SOUL_METAL_BLOCK,
                SOUL_OBSIDIAN,
                SOUL_REINFORCEMENT_TABLE,
                SOUL_STONE,
                SOUL_STONE_BRICKS,
                SOULIFIED_BEDROCK);
        tag(BlockTags.WALLS).add(CRACKED_DECAYED_STONE_BRICK_WALL,
                CRACKED_SOUL_STONE_BRICK_WALL,
                DECAYED_STONE_BRICK_WALL,
                DECAYED_STONE_WALL,
                SMOOTH_SOUL_STONE_WALL,
                SOUL_STONE_BRICK_WALL,
                SOUL_STONE_WALL);
        tag(ModBlockTags.HYPHAL_SOUL_SOIL).add(CRIMSON_HYPHAL_SOIL, WARPED_HYPHAL_SOIL);
        tag(ModBlockTags.INFERNO_BASE_BLOCKS).add(PARCHED_SOIL);
        tag(ModBlockTags.POISONOUS_FIRE_BASE_BLOCKS)
                .addTag(BlockTags.WARPED_STEMS)
                .add(WARPED_NYLIUM, WARPED_PLANKS, WARPED_WART_BLOCK, WARPED_HYPHAL_SOIL);
        tag(ModBlockTags.PURE_SOUL_FIRE_BASED_BLOCKS).add(BROKEN_REFINED_SOUL_METAL_BARS,
                CHIPPED_REFINED_SOUL_METAL_BARS,
                DAMAGED_REFINED_SOUL_METAL_BARS,
                PURIFIED_SOULIFIED_BEDROCK,
                REFINED_SOUL_SAND,
                REFINED_SOUL_SOIL,
                REFINED_SOUL_METAL_BARS,
                REFINED_SOUL_METAL_BLOCK);
        tag(ModBlockTags.SOUL_RABBIT_SPAWNABLE_BLOCKS).add(CRIMSON_HYPHAL_SOIL,
                PARCHED_SOIL,
                REFINED_SOUL_SAND,
                REFINED_SOUL_SOIL,
                SOUL_SAND,
                SOUL_SOIL,
                WARPED_HYPHAL_SOIL);
        tag(ModBlockTags.SOULIFIED_BUSH_PLACEABLE_BLOCKS).add(CRIMSON_HYPHAL_SOIL,
                PARCHED_SOIL,
                REFINED_SOUL_SAND,
                REFINED_SOUL_SOIL,
                SOUL_SAND,
                SOUL_SOIL,
                WARPED_HYPHAL_SOIL);
        tag(ModBlockTags.SOUL_DRAGON_IMMUNE).add(SOUL_SAND,
                SOUL_SOIL,
                PARCHED_SOIL,
                REFINED_SOUL_SAND,
                REFINED_SOUL_SOIL,
                WARPED_HYPHAL_SOIL,
                CRIMSON_HYPHAL_SOIL,
                SOUL_OBSIDIAN,
                SOUL_METAL_BARS,
                CHIPPED_SOUL_METAL_BARS,
                DAMAGED_SOUL_METAL_BARS,
                REFINED_SOUL_METAL_BARS,
                CHIPPED_REFINED_SOUL_METAL_BARS,
                DAMAGED_REFINED_SOUL_METAL_BARS,
                BROKEN_REFINED_SOUL_METAL_BARS,
                SOUL_STONE,
                BEDROCK,
                SOULIFIED_BEDROCK,
                PURIFIED_SOULIFIED_BEDROCK);
        tag(BlockTags.DRAGON_IMMUNE).add(SOUL_OBSIDIAN, SOULIFIED_BEDROCK, PURIFIED_SOULIFIED_BEDROCK);
        tag(BlockTags.WITHER_IMMUNE).add(SOULIFIED_BEDROCK, PURIFIED_SOULIFIED_BEDROCK);
        tag(BlockTags.ENDERMAN_HOLDABLE).add(CRIMSON_HYPHAL_SOIL, WARPED_HYPHAL_SOIL);
        tag(BlockTags.FIRE).add(INFERNO, PURE_SOUL_FIRE);
        tag(BlockTags.MUSHROOM_GROW_BLOCK).add(CRIMSON_HYPHAL_SOIL, WARPED_HYPHAL_SOIL);
        tag(BlockTags.NYLIUM).add(CRIMSON_HYPHAL_SOIL, WARPED_HYPHAL_SOIL);
        tag(BlockTags.FLOWER_POTS).add(POTTED_DEAD_BUSH);
        tag(BlockTags.SOUL_SPEED_BLOCKS).add(REFINED_SOUL_SAND, REFINED_SOUL_SOIL, CRIMSON_HYPHAL_SOIL, WARPED_HYPHAL_SOIL);
        addForgeBlockTags();

        for (BlockGroup<?> group : BlockGroup.getBlockGroups()) {
            group.fillBlockTags(this::tag);
        }
    }

    private void addForgeBlockTags() {
        tag(Tags.Blocks.OBSIDIAN).add(SOUL_OBSIDIAN);
        tag(Tags.Blocks.STONE).add(SOUL_STONE, DECAYED_STONE);
    }

    @Override
    public String getName() {
        return "Block Tags: " + Soullery.MOD_ID;
    }
}
