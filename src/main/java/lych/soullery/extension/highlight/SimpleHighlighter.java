package lych.soullery.extension.highlight;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.awt.*;
import java.util.UUID;

public abstract class SimpleHighlighter extends AbstractHighlighter {
    public SimpleHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public SimpleHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected final Color getColor(ServerWorld level, Entity entity) {
        return getDefaultColor();
    }

    protected abstract Color getDefaultColor();
}
