package lych.soullery.entity.monster.boss.enchanter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public enum EASPlacements implements EASPlacement {
    MIDPOINT {
        @Override
        public Vector3d doCalculatePositionToSummon(EnchanterEntity enchanter, LivingEntity target) {
            double amount = enchanter.getRandom().nextDouble() * 0.2 + 0.4;
            return new Vector3d(MathHelper.lerp(amount, enchanter.getX(), target.getX()) + enchanter.getRandom().nextGaussian() * 0.5,
                    MathHelper.lerp(amount, enchanter.getY(), target.getY()),
                    MathHelper.lerp(amount, enchanter.getZ(), target.getZ()) + enchanter.getRandom().nextGaussian() * 0.5);
        }
    },
    CENTER {
        @Override
        public Vector3d doCalculatePositionToSummon(EnchanterEntity enchanter, LivingEntity target) {
            float randomAngle = (float) (enchanter.getRandom().nextFloat() * 2 * Math.PI);
            return enchanter.position().add(new Vector3d(MathHelper.cos(randomAngle), 0, MathHelper.sin(randomAngle)).scale(enchanter.getRandom().nextDouble() * 1.5 + 3));
        }
    },
    TARGET {
        @Override
        public Vector3d doCalculatePositionToSummon(EnchanterEntity enchanter, LivingEntity target) {
            float randomAngle = (float) (enchanter.getRandom().nextFloat() * 2 * Math.PI);
            return target.position().add(new Vector3d(MathHelper.cos(randomAngle), 0, MathHelper.sin(randomAngle)).scale(enchanter.getRandom().nextDouble() + 1.5));
        }
    }
}
