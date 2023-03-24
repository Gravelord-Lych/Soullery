package lych.soullery.extension.highlight;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.PriorityQueue;
import java.util.UUID;

public class DummyHighlighter implements Highlighter {
    public DummyHighlighter() {}

    public DummyHighlighter(UUID entityUUID, long highlightTicksRemaining) {}

    public DummyHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {}

    @Nullable
    @Override
    public Color getColor(ServerWorld level, PriorityQueue<Highlighter> queue) {
        return null;
    }

    @Override
    public long getHighlightTicks() {
        return 0;
    }

    @Override
    public void setHighlightTicks(long highlightTicks) {}

    @Override
    public CompoundNBT save() {
        return new CompoundNBT();
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.NO_HIGHLIGHT;
    }
}
