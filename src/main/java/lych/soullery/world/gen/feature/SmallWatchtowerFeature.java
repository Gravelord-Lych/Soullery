package lych.soullery.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.selection.Selection;
import lych.soullery.world.gen.config.WatchtowerConfig;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static lych.soullery.util.WorldUtils.placeUp;

public class SmallWatchtowerFeature extends Feature<WatchtowerConfig> {
    protected final int radius;
    protected final List<Vector3i> positions;

    public SmallWatchtowerFeature(Codec<WatchtowerConfig> codec, int radius) {
        super(codec);
        this.radius = radius;
        this.positions = ImmutableList.of(new Vector3i(radius, 0, radius), new Vector3i(radius, 0, -radius), new Vector3i(-radius, 0, radius), new Vector3i(-radius, 0, -radius));
    }

    @Override
    public boolean place(ISeedReader reader, ChunkGenerator generator, Random random, BlockPos pos, WatchtowerConfig config) {
        BlockPos base = getValidBase(reader, pos.below());
        if (base == null) {
            return false;
        }
        pos = base.above();

        int height = config.getRandomHeight(random);
        Selection<BlockState> columnBlocks = config.getColumnBlocks();
        Selection<BlockState> floorBlocks = config.getFloorBlocks();
        Selection<BlockState> barBlocks = config.getBarBlocks();
        Selection<BlockState> topBlocks = config.getTopBlocks();
//      Do not place too many watchtowers in a small area.
        if (!check(reader, pos.above(height * 2 / 3), columnBlocks)) {
            return false;
        }

        placeColumns(reader, random, pos, height, columnBlocks);

        pos = pos.above(height);

        placeEdgeBlocks(reader, random, pos, columnBlocks, barBlocks, topBlocks);
        placeCentralFloor(reader, random, pos, floorBlocks);
        placeUpperColumns(reader, random, pos, columnBlocks);
        placeTop(reader, random, pos, topBlocks);
        spawnScout(reader, random, pos.above());
        placeLantern(reader, random, pos, height);

        return true;
    }

    protected boolean check(IWorldReader reader, BlockPos pos, Selection<BlockState> columnBlocks) {
        int checkExtraRadius = 6;
        for (BlockPos posIn : BlockPos.betweenClosed(pos.offset(-radius - checkExtraRadius, -checkExtraRadius / 2, -radius - 1), pos.offset(radius + checkExtraRadius, checkExtraRadius / 2, radius + checkExtraRadius))) {
            if (columnBlocks.stream().map(BlockState::getBlock).anyMatch(reader.getBlockState(posIn)::is)) {
                return false;
            }
        }
        return true;
    }

    protected void placeColumns(ISeedReader reader, Random random, BlockPos pos, int height, Selection<BlockState> columnBlocks) {
        for (Vector3i vector : positions) {
            BlockPos offset = pos.offset(vector);
            int i;
            for (i = 0; reader.getBlockState(offset).getMaterial().isSolid() && i < height - 2; i++) {
                offset = offset.above();
            }
            placeUp(reader, random, offset, height - i, columnBlocks::getRandom, this::setBlock);
        }
    }

    protected void placeEdgeBlocks(ISeedReader reader, Random random, BlockPos pos, Selection<BlockState> columnBlocks, Selection<BlockState> barBlocks, Selection<BlockState> topBlocks) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (abs(x) == radius || abs(z) == radius) {
                    setBlock(reader, pos.offset(x, 0, z), topBlocks.getRandom(random));
                    BlockState bar;
                    if (abs(x) == radius && abs(z) == radius) {
                        bar = columnBlocks.getRandom(random).setValue(WallBlock.UP, true);
                        bar = WorldUtils.discussCornerPosition(bar, x, z, radius);
                    } else {
                        bar = barBlocks.getRandom(random);
                        if (bar.getBlock() instanceof WallBlock) {
                            bar = bar.setValue(WallBlock.UP, false);
                        }
                        bar = WorldUtils.discussEdgePosition(bar, x, z, radius);
                    }
                    setBlock(reader, pos.offset(x, 1, z), bar);
                }
            }
        }
    }

    protected void placeCentralFloor(ISeedReader reader, Random random, BlockPos pos, Selection<BlockState> floorBlocks) {
        for (int x = -radius + 1; x <= radius - 1; x++) {
            for (int z = -radius + 1; z <= radius - 1; z++) {
                setBlock(reader, pos.offset(x, 0, z), floorBlocks.getRandom(random));
            }
        }
    }

    protected void placeLantern(ISeedReader reader, Random random, BlockPos pos, int height) {
        BlockPos chainStart = pos.below();
        int chainSize = Math.min(1 + random.nextInt(height / 2), height - 2);
        for (int ny = 0; ny < chainSize; ny++) {
            BlockPos below = chainStart.below(ny);
            if (reader.getBlockState(below.below()).getMaterial().isSolid()) {
                chainSize = ny + 1;
                break;
            }
            setBlock(reader, below, Blocks.CHAIN.defaultBlockState());
        }
        setBlock(reader, chainStart.below(chainSize), Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true));
    }

    protected void spawnScout(ISeedReader reader, Random random, BlockPos pos) {
        for (int i = 0; i < getScoutCount(random); i++) {
            EntityType<? extends AbstractVoidwalkerEntity> scoutType = getScoutType(random, i);
            AbstractVoidwalkerEntity voidwalker = scoutType.create(reader.getLevel());
            BlockPos scoutPos = pos;
            if (radius > 1) {
                int rx = random.nextInt(radius);
                int rz = random.nextInt(radius);
                scoutPos = scoutPos.offset(rx, 0, rz);
            }
            voidwalker.setScout(true);
            voidwalker.moveTo(scoutPos, random.nextFloat() * 360, 0);
            voidwalker.finalizeSpawn(reader, reader.getCurrentDifficultyAt(scoutPos), SpawnReason.STRUCTURE, null, null);
            reader.addFreshEntity(voidwalker);
        }
    }

    protected EntityType<? extends AbstractVoidwalkerEntity> getScoutType(Random random, int i) {
        EntityType<? extends AbstractVoidwalkerEntity> scoutType;
        float randomValue = random.nextFloat();
        if (randomValue < 0.7f) {
            scoutType = ModEntities.VOID_ARCHER;
        } else if (randomValue < 0.85f) {
            scoutType = ModEntities.VOIDWALKER;
        } else {
            scoutType = ModEntities.VOID_DEFENDER;
        }
        return scoutType;
    }

    protected int getScoutCount(Random random) {
        return 1;
    }

    @Nullable
    protected BlockPos getValidBase(ISeedReader reader, BlockPos base) {
        if (match(reader, base)) {
            return base;
        }
        for (int scale = 1; scale < 3; scale++) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                base = base.offset(direction.getStepX() * scale, direction.getStepY() * scale, direction.getStepZ() * scale);
                if (match(reader, base)) {
                    return base;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    protected boolean match(IWorldReader reader, BlockPos base) {
        for (Vector3i vec : positions) {
            BlockPos offset = base.offset(vec);
            if (reader.getBlockState(offset).isAir() || reader.getBlockState(offset).getMaterial().isLiquid()) {
                return false;
            }
        }
        return true;
    }

    protected void placeTop(ISeedReader reader, Random random, BlockPos pos, Selection<BlockState> topBlocks) {}

    protected void placeUpperColumns(ISeedReader reader, Random random, BlockPos pos, Selection<BlockState> columnBlocks) {}
}
