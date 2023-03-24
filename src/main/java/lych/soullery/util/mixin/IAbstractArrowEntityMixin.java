package lych.soullery.util.mixin;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IAbstractArrowEntityMixin {
    int getEnhancedLevel();

    void setEnhancedLevel(int enhancedLevel);

    @Nullable
    ItemStack getRecordedBow();

    void setRecordedBow(ItemStack bow);
}
