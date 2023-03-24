package lych.soullery.api.shield;

import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.Nullable;

/**
 * A shared shield user that can use others' {@link ISharedShield shared shield}
 */
public interface ISharedShieldUser extends IShieldUser {
    /**
     * Gets the {@link ISharedShieldProvider shield provider} of the shield which is using.
     *
     * @return The shield provider, <code>null</code> if there's no shield provider
     */
    @Nullable
    IShieldUser getShieldProvider();

    /**
     * If you want this entity to have its own shield, you can override this method.
     */
    @Nullable
    @Override
    default ISharedShield getSharedShield() {
        return getShieldProvider() == null ? null : getShieldProvider().getSharedShield();
    }

    /**
     * Throws an exception by default. If you want this entity to have its own shield,
     * you can override this method.
     */
    @Override
    default void setSharedShield(@Nullable ISharedShield sharedShield) {
        throw new UnsupportedOperationException();
    }

    /**
     * Will not be invoked unless the entity is a shield provider.
     */
    @Override
    default void onShieldExhausted() {
        IShieldUser.super.onShieldExhausted();
    }

    /**
     * Will not be invoked unless the entity is a shield provider.
     */
    @Override
    default void onShieldBreak() {
        IShieldUser.super.onShieldBreak();
    }

    /**
     * Redirect to the {@link ISharedShieldUser#getShieldProvider() shield provider}.
     * @param source The damage source.
     * @param amount The amount of the damage.
     * @return True if hit particles should be shown.
     */
    @Override
    default boolean showHitParticles(DamageSource source, float amount) {
        return getShieldProvider() != null && getShieldProvider().showHitParticles(source, amount);
    }
}
