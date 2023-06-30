package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.util.Arena;
import lych.soullery.util.PositionCalculators;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface EASPlacement {
    @Nullable
    Vector3d doCalculatePositionToSummon(EnchanterEntity enchanter, LivingEntity target);

    @Nullable
    default Vector3d calculatePositionToSummon(EnchanterEntity enchanter, LivingEntity target) {
        Arena arena = enchanter.getArena();
        BlockPos pos = arena.tryFind(() -> new BlockPos(doCalculatePositionToSummon(enchanter, target)));
        if (pos != null) {
            boolean ground = false;
            for (int dy = -5; dy <= 0; dy++) {
                if (enchanter.level.getBlockState(pos.above(dy)).getMaterial().blocksMotion()) {
                    ground = true;
                    break;
                }
            }
            boolean top = false;
            if (ground) {
                return new Vector3d(pos.getX() + 0.5, PositionCalculators.down(pos.getX(), pos.getY(), pos.getZ(), enchanter.level), pos.getZ() + 0.5);
            } else {
                for (int dy = 1; dy <= 5; dy++) {
                    if (enchanter.level.getBlockState(pos.above(dy)).getMaterial().blocksMotion()) {
                        top = true;
                        break;
                    }
                }
            }
            return new Vector3d(pos.getX() + 0.5, (top ? PositionCalculators.up(pos.getX(), pos.getY(), pos.getZ(), enchanter.level) : pos.getY()), pos.getZ() + 0.5);
        }
        return null;
    }
}
