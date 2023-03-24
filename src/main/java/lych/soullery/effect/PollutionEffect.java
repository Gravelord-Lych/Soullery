package lych.soullery.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.Soullery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class PollutionEffect extends Effect {
    private static final int TEXTURE_SIZE = 128;
    private static final ResourceLocation POLLUTION = Soullery.prefixTex("gui/container/pollution_effect.png");

    public PollutionEffect(EffectType category, int color) {
        super(category, color);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bind(POLLUTION);
        AbstractGui.blit(mStack, x, y, (int) z, 0, 24, 120, 31, TEXTURE_SIZE, TEXTURE_SIZE);
//      Rerender the icon.
        TextureAtlasSprite sprite = mc.getMobEffectTextures().get(this);
        mc.getTextureManager().bind(sprite.atlas().location());
        RenderSystem.color4f(1, 1, 1, 1);
        AbstractGui.blit(mStack, x + 6, y + 7, (int) z, 18, 18, sprite);
    }

    @Override
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha) {
        Minecraft.getInstance().getTextureManager().bind(POLLUTION);
        AbstractGui.blit(mStack, x, y, (int) z, 0, 0, 24, 24, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public int getGuiSortColor(EffectInstance potionEffect) {
        return super.getGuiSortColor(potionEffect) - (1 << 24);
    }
}
