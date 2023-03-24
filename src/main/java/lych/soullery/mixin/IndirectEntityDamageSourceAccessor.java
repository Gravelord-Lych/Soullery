package lych.soullery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IndirectEntityDamageSource.class)
public interface IndirectEntityDamageSourceAccessor {
    @Accessor
    @Mutable
    void setOwner(Entity owner);
}
