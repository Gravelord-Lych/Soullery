package lych.soullery.extension.highlight;

import net.minecraft.nbt.CompoundNBT;

import java.awt.*;
import java.util.UUID;

public class MindOperatorHelperAltHighlighter extends SimpleHighlighter {
    private static final Color COLOR = new Color(255, 255, 235);

    public MindOperatorHelperAltHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public MindOperatorHelperAltHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getDefaultColor() {
        return COLOR;
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.MIND_OPERATOR_HELPER_ALT;
    }

    @Override
    public int getPriority() {
        return 600;
    }
}
