package lych.soullery.mixin.client;

import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IllagerModel.class)
public interface IllagerModelAccessor {
    @Accessor
    ModelRenderer getLeftArm();

    @Accessor
    ModelRenderer getRightArm();
}
