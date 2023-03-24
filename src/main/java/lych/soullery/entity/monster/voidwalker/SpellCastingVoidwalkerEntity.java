package lych.soullery.entity.monster.voidwalker;

import lych.soullery.entity.ai.goal.CastingSpellGoal;
import lych.soullery.entity.iface.ISpellCastable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import java.util.List;

public abstract class SpellCastingVoidwalkerEntity extends AbstractVoidwalkerEntity implements ISpellCastable {
    private static final DataParameter<Integer> DATA_SPELL_CASTING_ID = EntityDataManager.defineId(SpellCastingVoidwalkerEntity.class, DataSerializers.INT);
    private SpellType currentSpell = SpellType.NONE;
    private int spellCastingTickCount;

    protected SpellCastingVoidwalkerEntity(EntityType<? extends SpellCastingVoidwalkerEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_SPELL_CASTING_ID, 0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new CastingSpellGoal<>(this));
    }

    @Override
    public boolean onSetTarget(LivingEntity target) {
        return false;
    }

    @Override
    public boolean isMeleeAttacker() {
        return false;
    }

    @Override
    public boolean isCastingSpell() {
        if (level.isClientSide()) {
            return getSpellId() > 0;
        }
        return spellCastingTickCount > 0;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getSpellCastingTickCount() > 0) {
            setSpellCastingTickCount(getSpellCastingTickCount() - 1);
        }
    }

    @Override
    public int getSpellCastingTickCount() {
        return spellCastingTickCount;
    }

    @Override
    public void setSpellCastingTickCount(int spellCastingTickCount) {
        this.spellCastingTickCount = spellCastingTickCount;
    }

    @Override
    public SpellType getCurrentSpell() {
        return level.isClientSide() ? getPossibleSpells().get(getSpellId()) : currentSpell;
    }

    protected abstract List<SpellType> getPossibleSpells();

    @Override
    public void setCastingSpell(SpellType type) {
        currentSpell = type;
        setSpellId(type.getId());
    }

    private int getSpellId() {
        return entityData.get(DATA_SPELL_CASTING_ID);
    }

    private void setSpellId(int id) {
        entityData.set(DATA_SPELL_CASTING_ID, id);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("SpellCastingTickCount", getSpellCastingTickCount());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        setSpellCastingTickCount(compoundNBT.getInt("SpellCastingTickCount"));
    }
}
