package lych.soullery.util.mixin;

import lych.soullery.extension.fire.Fire;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

public interface IAbstractFireBlockMixin {
    float getFireDamage();

    default Fire getFireType() {
        return Fire.byBlock((Block) this);
    }

    default Fire getApplicableFire(Entity entity) {
        return getFireType().canApplyTo(entity) ? getFireType().applyTo(entity) : Fire.empty();
    }
}
