package lych.soullery.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lych.soullery.Soullery;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper.ApplicationStatus;
import lych.soullery.gui.container.SoulReinforcementTableContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.Nullable;

import static lych.soullery.gui.container.SoulReinforcementTableContainer.*;

public class SoulReinforcementTableScreen extends ContainerScreen<SoulReinforcementTableContainer> {
    private static final ResourceLocation SOUL_REINFORCEMENT_TABLE = Soullery.prefixTex("gui/container/soul_reinforcement_table.png");

    public SoulReinforcementTableScreen(SoulReinforcementTableContainer container, PlayerInventory inventory, ITextComponent text) {
        super(container, inventory, text);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1, 1, 1, 1);
        if (minecraft != null) {
            minecraft.getTextureManager().bind(SOUL_REINFORCEMENT_TABLE);
            int startX = (width - imageWidth) / 2;
            int startY = (height - imageHeight) / 2;
            blit(stack, startX, startY, 0, 0, imageWidth, imageHeight);
            blit(stack, startX + 9, startY + 26, 9, 166, menu.getSEProgress(), 27);
            if ((menu.getSlot(0).hasItem() || menu.getSlot(1).hasItem() || menu.getSlot(2).hasItem()) && !menu.getSlot(3).hasItem()) {
                blit(stack, startX + 104, startY + 46, imageWidth, 0, 28, 21);
            }
        }
    }

    @Override
    protected void renderLabels(MatrixStack stack, int x, int y) {
        super.renderLabels(stack, x, y);
        font.draw(stack,
                format(menu.getSEProgressArray().get(PROGRESS),
                        menu.getSEProgressArray().get(COST),
                        menu.getSEProgressArray().get(OK) > 0,
                        ApplicationStatus.byId(menu.getSEProgressArray().get(STATUS))),
                titleLabelX,
                titleLabelY + 10,
                0);
    }

    private IFormattableTextComponent format(int energy, int energyCost, boolean canAfford, @Nullable ApplicationStatus status) {
        IFormattableTextComponent text = new StringTextComponent(String.format("%d SE", energy)).withStyle(TextFormatting.DARK_AQUA);
        if (menu.getSlot(0).hasItem() && menu.getSlot(1).hasItem() && menu.getSlot(2).hasItem()) {
            if (status != null && !status.isOk()) {
                text.append(new StringTextComponent(" ")).append(status.getErrorText());
            } else if (energyCost > 0) {
                text.append(new StringTextComponent(String.format(" - %d SE", energyCost)).withStyle(canAfford ? TextFormatting.BLUE : TextFormatting.RED));
            }
        }
        return text;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }
}
