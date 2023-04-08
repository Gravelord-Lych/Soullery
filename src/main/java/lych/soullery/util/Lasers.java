package lych.soullery.util;

import lych.soullery.entity.iface.ILaserAttacker;
import lych.soullery.extension.laser.LaserAttackResult;
import lych.soullery.extension.laser.LaserHitPredicate;
import lych.soullery.extension.laser.LaserHitType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.awt.*;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static lych.soullery.network.LaserNetwork.*;

public final class Lasers {

    private Lasers() {}

    public static void hurtHitEntities(LaserAttackResult result, float amount) {
        hurtHitEntities(result, ModDamageSources.LASER.setScalesWithDifficulty(), amount);
    }

    public static void hurtHitEntities(LaserAttackResult result, Entity attacker, float amount) {
        hurtHitEntities(result, ModDamageSources.laser(attacker).setScalesWithDifficulty(), amount);
    }

    public static void hurtHitEntities(LaserAttackResult result, DamageSource source, float amount) {
        acceptHitEntities(result, entity -> entity.hurt(source, amount));
    }

    public static void acceptHitEntities(LaserAttackResult result, Consumer<? super LivingEntity> consumer) {
        result.getPassedEntities().forEach(consumer);
    }

    public static void renderLaser(LaserAttackResult result, Entity laserOwner, int renderTickCount) {
        renderLaser(result, laserOwner, renderTickCount, -1);
    }

    public static void renderLaser(LaserAttackResult result, Entity laserOwner, int renderTickCount, int customWidth) {
        renderLaser(result, laserOwner, renderTickCount, customWidth, true);
    }

    public static void renderLaser(LaserAttackResult result, Entity laserOwner, int renderTickCount, int customWidth, boolean fixedDestination) {
        renderCustomColoredLaser(result, laserOwner, result.getData().getColor(), renderTickCount, customWidth, fixedDestination);
    }

    public static void renderCustomColoredLaser(LaserAttackResult result, Entity laserOwner, Color color, int renderTickCount, int customWidth, boolean fixedDestination) {
        if (!result.getLastHitPos().isPresent()) {
            return;
        }
        Vector3d src = laserOwner instanceof ILaserAttacker ? ((ILaserAttacker) laserOwner).getAttackerPosition() : new Vector3d(laserOwner.getX(), laserOwner.getEyeY(), laserOwner.getZ());
        Vector3d destPos = result.getLastHitPos().get();
        Vector3d dest = fixedDestination ? destPos : result.getLastHitPos().get();
        INSTANCE.send(PacketDistributor.ALL.noArg(), new LaserPacket(new LaserRenderData(src, dest, color, laserOwner.getId(), customWidth, renderTickCount)));
    }

    public static BiFunction<? super Vector3d, ? super World, ? extends LivingEntity> entities() {
        return entities(4);
    }

    public static BiFunction<? super Vector3d, ? super World, ? extends LivingEntity> entities(int radius) {
        return (vec, world) -> world.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(vec.subtract(radius, radius, radius), vec.add(radius, radius, radius))).stream().filter(entity -> entity.getBoundingBox().contains(vec)).findFirst().orElse(null);
    }

    public static BiFunction<? super Vector3d, ? super World, ? extends BlockState> blockState() {
        return (vec, world) -> world.getBlockState(new BlockPos(vec));
    }

    public static BiFunction<? super Vector3d, ? super World, Unit> any() {
        return (vec, world) -> Unit.INSTANCE;
    }

    public static boolean solid(BlockState state) {
        return state.getMaterial().isSolid();
    }

    public static boolean liquid(BlockState state) {
        return state.getMaterial().isLiquid();
    }

    public static LaserHitPredicate<LivingEntity> monster() {
        return LaserHitPredicate.by(LaserHitType.ENTITY)
                .makerFunction(entities())
                .predicate(entity -> entity instanceof IMob)
                .noResult()
                .build(25);
    }

    public static LaserHitPredicate<LivingEntity> monster(MobEntity laserAttacker) {
        return monster(laserAttacker, true);
    }

    public static LaserHitPredicate<LivingEntity> monster(MobEntity laserAttacker, boolean noResult) {
        LaserHitPredicate.Builder<LivingEntity> builder = LaserHitPredicate.by(LaserHitType.ENTITY)
                .makerFunction(entities())
                .predicate(entity -> entity instanceof IMob && !(laserAttacker.getTarget() instanceof IMob));
        if (noResult) {
            builder.noResult();
        }
        return builder.build(25);
    }

    public static LaserHitPredicate<Object> air() {
        return LaserHitPredicate.by(LaserHitType.AIR)
                .makerFunction(any())
                .predicate(Objects::nonNull)
                .build(Integer.MAX_VALUE);
    }

    public static LaserHitPredicate<BlockState> fluid() {
        return LaserHitPredicate.by(LaserHitType.FLUID)
                .makerFunction(blockState())
                .predicate(Lasers::liquid)
                .build(100);
    }

    public static LaserHitPredicate<LivingEntity> entity() {
        return entity(false);
    }

    public static LaserHitPredicate<LivingEntity> entity(boolean noResult) {
        LaserHitPredicate.Builder<LivingEntity> builder = LaserHitPredicate.by(LaserHitType.ENTITY)
                .makerFunction(entities())
                .predicate(Objects::nonNull);
        if (noResult) {
            builder.noResult();
        }
        return builder.build(50);
    }

    public static LaserHitPredicate<BlockState> block() {
        return LaserHitPredicate.by(LaserHitType.BLOCK)
                .makerFunction(blockState())
                .predicate(Lasers::solid)
                .build(1);
    }
}
