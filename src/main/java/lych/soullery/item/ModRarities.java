package lych.soullery.item;

import net.minecraft.item.Rarity;
import net.minecraft.util.text.TextFormatting;

public final class ModRarities {
    public static final Rarity LEGENDARY = Rarity.create("LEGENDARY", TextFormatting.RED);
    public static final Rarity MAX = Rarity.create("MAX", TextFormatting.DARK_BLUE);

    private ModRarities() {}
}
