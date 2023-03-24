package lych.soullery.entity.monster.boss.esv;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.entity.ai.goal.AttackMainTargetGoal;
import lych.soullery.entity.ai.goal.boss.SoulControllerGoals.GuideBoltsGoal;
import lych.soullery.entity.ai.goal.boss.SoulControllerGoals.ShootPursuersGoal;
import lych.soullery.entity.ai.goal.boss.SoulControllerGoals.SpawnVoidwalkersGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.ai.phase.PhaseManager;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SoulControllerEntity extends MonsterEntity implements ESVMob {
    private static final double BASE_MAX_HEALTH = 600;
    private static final int MAX_TIER = 5;
    private static final Int2DoubleMap HEALTH_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(BASE_MAX_HEALTH).range(2).value(900).range(3).value(1300).range(4).value(1800).range(5).value(2500).build();

    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS);
    private final PhaseManager<Phase> manager;
    private int tier = 1;
    @Nullable
    private Vector3d sneakTarget;
    @Nullable
    private UUID mainTarget;

    public SoulControllerEntity(EntityType<? extends SoulControllerEntity> type, World world) {
        super(type, world);
        moveControl = new MovementHelperController();
        manager = new PhaseManager<>(Phase::values);
        if (!level.isClientSide()) {
            registerPhasedGoals();
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
                .add(Attributes.FOLLOW_RANGE, 100)
                .add(Attributes.MOVEMENT_SPEED, 0.3);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(6, new RandomWalkingGoal(this, 0.8));
        goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(7, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, ESVMob.class));
        targetSelector.addGoal(2, new AttackMainTargetGoal(this, false, this::getMainTargetAsEntity));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, false));
        targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, ESVMob::nonESVMob));
    }

    protected void registerPhasedGoals() {
        goalSelector.addGoal(2, Goals.of(new ShootPursuersGoal(this)).phased(manager, Phase.SHOOT_PURSUERS).get());
        goalSelector.addGoal(2, Goals.of(new SpawnVoidwalkersGoal(this)).phased(manager, Phase.SPAWN_VOIDWALKERS).get());
        goalSelector.addGoal(2, Goals.of(new GuideBoltsGoal(this)).phased(manager, Phase.GUIDE_BOLTS).get());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide()) {
            bossInfo.setPercent(MathHelper.clamp(getHealth() / getMaxHealth(), 0, 1));
            bossInfo.setColor(BossInfo.Color.PURPLE);
        }
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        List<? extends PlayerEntity> players = world.players();
        tier = MathHelper.clamp(players.size(), 1, MAX_TIER);
        handlePreSpawnModifiers(new ArrayList<>(players));
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    private void handlePreSpawnModifiers(List<? extends PlayerEntity> players) {
        ModifiableAttributeInstance maxHealth = EntityUtils.getAttribute(this, Attributes.MAX_HEALTH);
        maxHealth.setBaseValue(HEALTH_MAP.get(MathHelper.clamp(getTier(), 1, MAX_TIER)));
        Collections.shuffle(players);
        setMainTarget(players.get(0).getUUID());
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Tier", tier);
        if (getSneakTarget() != null) {
            Vector3d et = getSneakTarget();
            compoundNBT.put("EtheTarget", newDoubleList(et.x, et.y, et.z));
        }
        ListNBT listNBT = new ListNBT();
        compoundNBT.put("Shadows", listNBT);
        if (getMainTarget() != null) {
            compoundNBT.putUUID("MainTarget", getMainTarget());
        }
        compoundNBT.put("PhaseManager", manager.save());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Tier")) {
            tier = compoundNBT.getInt("Tier");
        }
        if (compoundNBT.contains("EtheTarget", Constants.NBT.TAG_LIST)) {
            ListNBT etn = compoundNBT.getList("EtheTarget", Constants.NBT.TAG_DOUBLE);
            setSneakTarget(new Vector3d(etn.getDouble(0), etn.getDouble(1), etn.getDouble(2)));
        }
        if (compoundNBT.contains("MainTarget")) {
            setMainTarget(compoundNBT.getUUID("MainTarget"));
        }
        if (compoundNBT.contains("PhaseManager")) {
            manager.load(compoundNBT.getCompound("PhaseManager"));
        }
    }

    @Override
    public void setCustomName(@Nullable ITextComponent name) {
        super.setCustomName(name);
        if (name != null) {
            bossInfo.setName(name);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayerEntity player) {
        super.startSeenByPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayerEntity player) {
        super.stopSeenByPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Nullable
    public Vector3d getSneakTarget() {
        return sneakTarget;
    }

    public void setSneakTarget(@Nullable Vector3d sneakTarget) {
        this.sneakTarget = sneakTarget;
        setNoGravity(sneakTarget != null);
        noPhysics = sneakTarget != null;
    }

    @Nullable
    public LivingEntity getMainTargetAsEntity() {
        if (level.isClientSide() || getMainTarget() == null) {
            return null;
        }
        Entity entity = ((ServerWorld) level).getEntity(getMainTarget());
        return EntityUtils.isAlive(entity) && entity instanceof LivingEntity ? (LivingEntity) entity : null;
    }

    @Nullable
    public UUID getMainTarget() {
        return mainTarget;
    }

    public void setMainTarget(UUID mainTarget) {
        this.mainTarget = mainTarget;
    }

    public class MovementHelperController extends MovementController {
        public MovementHelperController() {
            super(SoulControllerEntity.this);
            speedModifier = 1;
        }

        @Override
        public void tick() {
            if (getSneakTarget() != null) {
                Vector3d sneakTarget = getSneakTarget();
                Objects.requireNonNull(sneakTarget);
                operation = Action.WAIT;
                double tx = sneakTarget.x - getX();
                double ty = sneakTarget.y - getY();
                double tz = sneakTarget.z - getZ();
                Vector3d vec = new Vector3d(tx, ty, tz);
                double length = vec.length();
                if (length < getBoundingBox().getSize()) {
                    operation = Action.WAIT;
                    setDeltaMovement(getDeltaMovement().scale(0.5));
                    setSneakTarget(null);
                } else {
                    float yRot = (float) (MathHelper.atan2(tz, tx) * 180 / Math.PI) - 90;
                    float xRot = (float) -(MathHelper.atan2(ty, MathHelper.sqrt(tx * tx + tz * tz)) * 180 / Math.PI);
                    SoulControllerEntity.this.yRot = rotlerp(SoulControllerEntity.this.yRot, yRot, 90);
                    SoulControllerEntity.this.xRot = rotlerp(SoulControllerEntity.this.xRot, xRot, 90);
                    SoulControllerEntity.this.yBodyRot = rotlerp(SoulControllerEntity.this.yBodyRot, yRot, 90);
                    setDeltaMovement(vec.scale(speedModifier * getAttributeValue(Attributes.MOVEMENT_SPEED) / length));
                }
            } else {
                super.tick();
            }
        }

        public void setSpeedModifier(double speedModifier) {
            this.speedModifier = speedModifier;
        }
    }

    public enum Phase implements IIdentifiableEnum {
        SHOOT_PURSUERS,
        SPAWN_VOIDWALKERS,
        GUIDE_BOLTS
    }
}
