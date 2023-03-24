package lych.soullery.entity.monster.boss.esv;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.Utils;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SoulCrystalEntity extends MonsterEntity implements ESVMob {
    private static final double BASE_MAX_HEALTH = 600;
    private static final int MAX_TIER = 5;
    private static final double FOLLOW_RANGE = 120;
    private static final Int2DoubleMap HEALTH_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(BASE_MAX_HEALTH).range(2).value(900).range(3).value(1300).range(4).value(1800).range(5).value(2500).build();
    private static final Predicate<Entity> CAN_ATTACK = EntityPredicates.ATTACK_ALLOWED.and(ESVMob::nonESVMob);
    private static final AxisAlignedBB TARGET_RANGE = new AxisAlignedBB(-FOLLOW_RANGE, -256, -FOLLOW_RANGE, FOLLOW_RANGE, 256, FOLLOW_RANGE);
    private final TargetManager targets = new TargetManager();
    private int time;
    private int tier = 1;
    private boolean valid;

    public SoulCrystalEntity(EntityType<? extends SoulCrystalEntity> type, World world) {
        super(type, world);
        time = random.nextInt(100000);
        setNoGravity(true);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
    }

    private AxisAlignedBB getTargetRange() {
        return TARGET_RANGE.move(position());
    }

    @Override
    public void tick() {
        super.tick();
        time++;
        if (!level.isClientSide()) {
            getTargets().update();
        }
    }

    public TargetManager getTargets() {
        return targets;
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return getTargets().getMainTarget();
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (getTargets().setMainTarget(target)) {
            super.setTarget(target);
        }
    }

    private static boolean isInvalidTarget(@Nullable LivingEntity target) {
        return !EntityUtils.isAlive(target);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
//        if (reason == SpawnReason.COMMAND || world.getLevel().dimension() != ModDimensions.ESV) {
//            setValid(false);
//        }
        setValid(true);
        tier = calculateTier();
        handlePreSpawnModifiers();
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    private int calculateTier() {
        return MathHelper.clamp(level.players().size(), 1, MAX_TIER);
    }

    private void handlePreSpawnModifiers() {
        ModifiableAttributeInstance maxHealth = EntityUtils.getAttribute(this, Attributes.MAX_HEALTH);
        double baseValue = maxHealth.getBaseValue();
        maxHealth.setBaseValue(HEALTH_MAP.get(MathHelper.clamp(getTier(), 1, MAX_TIER)));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Tier", tier);
        compoundNBT.putBoolean("Valid", valid);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Tier")) {
            tier = compoundNBT.getInt("Tier");
        }
        valid = compoundNBT.getBoolean("Valid");

    }

    public int getTier() {
        return tier;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getTime() {
        return time;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    public class TargetManager implements Iterable<LivingEntity> {
        private final SortedSet<LivingEntity> targets = new TreeSet<>(compareByDistance());
        @Nullable
        private LivingEntity mainTarget;

        private TargetManager() {}

        public void update() {
            targets.removeIf(SoulCrystalEntity::isInvalidTarget);
            targets.addAll(level.getEntitiesOfClass(LivingEntity.class, getTargetRange(), CAN_ATTACK));
            if (mainTarget == null && !targets.isEmpty()) {
                mainTarget = targets.first();
            }
            if (EntityUtils.isAlive(mainTarget)) {
                targets.add(mainTarget);
            } else {
                mainTarget = null;
            }
        }

        public SortedSet<LivingEntity> getTargets() {
            return new TreeSet<>(targets);
        }

        public SortedSet<PlayerEntity> getTargetPlayers() {
            return targets.stream().filter(entity -> entity instanceof PlayerEntity).map(entity -> (PlayerEntity) entity).collect(Utils.toTreeSet(compareByDistance()));
        }

        private <T extends Entity> Comparator<T> compareByDistance() {
            return Comparator.comparingDouble(SoulCrystalEntity.this::distanceToSqr);
        }

        @Nullable
        public LivingEntity getMainTarget() {
            return mainTarget;
        }

        public boolean setMainTarget(@Nullable LivingEntity mainTarget) {
            if (mainTarget == null || CAN_ATTACK.test(mainTarget)) {
                this.mainTarget = mainTarget;
                if (mainTarget != null) {
                    targets.add(mainTarget);
                }
                return true;
            }
            return false;
        }

        public boolean isEmpty() {
            return targets.isEmpty();
        }

        @NotNull
        @Override
        public Iterator<LivingEntity> iterator() {
            return targets.iterator();
        }

        public List<LivingEntity> extract(long count) {
            return targets.stream().sorted(compareByDistance()).limit(count).collect(Collectors.toList());
        }
    }

    public enum Phase implements IIdentifiableEnum {
        PURSUERS
    }
}
