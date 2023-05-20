package lych.soullery.extension.highlight;

import net.minecraft.nbt.CompoundNBT;

import java.awt.Color;
import java.util.UUID;

public class SoulifiedEnderPearlHighlighter extends SimpleHighlighter {
    private static final Color COLOR = new Color(41, 136, 204);

    public SoulifiedEnderPearlHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public SoulifiedEnderPearlHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getDefaultColor() {
        return COLOR;
    }

    @Override
    public int getPriority() {
        return 10000;
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.SOULIFIED_ENDER_PEARL;
    }
}
