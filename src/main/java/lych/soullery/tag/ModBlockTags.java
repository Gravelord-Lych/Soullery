package lych.soullery.tag;

import lych.soullery.Soullery;
import lych.soullery.api.TagNames;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags.IOptionalNamedTag;

public final class ModBlockTags {
    public static final IOptionalNamedTag<Block> HYPHAL_SOUL_SOIL = tag(TagNames.HYPHAL_SOUL_SOIL.getPath());
    public static final IOptionalNamedTag<Block> INFERNO_BASE_BLOCKS = tag(TagNames.INFERNO_BASE_BLOCKS.getPath());
    public static final IOptionalNamedTag<Block> POISONOUS_FIRE_BASE_BLOCKS = tag(TagNames.POISONOUS_FIRE_BASE_BLOCKS.getPath());
    public static final IOptionalNamedTag<Block> PURE_SOUL_FIRE_BASED_BLOCKS = tag(TagNames.PURE_SOUL_FIRE_BASED_BLOCKS.getPath());
    public static final IOptionalNamedTag<Block> SOUL_RABBIT_SPAWNABLE_BLOCKS = tag(TagNames.SOUL_RABBIT_SPAWNABLE_BLOCKS.getPath());
    public static final IOptionalNamedTag<Block> SOULIFIED_BUSH_PLACEABLE_BLOCKS = tag(TagNames.SOULIFIED_BUSH_PLACEABLE_BLOCKS.getPath());

    private ModBlockTags() {}

    private static IOptionalNamedTag<Block> tag(String name) {
        return BlockTags.createOptional(Soullery.prefix(name));
    }
}
