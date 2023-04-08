package lych.soullery.extension.laser;

import com.google.common.collect.ImmutableList;
import lych.soullery.util.Utils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LaserAttackResult {
    private final List<LivingEntity> passedEntities;
    @Nullable
    private final BlockPos passedBlockPos;
    @Nullable
    private final Vector3d passedPosition;
    private final LaserData data;
    private final World world;

    public LaserAttackResult(@Nullable Vector3d passedPosition, LaserData data, World world) {
        this(Collections.emptyList(), Utils.applyIfNonnull(passedPosition, BlockPos::new), passedPosition, data, world);
    }

    public LaserAttackResult(List<LivingEntity> passedEntities, @Nullable BlockPos passedBlockPos, @Nullable Vector3d passedPosition, LaserData data, World world) {
        this.passedEntities = ImmutableList.copyOf(passedEntities);
        this.passedBlockPos = passedBlockPos;
        this.passedPosition = passedPosition;
        this.data = data;
        this.world = world;
    }

    public List<LivingEntity> getPassedEntities() {
        return passedEntities;
    }

    public LaserData getData() {
        return data;
    }

    public World getWorld() {
        return world;
    }

    public Optional<BlockPos> getLastHitBlock() {
        return Optional.ofNullable(passedBlockPos);
    }

    public Optional<Vector3d> getLastHitPos() {
        return Optional.ofNullable(passedPosition);
    }
}
