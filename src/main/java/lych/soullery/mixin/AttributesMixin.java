package lych.soullery.mixin;

import net.minecraft.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Attributes.class)
public abstract class AttributesMixin {
    private static final double NEW_MAX_HEALTH = 2147483647.0;

    @ModifyConstant(method = "<clinit>", constant = @Constant(doubleValue = 1024, ordinal = 0))
    private static double modifyMaxHealth(double o) {
        return NEW_MAX_HEALTH;
    }
}
