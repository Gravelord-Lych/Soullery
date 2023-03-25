package lych.soullery.extension.highlight;

import net.minecraft.nbt.CompoundNBT;

import java.awt.*;
import java.util.UUID;

public class SoulControllerHighlighter extends SimpleHighlighter {
    private static final Color COLOR = new Color(40, 255, 170);

    public SoulControllerHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public SoulControllerHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.SOUL_CONTROLLER;
    }

    @Override
    protected Color getDefaultColor() {
        return COLOR;
    }

    @Override
    public int getPriority() {
        return 1500;
    }
}
