package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.advancements.ModCriteriaTriggers;
import lych.soullery.block.ModBlocks;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.ai.phase.PhaseManager;
import lych.soullery.entity.monster.boss.IHasKillers;
import lych.soullery.item.ModItems;
import lych.soullery.util.*;
import lych.soullery.world.gen.structure.piece.SkyCityPieces;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EnchanterEntity extends MonsterEntity implements IHasKillers {
    private static final DataParameter<Boolean> DATA_ETHEREALLY_INVULNERABLE = EntityDataManager.defineId(EnchanterEntity.class, DataSerializers.BOOLEAN);
    private static final int MAX_DAMAGE_RECEIVED = 30;
    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.GREEN, BossInfo.Overlay.PROGRESS);
    private final EASTypePickerList pl = EASTypePickerList.create(this);
    private final EnchanterSkillList sl = EnchanterSkillList.create(this);
    private final PhaseManager<Phase> manager;
    private final List<BlockPos> disassemblers = new ArrayList<>();
    private final Set<UUID> killers = new HashSet<>();
    private final Set<UUID> invalidChallengers = new HashSet<>();
    private final Counter killCount = new Counter();
    private boolean disassemblersUsed;
    private Arena arena = Arena.INFINITY;
    private int hurtTimes;
    private boolean countHurtTimes;

    public EnchanterEntity(EntityType<? extends EnchanterEntity> type, World world) {
        super(type, world);
        xpReward = 300;
        if (ConfigHelper.shouldFailhard()) {
            pl.selfCheck();
        }
        manager = new PhaseManager<>(Phase::values);
        if (!level.isClientSide()) {
            registerPhasedGoals();
        }
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(Math.max(health, getHealth() - MAX_DAMAGE_RECEIVED));
    }

    @Override
    public void kill() {
        super.setHealth(0);
    }

    protected void registerPhasedGoals() {
        goalSelector.addGoal(3, Goals.of(new EnchanterGoals.HideGoal(this)).phased(manager, Phase.HIDE).get());
        goalSelector.addGoal(3, Goals.of(new EnchanterGoals.AttackGoal(this)).phased(manager, Phase.ATTACK).get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ETHEREALLY_INVULNERABLE, false);
    }

    public boolean isEthereallyInvulnerable() {
        return entityData.get(DATA_ETHEREALLY_INVULNERABLE);
    }

    public void setEthereallyInvulnerable(boolean ei) {
        entityData.set(DATA_ETHEREALLY_INVULNERABLE, ei);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.MAX_HEALTH, 75)
                .add(Attributes.FOLLOW_RANGE, SkyCityPieces.INNER_BOSS_ROOM_SIZE + 10);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, LivingEntity.class, 12, 1.1, 1.4, this::shouldAvoid));
        goalSelector.addGoal(8, new RandomWalkingGoal(this, 1));
        goalSelector.addGoal(9, new LookAtGoal(this, PlayerEntity.class, 12));
        goalSelector.addGoal(10, new LookRandomlyGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, EnchantedArmorStandEntity.class, EnchanterEntity.class));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    private boolean shouldAvoid(LivingEntity entity) {
        if (!EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity)) {
            return false;
        }
        return entity instanceof PlayerEntity || entity instanceof IronGolemEntity || entity == getTarget();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!level.isClientSide() && !disassemblersUsed) {
            iterateKillers(level, player -> EntityUtils.isSurvival(player) && !invalidChallengers.contains(player.getUUID()), ModCriteriaTriggers.DESTROYER_II::trigger);
        }
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        if (deathTime == 100) {
            remove();
            for (int i = 0; i < 20; ++i) {
                double xSpeed = random.nextGaussian() * 0.02D;
                double ySpeed = random.nextGaussian() * 0.02D;
                double zSpeed = random.nextGaussian() * 0.02D;
                level.addParticle(ParticleTypes.POOF, getRandomX(1), getRandomY(), getRandomZ(1), xSpeed, ySpeed, zSpeed);
            }
        } else {
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1, 0, 0);
        }
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossInfo.setPercent(getHealth() / getMaxHealth());
        if (isEthereallyInvulnerable()) {
            bossInfo.setColor(BossInfo.Color.BLUE);
        } else {
            bossInfo.setColor(BossInfo.Color.GREEN);
        }
        for (Iterator<BlockPos> iterator = disassemblers.iterator(); iterator.hasNext(); ) {
            BlockPos pos = iterator.next();
            if (!level.getBlockState(pos).is(ModBlocks.DISASSEMBLER)) {
                iterator.remove();
                disassemblersUsed = true;
            }
        }
        markNonSurvivalPlayersInvalid(level, invalidChallengers);
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
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        setItemInHand(Hand.MAIN_HAND, new ItemStack(ModItems.ENCHANTING_WAND));
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    public int getSkillMessageColor() {
        return 0x00B78E;
    }

    public EnchantedArmorStandEntity summonEAS(EASTypePicker picker, LivingEntity target) {
        return summonEAS(picker.getType(), picker.getRandomPos(this, target));
    }

    public EnchantedArmorStandEntity summonEAS(@Nullable EASType type, Vector3d pos) {
        if (level.isClientSide()) {
            throw new UnsupportedOperationException();
        }
        ServerWorld level = (ServerWorld) this.level;
        EnchantedArmorStandEntity eas = ModEntities.ENCHANTED_ARMOR_STAND.create(level);
        if (type != null) {
            eas.setSpecialType(type);
        }
        eas.setOwner(this);
        eas.moveTo(pos.x, pos.y, pos.z, random.nextFloat() * 360, 0);
        eas.setSpawnInvul();
        eas.finalizeSpawn(level, level.getCurrentDifficultyAt(eas.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
        level.addFreshEntity(eas);
        EntityUtils.spawnAnimServerside(eas, level);
        return eas;
    }

    public void summonEASNearby(int radius, int count) {
        summonEASNearby(null, radius, count);
    }

    public void summonEASNearby(@Nullable EASType type, int radius, int count) {
        BlockPos pos = blockPosition();
        Vector3d srcPos = Vector3d.atBottomCenterOf(pos);
        if (!arena.isInfiniteArena()) {
            double hd = arena.minHorizontalDistanceToEdge(srcPos);
            if (!arena.isInsideHorizontally(srcPos) && hd <= 5 || hd <= radius) {
                srcPos = arena.moveTowardsHorizontalCenter(srcPos, !arena.isInsideHorizontally(srcPos) ? radius : radius - hd);
            }
        }
        Vector3d targetPos = srcPos.add(0, 0, radius);
        for (double degree = 0; degree < 360; degree += 360.0 / count) {
            Vector3d rotated = Vectors.rotateTo(targetPos, srcPos, Math.toRadians(degree), true);
            summonEAS(type, rotated);
        }
    }

    public EASTypePickerList getEASTypePickerList() {
        return pl;
    }

    public EnchanterSkillList getEnchanterSkillList() {
        return sl;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.put("PhaseManager", manager.save());
        compoundNBT.putInt("HurtTimes", hurtTimes);
        countHurtTimes = compoundNBT.getBoolean("CountHurtTimes");
        compoundNBT.putBoolean("EthereallyInvulnerable", isEthereallyInvulnerable());
        compoundNBT.putBoolean("DisassemblersUsed", disassemblersUsed);
        compoundNBT.put("Arena", getArena().save());
        Utils.saveBlockPosList(compoundNBT, disassemblers, "Disassemblers");
        Utils.saveCounter(compoundNBT, killCount, "KillCount");
        Utils.saveUUIDSet(compoundNBT, killers, "Killers");
        Utils.saveUUIDSet(compoundNBT, invalidChallengers, "InvalidChallengers");
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        manager.load(compoundNBT.getCompound("PhaseManager"));
        hurtTimes = compoundNBT.getInt("HurtTimes");
        setCountHurtTimes(compoundNBT.getBoolean("CountHurtTimes"));
        disassemblersUsed = compoundNBT.getBoolean("DisassemblersUsed");
        setEthereallyInvulnerable(compoundNBT.getBoolean("EthereallyInvulnerable"));
        if (compoundNBT.contains("Arena")) {
            setArena(Arena.load(compoundNBT.getCompound("Arena")));
        }
        Utils.loadBlockPosList(compoundNBT, disassemblers, "Disassemblers");
        Utils.loadCounter(compoundNBT, killCount, "KillCount");
        Utils.loadUUIDSet(compoundNBT, killers, "Killers");
        Utils.loadUUIDSet(compoundNBT, invalidChallengers, "InvalidChallengers");
    }

    public void addKill(ServerPlayerEntity player) {
        if (EntityUtils.isSurvival(player) && killCount.addAndGet(player.getUUID()) == 150) {
            ModCriteriaTriggers.DESTROYER.trigger(player);
        }
    }

    public void addDisassemblers(Collection<BlockPos> pos) {
        disassemblers.addAll(pos);
    }

    public void resetHurtTimes() {
        hurtTimes = 0;
    }

    public List<EnchantedArmorStandEntity> getEASInsideArena() {
        AxisAlignedBB bb;
        if (arena.isInfiniteArena()) {
            bb = getBoundingBox().inflate(SkyCityPieces.INNER_BOSS_ROOM_SIZE / 2.0, SkyCityPieces.BOSS_ROOM_HEIGHT / 2.0, SkyCityPieces.INNER_BOSS_ROOM_SIZE / 2.0).deflate(1);
        } else {
            bb = arena.bb();
        }
        return level.getEntitiesOfClass(EnchantedArmorStandEntity.class, bb, eas -> eas.isOwnedBy(this));
    }

    public int countEAS() {
        return getEASInsideArena().size();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!source.isBypassInvul() && (isEthereallyInvulnerable() || countHurtTimes && hurtTimes >= 3)) {
            return false;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt && countHurtTimes) {
            hurtTimes++;
        }
        return hurt;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void setCountHurtTimes(boolean countHurtTimes) {
        this.countHurtTimes = countHurtTimes;
        if (!countHurtTimes) {
            resetHurtTimes();
        }
    }

    public int[] getVanishingSlotIndex(ServerPlayerEntity target) {
        int size = PlayerInventory.getSelectionSize();
        int selected = target.inventory.selected;
        if (!PlayerInventory.isHotbarSlot(selected)) {
            selected = getRandom().nextInt(size);
        }
        int ex;
        if (getRandom().nextBoolean()) {
            ex = (selected + 4) % size;
        } else {
            ex = (selected - 4) % size;
        }
        ex = wrap(ex, size);
        return new int[]{selected, ex};
    }

    private static int wrap(int i, int mod) {
        while (i < 0) {
            i += mod;
        }
        while (i >= mod) {
            i -= mod;
        }
        return i;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return getRandom().nextBoolean() ? ModSoundEvents.ENCHANTER_COUGH.get() : ModSoundEvents.ENCHANTER_YAWN.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.ENCHANTER_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.ENCHANTER_DEATH.get();
    }

    @Override
    public Set<UUID> getKillers() {
        return killers;
    }

    public enum Phase implements IIdentifiableEnum {
        HIDE,
        ATTACK
    }
}
