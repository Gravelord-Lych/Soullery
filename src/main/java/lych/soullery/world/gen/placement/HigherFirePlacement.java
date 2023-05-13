package lych.soullery.world.gen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.placement.FirePlacement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class HigherFirePlacement extends FirePlacement {
    public HigherFirePlacement(Codec<FeatureSpreadConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> place(Random random, FeatureSpreadConfig config, BlockPos pos) {
        List<BlockPos> list = new ArrayList<>();

        for (int i = 0; i < random.nextInt(random.nextInt(config.count().sample(random)) + 1) + 1; i++) {
            int x = random.nextInt(16) + pos.getX();
            int z = random.nextInt(16) + pos.getZ();
            int y = random.nextInt(252) + 4;
            list.add(new BlockPos(x, y, z));
        }

        return list.stream();
    }
}
