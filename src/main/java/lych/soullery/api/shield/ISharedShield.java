package lych.soullery.api.shield;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;

/**
 * An energy shield which user can share with other mobs.
 * You only need to save and load the shield because it will automatically tick and update.<br>
 * The shield has 2 types of defense: Absolute Defense and Passive Defense. The Absolute Defense (AD)
 * means that any attack that hit the shield will get its power lowered, and the Passive Defense (PD)
 * is basically the "max health" of the shield. The PD will regenerate by the specified regen amount
 * each regen interval.
 */
public interface ISharedShield {
    /**
     * Gets the Absolute Defense of the shield.<br>
     * Absolute Defense (AD) refers to the amount of the damage that can be reduced. For example,
     * a shield of 3 AD can reduce damage from 8 to 5.
     * @return The Absolute Defense of the shield
     */
    float getAbsoluteDefense();

    /**
     * Gets the Passive Defense of the shield.<br>
     * Passive Defense (PD) refers to the "max health" of the shield. A shield can regenerate its
     * health until its health reaches its PD.
     * @return The Passive Defense of the shield
     */
    float getPassiveDefense();

    /**
     * Gets the max interval for shield regeneration.
     * @return The max regenerate interval of the shield
     */
    float getMaxRegenInterval();

    /**
     * Gets the amount of health regenerated during a shield regeneration.
     * @return The amount of health regenerated during a shield regeneration
     */
    float getRegenAmount();

    /**
     * Gets the health of the shield.
     * @return The health of the shield
     */
    float getHealth();

    /**
     * Sets the health of the shield to a new value.
     * @param health The new health
     */
    void setHealth(float health);

    /**
     * Heals the shield.
     * @param amount The amount
     */
    void heal(float amount);

    /**
     * Hurts the shield.
     * @param source The {@link DamageSource damage source} that is used to hurt the shield
     * @param amount The amount
     * @return The new amount of the damage which will be applied to the shielded entity after
     *         the shield's damage absorption. Non-positive return value means that the entity
     *         will not be damaged.
     */
    float hurt(DamageSource source, float amount);

    /**
     * If true, the shield will break (<code>setSharedShield(null)</code>) if it loses all its health.
     * @return True if the shield can be consumed.
     */
    boolean canBeConsumed();

    /**
     * Gets the invulnerable ticks remaining of the shield. A positive return value means that
     * the shield is invulnerable.
     * @return The invulnerable ticks of the shield
     */
    int getInvulnerableTicks();

    /**
     * Sets the invulnerable ticks of the shield.
     * @param invulnerableTicks The new invulnerable ticks
     */
    void setInvulnerableTicks(int invulnerableTicks);

    /**
     * Save the shield.
     * @return The NBT that stores the shield's data
     */
    CompoundNBT save();

    /**
     * You don't need to call this method.
     */
    void tick();
}
