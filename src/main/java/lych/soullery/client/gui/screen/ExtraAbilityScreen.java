package lych.soullery.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.Soullery;
import lych.soullery.gui.container.ExtraAbilityContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ExtraAbilityScreen extends ContainerScreen<ExtraAbilityContainer> {
    private static final ResourceLocation EXA = Soullery.prefixTex("gui/container/extra_ability.png");

    public ExtraAbilityScreen(ExtraAbilityContainer container, PlayerInventory inventory, ITextComponent text) {
        super(container, inventory, text);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1, 1, 1, 1);
        if (minecraft != null) {
            minecraft.getTextureManager().bind(EXA);
            int startX = (width - imageWidth) / 2;
            int startY = (height - imageHeight) / 2;
            blit(stack, startX, startY, 0, 0, imageWidth, imageHeight);
        }
    }

    @Override
    protected void renderLabels(MatrixStack stack, int x, int y) {
        super.renderLabels(stack, x, y);
        for (int i = 0; i < 3; i++) {
            int tx = titleLabelX + 16 - ExtraAbilityContainer.ORDER_NUMBER_SPACING + ExtraAbilityContainer.SPACING_X * i;
            font.draw(stack, number(i * 2 + 1), tx, titleLabelY + ExtraAbilityContainer.ORDER_NUMBER_OFFSET, 0);
            font.draw(stack, number(i * 2 + 2), tx, titleLabelY + ExtraAbilityContainer.ORDER_NUMBER_OFFSET + ExtraAbilityContainer.SPACING_Y, 0);
        }
    }

    private ITextComponent number(int num) {
        return new StringTextComponent(String.valueOf(num)).withStyle(num <= menu.getAvailableCount() ? TextFormatting.BLACK : TextFormatting.DARK_GRAY);
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }
}
