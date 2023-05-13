package lych.soullery.world.gen.feature;

import com.mojang.serialization.Codec;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.world.gen.config.StreetlightConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StreetlightFeature extends Feature<StreetlightConfig> {
    public StreetlightFeature(Codec<StreetlightConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, StreetlightConfig config) {
        int height = config.getRandomHeight(random);
        Selection<BlockState> selection = config.getRandomColumnType(random);;

        for (int i = 0; i < height - 1; i++) {
            setBlock(reader, pos.above(i), selection.getRandom(random));
        }

        BlockPos top = pos.above(height - 1);
        setBlock(reader, top, makeTop(selection.getRandom(random)));

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            setBlock(reader, top.offset(direction.getNormal()), selection.getRandom(random).setValue(WorldUtils.wallHeightByDirection(direction.getOpposite()), WallHeight.LOW));
            setBlock(reader, top.offset(direction.getNormal()).above(), Blocks.SOUL_TORCH.defaultBlockState());
        }

        return true;
    }

    @NotNull
    private static BlockState makeTop(BlockState columnBlockDefault) {
        return columnBlockDefault
                .setValue(WallBlock.UP, false)
                .setValue(WallBlock.EAST_WALL, WallHeight.LOW)
                .setValue(WallBlock.WEST_WALL, WallHeight.LOW)
                .setValue(WallBlock.NORTH_WALL, WallHeight.LOW)
                .setValue(WallBlock.SOUTH_WALL, WallHeight.LOW);
    }
}
