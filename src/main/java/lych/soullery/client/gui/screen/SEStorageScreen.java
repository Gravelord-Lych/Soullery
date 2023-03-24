package lych.soullery.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.Soullery;
import lych.soullery.gui.container.SEStorageContainer;
import lych.soullery.util.SoulEnergies;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class SEStorageScreen extends ContainerScreen<SEStorageContainer> {
    static final ResourceLocation DEFAULT = Soullery.prefixTex("gui/container/soul_energy_storage.png");
    static final Map<Integer, ResourceLocation> SE_STORAGE_TEXTURES = Util.make(new HashMap<>(), map -> {
        map.put(1, DEFAULT);
        map.put(2, Soullery.prefixTex("gui/container/soul_energy_storage_l2.png"));
        map.put(3, Soullery.prefixTex("gui/container/soul_energy_storage_l3.png"));
    });
    private static final int TEXTURE_WIDTH = 176;
    private static final int TEXTURE_HEIGHT = 166;

    public SEStorageScreen(SEStorageContainer container, PlayerInventory inventory, ITextComponent text) {
        super(container, inventory, text);
        imageWidth = TEXTURE_WIDTH;
        imageHeight = TEXTURE_HEIGHT;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (minecraft != null) {
            ResourceLocation texture;
            int storageLevel = menu.getStorageTier();
            texture = SE_STORAGE_TEXTURES.getOrDefault(storageLevel, DEFAULT);
            minecraft.getTextureManager().bind(texture);
            int startX = (width - imageWidth) / 2;
            int startY = (height - imageHeight) / 2;
            blit(stack, startX, startY, 0, 0, imageWidth, imageHeight);
            blit(stack, startX + 9, startY + 26, 9, 166, menu.getSEProgress(), 27);
        }
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
}
