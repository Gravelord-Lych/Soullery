package lych.soullery.entity.monster.voidwalker;

import com.google.common.collect.ImmutableList;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.BowAttackGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.RetreatGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.SpawnRainOfArrowGoal;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.VoidwalkerRandomWalkingGoal;
import lych.soullery.entity.iface.ISpellCastable;
import lych.soullery.entity.projectile.EtherealArrowEntity;
import lych.soullery.item.ModItems;
import lych.soullery.util.mixin.IAbstractArrowEntityMixin;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import java.util.List;

import static lych.soullery.entity.iface.ISpellCastable.SpellType.NONE;

public class VoidArcherEntity extends SpellCastingVoidwalkerEntity implements ISpellCastable, IRangedAttackMob {
    public static final SpellType RAIN_OF_ARROW = SpellType.create(1, 0.2, 0.6, 0.65);
    public static final SpellType RAIN_OF_ENHANCED_ARROW = SpellType.create(1, 0.85, 0.2, 0.25);
    private static final ImmutableList<SpellType> POSSIBLE_SPELLS = ImmutableList.of(NONE, RAIN_OF_ARROW);
    private static final ImmutableList<SpellType> POSSIBLE_SPELLS_ELITE = ImmutableList.of(NONE, RAIN_OF_ENHANCED_ARROW);

    public VoidArcherEntity(EntityType<? extends VoidArcherEntity> type, World world) {
        super(type, world);
    }

    @Override
    public EntityPredicate customizeTargetConditions(EntityPredicate targetConditions) {
        return targetConditions;
    }

    @Override
    public boolean canCreateWeapon() {
        return true;
    }

    @Override
    public ItemStack createWeapon() {
        return new ItemStack(ModItems.SOUL_BOW);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new SpawnRainOfArrowGoal(this));
        goalSelector.addGoal(3, new BowAttackGoal(this, 1, this::getAttackInterval, 18));
        goalSelector.addGoal(4, new RetreatGoal(this, 150));
        goalSelector.addGoal(5, new VoidwalkerRandomWalkingGoal(this, 0.88));
        goalSelector.addGoal(6, new LookRandomlyGoal(this));
    }

    private int getAttackInterval() {
        switch (getTier()) {
            case PARAGON:
                return 35;
            case ELITE:
                return 40;
            case EXTRAORDINARY:
                return 25;
            case ORDINARY:
            default:
                return 30;
        }
    }

    @Override
    protected List<SpellType> getPossibleSpells() {
        return getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? POSSIBLE_SPELLS_ELITE : POSSIBLE_SPELLS;
    }

    @Override
    protected void onSetSneakTarget(Vector3d sneakTarget) {
        super.onSetSneakTarget(sneakTarget);
        if (isUsingItem()) {
            stopUsingItem();
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {
        setItemInHand(Hand.MAIN_HAND, createWeapon());
    }

    @Override
    public SoundEvent getCastingSoundEvent() {
//      TODO : sound
        return SoundEvents.ILLUSIONER_CAST_SPELL;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        for (int i = 0; i < (getTier() == VoidwalkerTier.EXTRAORDINARY ? 3 : 2); i++) {
            EtherealArrowEntity arrow = new EtherealArrowEntity(level, this);
            if (getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY)) {
                arrow.setEnhanced(true);
            }
            if (getTier().strongerThan(VoidwalkerTier.ORDINARY)) {
                ((IAbstractArrowEntityMixin) arrow).setEnhancedLevel(getTier().strongerThan(VoidwalkerTier.ELITE) ? 2 : 1);
            }
            arrow.setEnchantmentEffectsFromEntity(this, power);
            arrow.setBaseDamage(getAttributeValue(Attributes.ATTACK_DAMAGE));
            double tx = target.getX() - getX();
            double ty = target.getY(0.33) - arrow.getY();
            double tz = target.getZ() - getZ();
            double dis = MathHelper.sqrt(tx * tx + ty * ty + ty * tz);
            arrow.shoot(tx, ty + dis * 0.15, tz, 1.6f, 8);
            level.addFreshEntity(arrow);
        }
    }

    @Override
    protected void doStrengthenSelf(VoidwalkerTier tier, VoidwalkerTier oldTier, DifficultyInstance difficulty) {
        strengthenSelfByDefault(tier);
        switch (tier) {
            case PARAGON:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
                break;
            case ELITE:
            case EXTRAORDINARY:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
                break;
            default:
        }
    }
}
