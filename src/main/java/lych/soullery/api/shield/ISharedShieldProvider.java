package lych.soullery.api.shield;

import net.minecraft.util.DamageSource;
import org.jetbrains.annotations.Nullable;

/**
 * A shared shield user that can provide {@link ISharedShield shared shield} for other entities.
 */
public interface ISharedShieldProvider extends ISharedShieldUser {
    @Nullable
    @Override
    ISharedShield getSharedShield();

    @Override
    void setSharedShield(@Nullable ISharedShield sharedShield);

    /**
     * The entity is sharing its own shield.
     */
    @Nullable
    @Override
    default IShieldUser getShieldProvider() {
        return this;
    }

    @Override
    default boolean showHitParticles(DamageSource source, float amount) {
        return true;
    }
}
