package lych.soullery.util;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public final class BossInfoMarkerTextComponent extends StringTextComponent {
    public static final BossInfoMarkerTextComponent INSTANCE = new BossInfoMarkerTextComponent();

    private BossInfoMarkerTextComponent() {
        super("");
        siblings.clear();
    }

    @Override
    public String toString() {
        return "TextComponent{text=''}";
    }

    @Override
    public IFormattableTextComponent append(ITextComponent component) {
        throw new UnsupportedOperationException();
    }
}
