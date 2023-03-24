package lych.soullery.mixin.client;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlocks;
import lych.soullery.world.gen.biome.ModBiomes;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.FlatPresetsScreen;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;

@Mixin(FlatPresetsScreen.class)
public abstract class FlatPresetsScreenMixin {
    @Shadow
    private static void preset(ITextComponent component, IItemProvider iconItem, RegistryKey<Biome> biome, List<Structure<?>> structures, boolean hasStronghold, boolean hasDecoration, boolean hasLakes, FlatLayerInfo... layers) {}

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addMore(CallbackInfo ci) {
        preset(new TranslationTextComponent(Soullery.prefixMsg("createWorld", "customize.preset.soul_land")),
                Blocks.SOUL_SOIL,
                ModBiomes.SOUL_PLAINS,
                Collections.emptyList(),
                false,
                false,
                false,
                new FlatLayerInfo(1, Blocks.SOUL_SOIL),
                new FlatLayerInfo(2, Blocks.SOUL_SAND),
                new FlatLayerInfo(60, ModBlocks.SOUL_STONE),
                new FlatLayerInfo(1, Blocks.BEDROCK));
    }
}
