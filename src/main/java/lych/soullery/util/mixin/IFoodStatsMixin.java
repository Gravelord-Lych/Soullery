package lych.soullery.util.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

public interface IFoodStatsMixin {
    @Nullable
    PlayerEntity getPlayer();

    void setPlayer(PlayerEntity player);
}
