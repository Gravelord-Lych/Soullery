package lych.soullery.entity.ai.goal;

import lych.soullery.entity.monster.raider.EngineerEntity;
import lych.soullery.entity.monster.raider.RedstoneMortarEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static lych.soullery.util.EntityUtils.checkGoalInstantiationServerside;

public class BombardGoal extends MultiRangedAttackGoal<RedstoneMortarEntity> {
    private final ServerWorld level;
    private BlockPos bedPos;
    private Mode mode;

    public BombardGoal(RedstoneMortarEntity mob, double speedModifier, IntSupplier attackInterval, Supplier<Float> attackRadius, IntSupplier attackTimes, IntSupplier attackIntervalOfPerAttack) {
        super(mob, speedModifier, attackInterval, attackRadius, attackTimes, attackIntervalOfPerAttack);
        checkGoalInstantiationServerside(mob);
        level = (ServerWorld) mob.level;
    }

    public BombardGoal(RedstoneMortarEntity mob, double speedModifier, IntSupplier attackIntervalMin, IntSupplier attackIntervalMax, Supplier<Float> attackRadius, IntSupplier attackIntervalOfPerAttack, IntSupplier attackTimes) {
        super(mob, speedModifier, attackIntervalMin, attackIntervalMax, attackRadius, attackIntervalOfPerAttack, attackTimes);
        checkGoalInstantiationServerside(mob);
        level = (ServerWorld) mob.level;
    }

    @Override
    public boolean canUse() {
        EngineerEntity owner = mob.getOwner();
        if (owner != null && owner.hasActiveRaid()) {
            if (this.bedPos == null) {
                findBed(owner);
            }
            if (this.bedPos != null) {
                mode = Mode.DESTROY;
                return true;
            }
        }
        boolean canUse = super.canUse();
        if (canUse) {
            mode = Mode.HURT;
            return true;
        }
        return false;
    }

    private void findBed(EngineerEntity owner) {
        Raid raid = owner.getCurrentRaid();
        Objects.requireNonNull(raid);
        List<PointOfInterest> pois = level.getPoiManager().getInRange(type -> type == PointOfInterestType.HOME, raid.getCenter(), 64, PointOfInterestManager.Status.ANY).collect(Collectors.toList());
        pois.stream()
                .map(PointOfInterest::getPos)
                .filter(pos -> level.getBlockState(pos).getBlock() instanceof BedBlock)
                .filter(pos -> mob.distanceToSqr(Vector3d.atCenterOf(pos)) >= 10 * 10)
                .findAny()
                .ifPresent(bedPos -> this.bedPos = bedPos);
    }

    @Override
    public void tick() {
        if (mob.getOwner() != null && mob.getOwner().getCurrentRaid() == null) {
            bedPos = null;
            mode = Mode.HURT;
        } else {
            checkBed();
        }
        if (mode == Mode.DESTROY) {
            double distanceSqrToTarget = mob.distanceToSqr(bedPos.getX(), bedPos.getY(), bedPos.getZ());
            seeTime++;
            mob.getLookControl().setLookAt(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 30, 30);
            if (--attackTime == 0) {
                float distanceProportion = MathHelper.sqrt(distanceSqrToTarget) / attackRadius.get();
                if (finishedAttack()) {
                    attackTime = MathHelper.floor(MathHelper.lerp(distanceProportion, attackIntervalMin.getAsInt(), attackIntervalMax.getAsInt()));
                    currentAttackTimes = 0;
                } else {
                    mob.bombard(Vector3d.atBottomCenterOf(bedPos));
                    attackTime = attackIntervalOfPerAttack.getAsInt();
                    currentAttackTimes++;
                }
            } else if (attackTime < 0) {
                float distanceProportion = MathHelper.sqrt(distanceSqrToTarget) / attackRadius.get();
                attackTime = MathHelper.floor(MathHelper.lerp(distanceProportion, attackIntervalMin.getAsInt(), attackIntervalMax.getAsInt()));
            }
        } else {
            super.tick();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void stop() {
        super.stop();
        checkBed();
    }

    private void checkBed() {
        if (bedPos == null || !(level.getBlockState(bedPos).getBlock() instanceof BedBlock)) {
            EngineerEntity owner = mob.getOwner();
            if (owner != null) {
                findBed(owner);
            }
            if (bedPos == null) {
                mode = Mode.HURT;
            }
        }
    }

    private enum Mode {
        HURT,
        DESTROY
    }
}
