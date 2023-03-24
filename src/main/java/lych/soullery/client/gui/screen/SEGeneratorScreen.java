package lych.soullery.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.gui.container.*;
import lych.soullery.util.SoulEnergies;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static lych.soullery.client.gui.screen.SEStorageScreen.DEFAULT;
import static lych.soullery.client.gui.screen.SEStorageScreen.SE_STORAGE_TEXTURES;

public class SEGeneratorScreen<T extends AbstractSEGeneratorContainer> extends ContainerScreen<T> {
    private static final int TEXTURE_WIDTH = 176;
    private static final int TEXTURE_HEIGHT = 166;

    protected SEGeneratorScreen(T container, PlayerInventory inventory, ITextComponent text) {
        super(container, inventory, text);
        imageWidth = TEXTURE_WIDTH;
        imageHeight = TEXTURE_HEIGHT;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (minecraft != null) {
            int tier = menu.getTier();
            ResourceLocation texture = getTexture(tier);
            minecraft.getTextureManager().bind(texture);
            int startX = (width - imageWidth) / 2;
            int startY = (height - imageHeight) / 2;
            blit(stack, startX, startY, 0, 0, imageWidth, imageHeight);
            blit(stack, startX + 9, startY + 26, 9, 166, menu.getSEProgress(), 27);
        }
    }

    protected ResourceLocation getTexture(int storageLevel) {
        return SE_STORAGE_TEXTURES.getOrDefault(storageLevel, DEFAULT);
    }

    @Override
    protected void renderLabels(MatrixStack stack, int x, int y) {
        super.renderLabels(stack, x, y);
        font.draw(stack, SoulEnergies.formatSE(menu.getSEProgressArray().get(0), menu.getSEProgressArray().get(1)), titleLabelX, titleLabelY + 10, 0);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }

    public static class Common extends SEGeneratorScreen<SEGeneratorContainer> {
        public Common(SEGeneratorContainer container, PlayerInventory inventory, ITextComponent text) {
            super(container, inventory, text);
        }
    }

    public static class Depth extends SEGeneratorScreen<DepthSEGeneratorContainer> {
        public Depth(DepthSEGeneratorContainer container, PlayerInventory inventory, ITextComponent text) {
            super(container, inventory, text);
        }
    }

    public static class Heat extends SEGeneratorScreen<HeatSEGeneratorContainer> {
        public Heat(HeatSEGeneratorContainer container, PlayerInventory inventory, ITextComponent text) {
            super(container, inventory, text);
        }
    }

    public static class Nether extends SEGeneratorScreen<NetherSEGeneratorContainer> {
        public Nether(NetherSEGeneratorContainer container, PlayerInventory inventory, ITextComponent text) {
            super(container, inventory, text);
        }
    }

    public static class Sky extends SEGeneratorScreen<SkySEGeneratorContainer> {
        public Sky(SkySEGeneratorContainer container, PlayerInventory inventory, ITextComponent text) {
            super(container, inventory, text);
        }
    }

    public static class Solar extends SEGeneratorScreen<SolarSEGeneratorContainer> {
        public Solar(SolarSEGeneratorContainer container, PlayerInventory inventory, ITextComponent text) {
            super(container, inventory, text);
        }
    }
}
