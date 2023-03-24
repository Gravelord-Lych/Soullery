package lych.soullery.api.shield;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A shield user that can use {@link ISharedShield shared shield}.
 * See Void Defender for an example
 */
public interface IShieldUser {
    /**
     * Gets the shield that is currently using by the entity.
     * @return The shield. <code>null</code> if no shield is using by the entity
     */
    @Nullable
    ISharedShield getSharedShield();

    /**
     * Sets the shield that is currently using by the entity.
     * @param sharedShield The shield. <code>null</code> if you want to remove the entity's shield
     */
    void setSharedShield(@Nullable ISharedShield sharedShield);

    /**
     * Returns true if the shield returned by the {@link IShieldUser#getSharedShield()} method can absorb damage.
     * @return True if the shield is valid
     */
    default boolean isShieldValid() {
        return !getAllShields().isEmpty();
    }

    /**
     * This method will be called after the currently using shield is exhausted. (<code>shield.setHealth(0)</code>)<br>
     * The method will always be called no matter whether the currently using shield is
     * {@link ISharedShield#canBeConsumed() consumable} or not
     */
    default void onShieldExhausted() {}

    /**
     * This method will be called after the currently using shield is broken. (<code>setSharedShield(null)</code>)<br>
     * Unless the currently using shield is {@link ISharedShield#canBeConsumed() consumable},
     * the method will <b>not</b> be called.
     */
    default void onShieldBreak() {}

    /**
     * Returns whether hit particles should be shown or not.
     * @param source The damage source.
     * @param amount The amount of the damage.
     * @return True if hit particles should be shown.
     */
    default boolean showHitParticles(DamageSource source, float amount) {
        return true;
    }

    /**
     * This method may need to be overridden if the entity has multiple shields.
     * @see ISharedShield#canBeConsumed() canBeConsumed.
     * @return True if the entity has a consumable shield
     */
    default boolean hasConsumableShield() {
        return getSharedShield() != null && getSharedShield().canBeConsumed();
    }

    /**
     * Gets all shields that the entity has no matter whether they are active or not. Used for the calculation
     * of the entity's highlight's color.<br>
     * The method do not need to be overridden unless the entity has multiple shields.
     * @return The entity's all shields
     */
    default List<ISharedShield> getAllShields() {
        return getSharedShield() == null ? ImmutableList.of() : ImmutableList.of(getSharedShield());
    }

    /**
     * Gets the main shield. The main shield's PD is used to calculate the entity's highlight's color.<br>
     * The method do not need to be overridden unless the entity has multiple shields. <strong>However, it must not
     * return <code>null</code> if the entity's shield is {@link IShieldUser#isShieldValid() valid}</strong>
     * @return The main shield
     */
    @Nullable
    default ISharedShield getMainShield() {
        return getSharedShield();
    }
}
