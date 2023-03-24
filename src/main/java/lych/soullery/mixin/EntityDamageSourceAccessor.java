package lych.soullery.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityDamageSource.class)
public interface EntityDamageSourceAccessor {
    @Accessor
    @Mutable
    void setEntity(Entity entity);
}
