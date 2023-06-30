package lych.soullery.entity.monster.boss;

import lych.soullery.entity.iface.ISpellCastable;
import lych.soullery.entity.iface.ITieredBoss;
import lych.soullery.entity.iface.ITieredMob;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractSkeletonKingEntity extends AbstractSkeletonEntity implements ISpellCastable, ITieredBoss {
    private static final DataParameter<Integer> DATA_SPELL_CASTING_ID = EntityDataManager.defineId(AbstractSkeletonKingEntity.class, DataSerializers.INT);

    private final Set<UUID> killerUUIDSet = new HashSet<>();
    private int spellCastingTickCount;
    private int tier = ITieredMob.MIN_TIER;
    private boolean canModifyTier = true;
    private SpellType currentSpell = SpellType.NONE;

    protected AbstractSkeletonKingEntity(EntityType<? extends AbstractSkeletonKingEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SPELL_CASTING_ID, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(9, new WaterAvoidingRandomWalkingGoal(this, 1));
        goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(10, new LookRandomlyGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    public boolean isCastingSpell() {
        if (level.isClientSide()) {
            return getSpellId() > 0;
        }
        return spellCastingTickCount > 0;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Override
    public SpellType getCurrentSpell() {
        return level.isClientSide() ? getCurrentSpellOnClient() : currentSpell;
    }

    @OnlyIn(Dist.CLIENT)
    protected abstract SpellType getCurrentSpellOnClient();

    @Override
    public void setCastingSpell(SpellType type) {
        currentSpell = type;
        setSpellId(type.getId());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (spellCastingTickCount > 0) {
            spellCastingTickCount--;
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        if (!canModifyTier) {
            throw new UnsupportedOperationException("You can't modify tier now");
        }
        this.tier = MathHelper.clamp(tier, ITieredMob.MIN_TIER, ITieredMob.MAX_TIER);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    @Override
    public final ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        setTierAccordingToPlayers();
        canModifyTier = false;
        ILivingEntityData result = super.finalizeSpawn(world, instance, reason, data, compoundNBT);
        if (shouldClearSlots(world, instance, reason, data, compoundNBT)) {
            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
                setItemSlot(slotType, ItemStack.EMPTY);
            }
        }
        onSpawn(world, instance, reason, data, compoundNBT);
        return result;
    }

    protected void onSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {}

    protected boolean shouldClearSlots(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        return true;
    }

    @Override
    public void reassessWeaponGoal() {}

    @Override
    public Set<UUID> getKillerUUIDSet() {
        return killerUUIDSet;
    }

    @Override
    public int getSpellCastingTickCount() {
        return spellCastingTickCount;
    }

    @Override
    public void setSpellCastingTickCount(int spellCastingTickCount) {
        this.spellCastingTickCount = spellCastingTickCount;
    }

    protected int getSpellId() {
        return entityData.get(DATA_SPELL_CASTING_ID);
    }

    protected void setSpellId(int id) {
        entityData.set(DATA_SPELL_CASTING_ID, id);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("Tier", tier);
        compoundNBT.putBoolean("CanModifyTier", canModifyTier);
        compoundNBT.putInt("SpellTicks", spellCastingTickCount);
        saveKillers(compoundNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("Tier")) {
            tier = compoundNBT.getInt("Tier");
        }
        if (compoundNBT.contains("CanModifyTier")) {
            canModifyTier = compoundNBT.getBoolean("CanModifyTier");
        }
        spellCastingTickCount = compoundNBT.getInt("SpellTicks");
        loadKillers(compoundNBT);
    }
}
