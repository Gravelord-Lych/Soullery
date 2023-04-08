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

public class ChaosWandItem extends AbstractWandItem {
    private static final int RANGE = 6;
    private static final int COST = 400;
    private static final int TIME = 100;

    public ChaosWandItem(Properties properties) {
        super(properties, COST);
    }

    @Nullable
    @Override
    protected ActionResultType performWandUse(ServerWorld level, ServerPlayerEntity player, Hand hand) {
        Predicate<LivingEntity> selector = EntityPredicates.ATTACK_ALLOWED::test;
        boolean controlled = false;
        for (MobEntity mob : level.getNearbyEntities(MobEntity.class,  new EntityPredicate().range(RANGE).selector(selector.and(entity -> entity instanceof IMob)), player, player.getBoundingBox().inflate(RANGE))) {
            boolean success = ControlDictionaries.CHAOS.control(mob, player, TIME) != null;
            controlled |= success;
            if (success) {
                EntityUtils.addParticlesAroundSelfServerside(mob, level, RedstoneParticles.YELLOW_GREEN, 6);
            }
        }
        return controlled ? ActionResultType.CONSUME : null;
    }
}
