package lych.soullery.entity.monster;

import lych.soullery.entity.ai.goal.LaserAttackGoal;
import lych.soullery.entity.iface.ILaserAttacker;
import lych.soullery.extension.laser.LaserData;
import lych.soullery.util.Lasers;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class WandererEntity extends MonsterEntity implements ILaserAttacker {
    private static final DataParameter<Boolean> DATA_ATTACKING = EntityDataManager.defineId(WandererEntity.class, DataSerializers.BOOLEAN);
    private static final float LASER_DAMAGE = 8;
    private final LaserData data = new LaserData.Builder()
            .color(Color.CYAN)
            .predicate(Lasers.monster(this), LaserData.DEFAULT_DURABILITY / 2)
            .build();

    public WandererEntity(EntityType<? extends WandererEntity> wanderer, World world) {
        super(wanderer, world);
        setPathfindingMalus(PathNodeType.LAVA, 4);
        setPathfindingMalus(PathNodeType.DANGER_FIRE, 0);
        setPathfindingMalus(PathNodeType.DAMAGE_FIRE, 0);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ATTACKING, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.FOLLOW_RANGE, 20);
    }

    @Override
    protected float getBlockSpeedFactor() {
        if (onSoulSpeedBlock()) {
            return 1;
        }
        return super.getBlockSpeedFactor();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(2, new LaserAttackGoal<>(this, 1, 40, 12));
        goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.9));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(6, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    public LaserData getLaserData(LivingEntity target) {
        return data;
    }

    @Override
    public float getLaserDamage(LivingEntity target) {
        return LASER_DAMAGE;
    }

    @Override
    public boolean isAttacking() {
        return entityData.get(DATA_ATTACKING);
    }

    @Override
    public void setAttacking(boolean attacking) {
        entityData.set(DATA_ATTACKING, attacking);
    }

    @Override
    public void playLaserSound() {
        playSound(ModSoundEvents.WANDERER_LASER.get(), 1, 1 + random.nextFloat() * 0.1f);
    }

    @Override
    public Vector3d getAttackerPosition() {
        return new Vector3d(getX(), getEyeY(), getZ());
    }

    @Override
    public Vector3d getTargetPosition(LivingEntity target) {
        return target.getBoundingBox().getCenter();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.WANDERER_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.WANDERER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.WANDERER_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(ModSoundEvents.WANDERER_STEP.get(), 0.15f, 1);
    }
}
