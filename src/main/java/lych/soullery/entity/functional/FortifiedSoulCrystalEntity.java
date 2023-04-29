package lych.soullery.entity.functional;

import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.SoulSkeletonEntity;
import lych.soullery.util.PositionCalculators;
import lych.soullery.world.gen.feature.PlateauSpikeFeature;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class FortifiedSoulCrystalEntity extends SoulCrystalEntity {
    private static final int MAX_ABSORBING_TICKS = 10;
    private static final int MAX_EXPLOSION_TICKS = 60;
    private static final int MAX_EXPLOSION_COOLDOWN = 40;
    private static final float EXPLOSION_POWER = 3;
    private static final float FIRE_PROBABILITY = 0.2f;
    private static final DataParameter<Integer> DATA_ABSORBING_TARGET = EntityDataManager.defineId(FortifiedSoulCrystalEntity.class, DataSerializers.INT);

    private int absorbingTicks;
    private int explosionTicks = -1;
    private int explosionCooldown;

    public FortifiedSoulCrystalEntity(EntityType<? extends FortifiedSoulCrystalEntity> type, World world) {
        super(type, world);
    }

    public FortifiedSoulCrystalEntity(World world, double x, double y, double z) {
        this(ModEntities.FORTIFIED_SOUL_CRYSTAL, world);
        setPos(x, y ,z);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ABSORBING_TARGET, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide()) {
            SoulSkeletonEntity skeleton = getAbsorbingSkeleton();
            if (skeleton != null) {
                if (absorbingTicks++ > MAX_ABSORBING_TICKS) {
                    skeleton.remove();
                    setAbsorbingSkeleton(null);
                    findSkeleton();
                }
            } else if ((tickCount & 3) == 0) {
                findSkeleton();
            }
            if (explosionTicks > 0) {
                explosionTicks--;
            } else if (explosionTicks == 0) {
                if (getBeamTarget() == null) {
                    if (ConfigHelper.shouldFailhard()) {
                        throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + "BeamTarget is null when trying to explode");
                    } else {
                        LOGGER.error("BeamTarget is null when trying to explode, that should be impossible");
                    }
                } else {
                    BlockPos beamTarget = getBeamTarget();
                    level.explode(this, beamTarget.getX() + 0.5, beamTarget.getY(), beamTarget.getZ() + 0.5, EXPLOSION_POWER, random.nextFloat() <= FIRE_PROBABILITY, Explosion.Mode.NONE);
                    setBeamTarget(null);
                    explosionCooldown = MAX_EXPLOSION_COOLDOWN;
                }
            }
            if (explosionCooldown > 0) {
                setBeamTarget(null);
                explosionCooldown--;
            } else if (explosionTicks < 0 && (tickCount & 7) == 0) {
                level.players().stream().filter(player -> player.canSee(this)).filter(EntityPredicates.ATTACK_ALLOWED).min(Comparator.comparingDouble(this::distanceToSqr)).ifPresent(player -> {
                    BlockPos pos = player.blockPosition();
                    Block block = level.getBlockState(pos).getBlock();
                    for (int i = 0; i < 5; i++) {
                        BlockPos newPos = pos.offset(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
                        if (level.getBlockState(newPos).getBlock() == block) {
                            pos = newPos;
                            break;
                        }
                    }
                    int y = PositionCalculators.down(pos.getX(), pos.getY(), pos.getZ(), level);
                    if (Math.abs(pos.getY() - y) <= 3) {
                        pos = new BlockPos(pos.getX(), y, pos.getZ());
                    }
                    setBeamTarget(pos);
                });
            }
        }
    }

    private void findSkeleton() {
        if (!level.isClientSide()) {
            SoulSkeletonEntity nearestSkeleton = level.getNearestEntity(SoulSkeletonEntity.class,
                    new EntityPredicate().allowSameTeam().selector(entity -> entity != getAbsorbingSkeleton()),
                    null,
                    getX(),
                    getY(),
                    getZ(),
                    getBoundingBox().inflate(PlateauSpikeFeature.RADIUS));
            if (nearestSkeleton != null) {
                setAbsorbingSkeleton(nearestSkeleton);
            }
        }
    }

    @Nullable
    public SoulSkeletonEntity getAbsorbingSkeleton() {
        Entity entity = level.getEntity(entityData.get(DATA_ABSORBING_TARGET));
        return entity instanceof SoulSkeletonEntity ? (SoulSkeletonEntity) entity : null;
    }

    @Override
    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        super.setBeamTarget(beamTarget);
        if (beamTarget != null) {
            explosionTicks = MAX_EXPLOSION_TICKS;
        } else {
            explosionTicks = -1;
        }
    }

    public void setAbsorbingSkeleton(@Nullable SoulSkeletonEntity absorbingSkeleton) {
        if (absorbingSkeleton == null) {
            entityData.set(DATA_ABSORBING_TARGET, -1);
        } else {
            entityData.set(DATA_ABSORBING_TARGET, absorbingSkeleton.getId());
        }
        absorbingTicks = 0;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("ExplosionTicks", explosionTicks);
        compoundNBT.putInt("ExplosionCooldown", explosionCooldown);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("ExplosionTicks", Constants.NBT.TAG_INT)) {
            explosionTicks = compoundNBT.getInt("ExplosionTicks");
        }
        explosionCooldown = compoundNBT.getInt("ExplosionCooldown");
    }
}
