package lych.soullery.mixin;

import lych.soullery.world.SeedHelper;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(DimensionGeneratorSettings.class)
public class DimensionGeneratorSettingsMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(method = "<init>(JZZLnet/minecraft/util/registry/SimpleRegistry;Ljava/util/Optional;)V", at = @At("RETURN"))
    private void setSeed(long seed, boolean generateFeatures, boolean generateBonusChest, SimpleRegistry<Dimension> dimensions, Optional<String> legacyCustomOptions, CallbackInfo ci) {
        SeedHelper.setSeed(seed);
    }
}
