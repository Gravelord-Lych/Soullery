package lych.soullery.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

public class LakeSoulLava extends Placement<ChanceConfig> {
    public LakeSoulLava(Codec<ChanceConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, ChanceConfig config, BlockPos pos) {
        if (random.nextInt(config.chance) == 0) {
            int x = random.nextInt(16) + pos.getX();
            int z = random.nextInt(16) + pos.getZ();
            int y = random.nextInt(helper.getGenDepth());
            return Stream.of(new BlockPos(x, y, z));
        } else {
            return Stream.empty();
        }
    }
}
