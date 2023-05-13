package lych.soullery.extension.highlight;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.UUID;

public class HorcruxHighlighter extends AbstractHighlighter {
    private float hue;

    public HorcruxHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public HorcruxHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
        hue = compoundNBT.getFloat("Hue");
    }

    @Nullable
    @Override
    protected Color getColor(ServerWorld level, Entity entity) {
        if (hue >= 1) {
            hue = 0;
        } else {
            hue += 0.0078125f;
        }
        return new Color(Color.HSBtoRGB(hue, 0.15f, 1));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putFloat("Hue", hue);
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.HORCRUX;
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
