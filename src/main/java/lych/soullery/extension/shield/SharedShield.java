package lych.soullery.extension.shield;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import lych.soullery.api.shield.ISharedShield;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class SharedShield implements ISharedShield {
    private final float absoluteDefense;
    private final float passiveDefense;
    private final int maxRegenInterval;
    private final float regenAmount;
    private final boolean consumable;
    private int regenInterval;
    private int invulnerableTicks;
    private float health;

    public SharedShield(float absoluteDefense, float passiveDefense) {
        this(absoluteDefense, passiveDefense, true);
    }

    public SharedShield(float absoluteDefense, float passiveDefense, boolean consumable) {
        this(absoluteDefense, passiveDefense, 0, 0, consumable);
    }

    public SharedShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount) {
        this(absoluteDefense, passiveDefense, maxRegenInterval, regenAmount, true);
    }

    public SharedShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount, boolean consumable) {
        Preconditions.checkArgument(absoluteDefense >= 0, "AbsoluteDefense should not be negative");
        Preconditions.checkArgument(passiveDefense >= 0, "PassiveDefense should not be negative");
        Preconditions.checkArgument(maxRegenInterval >= 0, "MaxRegenInterval should not be negative");
        Preconditions.checkArgument(regenAmount >= 0, "RegenAmount should not be negative");
        this.absoluteDefense = absoluteDefense;
        this.passiveDefense = passiveDefense;
        this.maxRegenInterval = maxRegenInterval;
        this.regenAmount = regenAmount;
        this.consumable = consumable;
        this.health = passiveDefense;
        this.regenInterval = maxRegenInterval;
    }

    public SharedShield(CompoundNBT compoundNBT) {
        this(compoundNBT.getFloat("SharedShield.AbsoluteDefense"), compoundNBT.getFloat("SharedShield.PassiveDefense"), compoundNBT.getInt("SharedShield.MaxRegenInterval"), compoundNBT.getFloat("SharedShield.RegenAmount"), compoundNBT.getBoolean("SharedShield.Consumable"));
        setHealth(compoundNBT.getFloat("SharedShield.Health"));
        setInvulnerableTicks(compoundNBT.getInt("SharedShield.InvulnerableTicks"));
        regenInterval = compoundNBT.getInt("SharedShield.RegenInterval");
    }

    @Override
    public void tick() {
        tickRegeneration();
        tickInvulnerability();
    }

    protected void tickRegeneration() {
        if (maxRegenInterval <= 0 || regenAmount <= 0) {
            return;
        }
        if (regenInterval > 0) {
            regenInterval--;
        } else {
            heal(regenAmount);
            regenInterval = maxRegenInterval;
        }
    }

    protected void tickInvulnerability() {
        if (invulnerableTicks > 0) {
            invulnerableTicks--;
        }
    }

    @Override
    public float getAbsoluteDefense() {
        return absoluteDefense;
    }

    @Override
    public float getPassiveDefense() {
        return passiveDefense;
    }

    @Override
    public float getMaxRegenInterval() {
        return maxRegenInterval;
    }

    @Override
    public float getRegenAmount() {
        return regenAmount;
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    public void setHealth(float health) {
        this.health = MathHelper.clamp(health, 0, passiveDefense);
    }

    @Override
    public void heal(float amount) {
        setHealth(getHealth() + Math.max(0, amount));
    }

    protected void actuallyHurt(float amount) {
        setHealth(getHealth() - amount);
    }

    @Override
    public float hurt(DamageSource source, float amount) {
        if (getInvulnerableTicks() > 0) {
            return -health;
        }
        amount = Math.max(0, amount - absoluteDefense);
        if (amount < health) {
            actuallyHurt(amount);
            return amount - health;
        }
        if (amount == health) {
            setHealth(0);
            return 0;
        }
        setHealth(0);
        return amount - health;
    }

    @Override
    public boolean canBeConsumed() {
        return consumable;
    }

    @Override
    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putFloat("SharedShield.AbsoluteDefense", absoluteDefense);
        compoundNBT.putFloat("SharedShield.PassiveDefense", passiveDefense);
        compoundNBT.putInt("SharedShield.MaxRegenInterval", maxRegenInterval);
        compoundNBT.putFloat("SharedShield.RegenAmount", regenAmount);
        compoundNBT.putFloat("SharedShield.Health", health);
        compoundNBT.putInt("SharedShield.RegenInterval", regenInterval);
        compoundNBT.putInt("SharedShield.InvulnerableTicks", invulnerableTicks);
        compoundNBT.putBoolean("SharedShield.Consumable", consumable);
        return compoundNBT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SharedShield)) return false;
        SharedShield that = (SharedShield) o;
        return Float.compare(that.getAbsoluteDefense(), getAbsoluteDefense()) == 0 && Float.compare(that.getPassiveDefense(), getPassiveDefense()) == 0 && getMaxRegenInterval() == that.getMaxRegenInterval() && Float.compare(that.getRegenAmount(), getRegenAmount()) == 0 && canBeConsumed() == that.canBeConsumed() && regenInterval == that.regenInterval && getInvulnerableTicks() == that.getInvulnerableTicks() && Float.compare(that.getHealth(), getHealth()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAbsoluteDefense(), getPassiveDefense(), getMaxRegenInterval(), getRegenAmount(), canBeConsumed(), regenInterval, getInvulnerableTicks(), getHealth());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("absoluteDefense", absoluteDefense)
                .add("passiveDefense", passiveDefense)
                .add("maxRegenInterval", maxRegenInterval)
                .add("regenAmount", regenAmount)
                .add("regenInterval", regenInterval)
                .add("consumable", consumable)
                .add("invulnerableTicks", invulnerableTicks)
                .add("health", health)
                .toString();
    }

    @Override
    public int getInvulnerableTicks() {
        return invulnerableTicks;
    }

    @Override
    public void setInvulnerableTicks(int invulnerableTicks) {
        this.invulnerableTicks = invulnerableTicks;
    }
}
