package lych.soullery.world.gen.config;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Random;

import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public class SoulTowerConfig implements IFeatureConfig {
    public static final Codec<SoulTowerConfig> CODEC = RecordCodecBuilder.create(SoulTowerConfig::makeCodec);
    private final int minHeight;
    private final int maxHeight;
    private final Selection<BlockState> stairBlocks;
    private final Selection<BlockState> outerTowerBlocks;
    private final Selection<BlockState> innerTowerBlocks;
    private final Selection<BlockState> slabBlocks;
    private final Selection<BlockState> glassBlocks;

    public SoulTowerConfig(int minHeight, int maxHeight, Selection<BlockState> stairBlocks, Selection<BlockState> outerTowerBlocks, Selection<BlockState> innerTowerBlocks, Selection<BlockState> slabBlocks, Selection<BlockState> glassBlocks) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.stairBlocks = stairBlocks;
        this.outerTowerBlocks = outerTowerBlocks;
        this.innerTowerBlocks = innerTowerBlocks;
        this.slabBlocks = slabBlocks;
        this.glassBlocks = glassBlocks;
    }

    public SoulTowerConfig(CompoundNBT compoundNBT) {
        this(
                compoundNBT.getInt("MinHeight"),
                compoundNBT.getInt("MaxHeight"),
                WorldUtils.load(compoundNBT.getList("StairBlocks", TAG_COMPOUND)),
                WorldUtils.load(compoundNBT.getList("OuterTowerBlocks", TAG_COMPOUND)),
                WorldUtils.load(compoundNBT.getList("InnerTowerBlocks", TAG_COMPOUND)),
                WorldUtils.load(compoundNBT.getList("SlabBlocks", TAG_COMPOUND)),
                WorldUtils.load(compoundNBT.getList("GlassBlocks", TAG_COMPOUND))
        );
    }

    private static App<RecordCodecBuilder.Mu<SoulTowerConfig>, SoulTowerConfig> makeCodec(RecordCodecBuilder.Instance<SoulTowerConfig> instance) {
        return instance.group(Codec.INT.fieldOf("min_height").forGetter(SoulTowerConfig::getMinHeight),
                Codec.INT.fieldOf("max_height").forGetter(SoulTowerConfig::getMaxHeight),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("stair_blocks").forGetter(SoulTowerConfig::getStairBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("outer_tower_blocks").forGetter(SoulTowerConfig::getOuterTowerBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("inner_tower_blocks").forGetter(SoulTowerConfig::getInnerTowerBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("slab_blocks").forGetter(SoulTowerConfig::getSlabBlocks),
                WorldUtils.BLOCK_SELECTION_CODEC.fieldOf("glass_blocks").forGetter(SoulTowerConfig::getGlassBlocks)).apply(instance, SoulTowerConfig::new);
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getRandomHeight(Random random) {
        return getMinHeight() + random.nextInt(getMaxHeight() - getMinHeight() + 1);
    }

    public Selection<BlockState> getStairBlocks() {
        return stairBlocks;
    }

    public Selection<BlockState> getOuterTowerBlocks() {
        return outerTowerBlocks;
    }

    public Selection<BlockState> getInnerTowerBlocks() {
        return innerTowerBlocks;
    }

    public Selection<BlockState> getSlabBlocks() {
        return slabBlocks;
    }

    public Selection<BlockState> getGlassBlocks() {
        return glassBlocks;
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("MinHeight", getMinHeight());
        compoundNBT.putInt("MaxHeight", getMaxHeight());
        compoundNBT.put("StairBlocks", WorldUtils.save(getStairBlocks()));
        compoundNBT.put("OuterTowerBlocks", WorldUtils.save(getOuterTowerBlocks()));
        compoundNBT.put("InnerTowerBlocks", WorldUtils.save(getInnerTowerBlocks()));
        compoundNBT.put("SlabBlocks", WorldUtils.save(getSlabBlocks()));
        compoundNBT.put("GlassBlocks", WorldUtils.save(getGlassBlocks()));
        return compoundNBT;
    }
}
