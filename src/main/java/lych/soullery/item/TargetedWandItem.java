package lych.soullery.item;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public abstract class TargetedWandItem extends AbstractWandItem {
    public TargetedWandItem(Properties properties, int energyCost) {
        super(properties, energyCost);
    }

    @Nullable
    @Override
    protected ActionResultType performWandUse(ServerWorld level, ServerPlayerEntity player, Hand hand) {
        EntityRayTraceResult ray = EntityUtils.getEntityRayTraceResult(player, getWandDistance(), testsBlock());
        if (ray != null) {
            return performWandOn(level, player, ray, hand);
        }
        return null;
    }

    @Nullable
    protected abstract ActionResultType performWandOn(ServerWorld level, ServerPlayerEntity player, EntityRayTraceResult ray, Hand hand);

    protected int getWandDistance() {
        return 8;
    }

    protected boolean testsBlock() {
        return true;
    }
}
