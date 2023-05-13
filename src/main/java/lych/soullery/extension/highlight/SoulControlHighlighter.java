package lych.soullery.extension.highlight;

import com.google.common.collect.ImmutableList;
import lych.soullery.api.capability.IControlledMobData;
import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.SoulManager;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.awt.Color;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.function.Supplier;

public class SoulControlHighlighter extends AbstractHighlighter {
    public static final Supplier<float[]> DEFAULT_COLOR = () -> new float[]{0.5f, 0.8f, 1};
    private static final float[] GLOWING_COLOR = new float[]{Float.NaN, 0, 1};
    private static final int IS_GLOWING = 6;

    public SoulControlHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public SoulControlHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getColor(ServerWorld level, Entity entity) {
        if (!(entity instanceof MobEntity)) {
            return null;
        }
        PriorityQueue<Controller<?>> queue = SoulManager.getData((MobEntity) entity).map(IControlledMobData::getControllers).orElseGet(PriorityQueue::new);
        if (queue.isEmpty()) {
            return null;
        }
        Controller<?> activeController = queue.element();
        ImmutableList.Builder<float[]> builder = ImmutableList.builder();
        float[] color = activeController.getColor().clone();
        int timeRemaining = SoulManager.timeRemaining((MobEntity) entity, activeController.getType());
        if (SoulManager.getRemainingPercent((MobEntity) entity, activeController.getType()) <= 0.5) {
            color[2] += applyBlink(timeRemaining, 2.5f, 2, 200);
        }
        builder.add(color);
        if (entity.isGlowing() || ((IEntityMixin) entity).callGetSharedFlag(IS_GLOWING)) {
            builder.add(GLOWING_COLOR);
        }
        return reduce(builder.build());
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.SOUL_CONTROL;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
