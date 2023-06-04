package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableSet;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.extension.fire.Fires;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EnergizedBlazeReinforcement extends TickableReinforcement {
    private static final int MAX_LEVEL = 5;
    private static final int SECONDS_ON_FIRE = 1;
    private static final int BASE_RADIUS = 1;
    private static final int BASE_INFERNO_RADIUS = -1;
    private static final double RADIUS_STEP = 1;
    private static final double INFERNO_RADIUS_STEP = 0.5;

    public EnergizedBlazeReinforcement() {
        super(ModEntities.ENERGIZED_BLAZE);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {
        level = Math.min(level, MAX_LEVEL);

        double radius = getFireRadius(level);
        List<LivingEntity> entities = entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(radius));
        removeInvalidEntities(entity, radius, entities);
        double hellRadius = getInfernoRadius(level);
        List<LivingEntity> innerEntities = hellRadius <= 0 ? Collections.emptyList() : entity.level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(hellRadius));
        removeInvalidEntities(entity, hellRadius, innerEntities);
        entities.removeAll(innerEntities);

        for (Entity inner : innerEntities) {
            inner.setSecondsOnFire(SECONDS_ON_FIRE);
            ((IEntityMixin) inner).setFireOnSelf(Fires.INFERNO);
        }
        for (Entity outer : entities) {
            outer.setSecondsOnFire(SECONDS_ON_FIRE);
        }
    }

    private static void removeInvalidEntities(LivingEntity center, double radius, List<LivingEntity> entities) {
        entities.removeIf(e -> e.distanceToSqr(center) > radius * radius);
        if (ESVMob.isESVMob(center)) {
            entities.removeIf(ESVMob::isESVMob);
        } else {
            entities.removeIf(e -> center instanceof IMob == e instanceof IMob);
        }
    }

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {}

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return ImmutableSet.copyOf(EquipmentSlotType.values());
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    private static double getFireRadius(int level) {
        return BASE_RADIUS + level * RADIUS_STEP;
    }

    private static double getInfernoRadius(int level) {
        return BASE_INFERNO_RADIUS + level * INFERNO_RADIUS_STEP;
    }

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }
}
