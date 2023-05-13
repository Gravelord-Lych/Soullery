package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.world.gen.config.WatchtowerConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;

import java.util.Random;

public class LargeWatchtowerFeature extends SmallWatchtowerFeature {
    private static final int ROOM_HEIGHT = 4;

    public LargeWatchtowerFeature(Codec<WatchtowerConfig> codec, int radius) {
        super(codec, radius);
    }

    @Override
    protected void placeTop(ISeedReader reader, Random random, BlockPos pos, Selection<BlockState> topBlocks) {
        for (BlockPos topPos : BlockPos.betweenClosed(pos.offset(-radius, ROOM_HEIGHT + 1, -radius), pos.offset(radius, ROOM_HEIGHT + 1, radius))) {
            setBlock(reader, topPos, topBlocks.getRandom(random));
        }
    }

    @Override
    protected void placeUpperColumns(ISeedReader reader, Random random, BlockPos pos, Selection<BlockState> columnBlocks) {
        WorldUtils.placeUp(reader, random, pos.offset(radius, 2, radius), ROOM_HEIGHT - 1, columnBlocks::getRandom, this::setBlock);
        WorldUtils.placeUp(reader, random, pos.offset(radius, 2, -radius), ROOM_HEIGHT - 1, columnBlocks::getRandom, this::setBlock);
        WorldUtils.placeUp(reader, random, pos.offset(-radius, 2, radius), ROOM_HEIGHT - 1, columnBlocks::getRandom, this::setBlock);
        WorldUtils.placeUp(reader, random, pos.offset(-radius, 2, -radius), ROOM_HEIGHT - 1, columnBlocks::getRandom, this::setBlock);
    }

    @Override
    protected int getScoutCount(Random random) {
        return 2 + random.nextInt(2);
    }

    @Override
    protected EntityType<? extends AbstractVoidwalkerEntity> getScoutType(Random random, int i) {
        if (i == 0) {
            return ModEntities.VOID_ARCHER;
        }
        if (i == 2) {
            return random.nextBoolean() ? ModEntities.VOIDWALKER : ModEntities.VOID_DEFENDER;
        }
        return super.getScoutType(random, i);
    }

    @Override
    protected void placeLantern(ISeedReader reader, Random random, BlockPos pos, int height) {
        BlockPos lanternPos = pos.above(ROOM_HEIGHT);
        setBlock(reader, lanternPos, Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }
}
