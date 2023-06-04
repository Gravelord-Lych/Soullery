package lych.soullery.api;

import net.minecraft.util.ResourceLocation;

public final class TagNames {
    /**
     * Mark blocks that are hyphal soul soils.
     */
    public static final ResourceLocation HYPHAL_SOUL_SOIL = new ResourceLocation(SoulleryAPI.MOD_ID, "hyphal_soul_soil");
    /**
     * Similar to {@link net.minecraft.tags.BlockTags#SOUL_FIRE_BASE_BLOCKS SOUL_FIRE_BASE_BLOCKS}<br>
     * Fire on these blocks will turn into soullery:inferno.
     */
    public static final ResourceLocation INFERNO_BASE_BLOCKS = new ResourceLocation(SoulleryAPI.MOD_ID, "inferno_base_blocks");
    /**
     * Similar to {@link net.minecraft.tags.BlockTags#SOUL_FIRE_BASE_BLOCKS SOUL_FIRE_BASE_BLOCKS}<br>
     * Fire on these blocks will turn into soullery:poisonous_fire.
     */
    public static final ResourceLocation POISONOUS_FIRE_BASE_BLOCKS = new ResourceLocation(SoulleryAPI.MOD_ID, "poisonous_fire_base_blocks");
    /**
     * Similar to {@link net.minecraft.tags.BlockTags#SOUL_FIRE_BASE_BLOCKS SOUL_FIRE_BASE_BLOCKS}<br>
     * Fire on these blocks will turn into soullery:pure_soul_fire.
     */
    public static final ResourceLocation PURE_SOUL_FIRE_BASED_BLOCKS = new ResourceLocation(SoulleryAPI.MOD_ID, "pure_soul_fire_based_blocks");
    /**
     * Mark soul lavas which will be rendered with special color. You'd better make fluids with this tag have {@link net.minecraft.tags.FluidTags#LAVA LAVA} tag.
     */
    public static final ResourceLocation SOUL_LAVA = new ResourceLocation(SoulleryAPI.MOD_ID, "soul_lava");
    /**
     * Soul Rabbits can spawn on these blocks.
     */
    public static final ResourceLocation SOUL_RABBIT_SPAWNABLE_BLOCKS = new ResourceLocation(SoulleryAPI.MOD_ID, "soul_rabbit_spawnable_blocks");
    /**
     * For blocks which soulified bush can be placed on.
     */
    public static final ResourceLocation SOULIFIED_BUSH_PLACEABLE_BLOCKS = new ResourceLocation(SoulleryAPI.MOD_ID, "soulified_bush_placeable_blocks");
    /**
     * Bosses who are tiered should have this tag.
     */
    public static final ResourceLocation TIERED_BOSS = new ResourceLocation(SoulleryAPI.MOD_ID, "tiered_boss");
    public static final ResourceLocation SOUL_DRAGON_IMMUNE = new ResourceLocation(SoulleryAPI.MOD_ID, "soul_dragon_immune");
    /**
     * Soul Extractors can at least extract one Soul Piece from a mob and additionally increase Soul Piece's drop probability by 50%.
     */
    public static final ResourceLocation SOUL_EXTRACTORS = new ResourceLocation(SoulleryAPI.MOD_ID, "soul_extractors");

    private TagNames() {}
}

