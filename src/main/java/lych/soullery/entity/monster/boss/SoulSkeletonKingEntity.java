package lych.soullery.entity.monster.boss;

import com.google.common.collect.ImmutableList;
import lych.soullery.entity.ai.goal.CastingSpellGoal;
import lych.soullery.entity.ai.goal.boss.SoulSkeletonKingGoals.ShootGoal;
import lych.soullery.entity.ai.goal.boss.SoulSkeletonKingGoals.SummonGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.monster.IPurifiable;
import lych.soullery.entity.monster.SoulSkeletonEntity;
import lych.soullery.extension.fire.Fires;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.item.ModItems;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.mixin.IEntityMixin;
import lych.soullery.world.gen.biome.sll.SLLayer;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.stream.Stream;

import static lych.soullery.entity.iface.ISpellCastable.SpellType.NONE;

public class SoulSkeletonKingEntity extends AbstractSkeletonKingEntity implements IPurifiable {
    private static final DataParameter<Boolean> DATA_PURIFIED = EntityDataManager.defineId(SoulSkeletonKingEntity.class, DataSerializers.BOOLEAN);
    public static final SpellType SUMMON = SpellType.create(1, 0.2, 0.95, 1);
    public static final SpellType SHOOT = SpellType.create(2, 0, 0.5, 0.8);
    private static final ImmutableList<SpellType> POSSIBLE_SPELLS = Stream.of(NONE, SUMMON, SHOOT).sorted(Comparator.naturalOrder()).collect(ImmutableList.toImmutableList());
    private final ServerBossInfo bossInfo = new ServerBossInfo(getDisplayName(), BossInfo.Color.WHITE, BossInfo.Overlay.PROGRESS);
    private boolean updatedWeapon;

    public SoulSkeletonKingEntity(EntityType<? extends SoulSkeletonKingEntity> type, World world) {
        super(type, world);
        xpReward = 200;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PURIFIED, false);
    }

    @Override
    public boolean isPurified() {
        return entityData.get(DATA_PURIFIED);
    }

    @Override
    public void setPurified(boolean purified) {
        entityData.set(DATA_PURIFIED, purified);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractSkeletonEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 200)
                .add(Attributes.FOLLOW_RANGE, 24)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new CastingSpellGoal<>(this));
        goalSelector.addGoal(3, Goals.of(new MeleeAttackGoal(this, 1.1, true)).executeIf(this::canMelee).get());
        goalSelector.addGoal(4, new SummonGoal(this));
        goalSelector.addGoal(5, new ShootGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, SoulSkeletonKingEntity.class, SoulSkeletonEntity.class).setAlertOthers());
    }

    public boolean canMelee() {
        return updatedWeapon;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (isLowHealth() && !level.isClientSide() && !updatedWeapon) {
            ItemStack sword = new ItemStack(ModItems.REFINED_SOUL_METAL_SWORD);
            ReinforcementHelper.addReinforcement(sword, Reinforcements.SOUL_SKELETON, 1);
            sword.enchant(Enchantments.FIRE_ASPECT, 1);
            sword.enchant(Enchantments.SHARPNESS, 2);
            setItemInHand(Hand.MAIN_HAND, sword);
            setCastingSpell(NONE);
            updatedWeapon = true;
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (updatedWeapon) {
            bossInfo.setColor(BossInfo.Color.RED);
        } else {
            bossInfo.setColor(BossInfo.Color.BLUE);
        }
        bossInfo.setPercent(getHealth() / getMaxHealth());
    }

    @Override
    public void setCastingSpell(SpellType type) {
        if (canMelee()) {
            return;
        }
        super.setCastingSpell(type);
    }

    @Override
    protected void onSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        super.onSpawn(world, instance, reason, data, compoundNBT);
        if (world.getBiomeName(blockPosition()).map(SLLayer::getId).map(SLLayer::isPure).orElse(false)) {
            setPurified(true);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (target.isOnFire() && isPurified()) {
                ((IEntityMixin) target).setFireOnSelf(Fires.PURE_SOUL_FIRE);
            }
            return true;
        }
        return false;
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSoundEvents.SOUL_SKELETON_STEP.get();
    }

    @Override
    protected float getEquipmentDropChance(EquipmentSlotType slotType) {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    protected SpellType getCurrentSpellOnClient() {
        return POSSIBLE_SPELLS.get(getSpellId());
    }

    @Override
    public float getDamageMultiplier() {
        return 1;
    }

    @Override
    public SoundEvent getCastingSoundEvent() {
        return SoundEvents.ILLUSIONER_CAST_SPELL;
    }

    public boolean isLowHealth() {
        return getHealth() < getMaxHealth() * 0.25f;
    }

    @Override
    public void setCustomName(@Nullable ITextComponent component) {
        super.setCustomName(component);
        if (component != null) {
            bossInfo.setName(component);
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

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("UpdatedWeapon", updatedWeapon);
        compoundNBT.putBoolean("Purified", isPurified());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        updatedWeapon = compoundNBT.getBoolean("UpdatedWeapon");
        setPurified(compoundNBT.getBoolean("Purified"));
    }
}
