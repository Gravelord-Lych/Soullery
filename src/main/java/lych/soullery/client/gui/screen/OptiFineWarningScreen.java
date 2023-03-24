package lych.soullery.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import lych.soullery.Soullery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class OptiFineWarningScreen extends Screen {
    private final Screen lastScreen;
    private int ticksUntilEnable = 20 * 3;
    private IBidiRenderer renderer = IBidiRenderer.EMPTY;
    private static final ITextComponent WARNING = new TranslationTextComponent(Soullery.prefixMsg("gui", "optifine_warning"));

    public OptiFineWarningScreen(Screen screen) {
        super(new TranslationTextComponent(Soullery.prefixMsg("gui", "optifine_warning_title")));
        this.lastScreen = screen;
    }

    @Override
    public String getNarrationMessage() {
        return super.getNarrationMessage() + ". " + WARNING.getString();
    }

    @Override
    protected void init() {
        super.init();
        addButton(new Button(width / 2 - 75, height * 3 / 4, 150, 20, DialogTexts.GUI_PROCEED, button -> Minecraft.getInstance().setScreen(lastScreen)));
        renderer = IBidiRenderer.create(font, WARNING, width - 50);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 30, 16777215);
        this.renderer.renderCentered(matrixStack, this.width / 2, 70);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (--ticksUntilEnable == 0) {
            for(Widget widget : buttons) {
                widget.active = true;
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
