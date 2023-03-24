package lych.soullery.api.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ArrowSpawnEvent extends Event {
    private final ItemStack bow;
    private final PlayerEntity player;
    private final AbstractArrowEntity arrow;

    public ArrowSpawnEvent(ItemStack bow, PlayerEntity player, AbstractArrowEntity arrow) {
        this.bow = bow;
        this.player = player;
        this.arrow = arrow;
    }

    public ItemStack getBow() {
        return bow;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public AbstractArrowEntity getArrow() {
        return arrow;
    }
}
