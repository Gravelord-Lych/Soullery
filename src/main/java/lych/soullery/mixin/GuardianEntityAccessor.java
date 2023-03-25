package lych.soullery.mixin;

import net.minecraft.entity.monster.GuardianEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuardianEntity.class)
public interface GuardianEntityAccessor {
    @Invoker
    void callSetActiveAttackTarget(int id);
}
