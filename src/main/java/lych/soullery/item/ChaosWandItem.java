package lych.soullery.item;

import lych.soullery.extension.control.dict.ControlDictionaries;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.RedstoneParticles;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ChaosWandItem extends AbstractWandItem<ChaosWandItem> {
    private static final int RANGE = 6;
    private static final int RANGE_II = 9;
    private static final int COST = 400;
    private static final int TIME = 100;
    private static final int TIME_II = 150;

    public ChaosWandItem(Properties properties, int tier) {
        super(properties, COST, tier);
    }

    @Nullable
    @Override
    protected ActionResultType performWandUse(ServerWorld level, ServerPlayerEntity player, Hand hand) {
        Predicate<LivingEntity> selector = EntityPredicates.ATTACK_ALLOWED::test;
        boolean controlled = false;
        for (MobEntity mob : level.getNearbyEntities(MobEntity.class,  new EntityPredicate().range(getRange()).selector(selector.and(entity -> entity instanceof IMob)), player, player.getBoundingBox().inflate(getRange()))) {
            boolean success = ControlDictionaries.CHAOS.control(mob, player, getTier() > 1 ? TIME_II : TIME) != null;
            controlled |= success;
            if (success) {
                EntityUtils.addParticlesAroundSelfServerside(mob, level, RedstoneParticles.YELLOW_GREEN, 6);
            }
        }
        return controlled ? ActionResultType.CONSUME : null;
    }

    private int getRange() {
        return getTier() > 1 ? RANGE_II : RANGE;
    }
}
