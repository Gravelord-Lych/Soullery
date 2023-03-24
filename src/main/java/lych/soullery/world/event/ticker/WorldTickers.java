package lych.soullery.world.event.ticker;

import lych.soullery.Soullery;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.boss.Meta08Entity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.HashSet;
import java.util.Set;

import static lych.soullery.world.event.ticker.ContextedWorldTickerType.createAndRegister;
import static lych.soullery.world.event.ticker.ContextedWorldTickerType.onRegisterTickers;

public class WorldTickers {
    public static final WorldTickerType SUMMON_META_08;
    public static final WorldTickerType EXPLOSION;

    static final Set<WorldTickerType> TICKERS;

    static {
        TICKERS = new HashSet<>();
        EXPLOSION = createAndRegister(Soullery.prefix("explosion"), 20, (ticker, timeRemaining) -> explosion(ticker, false));
        SUMMON_META_08 = createAndRegister(Soullery.prefix("summon_meta8"), (ticker, timeRemaining) -> {
            explosion(ticker, true);
            if (timeRemaining < 50) {
                for (int i = 0; i < 2; i++) {
                    double x = ticker.getRandom().nextGaussian() * 0.75;
                    double y = 0.3 + ticker.getRandom().nextDouble() * 1.2;
                    double z = ticker.getRandom().nextGaussian() * 0.75;
                    ticker.getLevel().sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, ticker.getCenter().getX() + x, ticker.getCenter().getY() + y, ticker.getCenter().getZ() + z, 1, 0, 0.5, 0, 5);
                }
            }
            if (ticker.isLastTick()) {
                Meta08Entity meta8 = ModEntities.META8.create(ticker.getLevel());
                if (meta8 != null) {
                    meta8.moveTo(ticker.getCenter().getX() + 0.5, ticker.getCenter().getY() + 0.05, ticker.getCenter().getZ() + 0.5, ticker.getRandom().nextFloat() * 360, 0);
                    for(ServerPlayerEntity player : ticker.getLevel().getEntitiesOfClass(ServerPlayerEntity.class, new AxisAlignedBB(ticker.getCenter()).inflate(50.0D))) {
                        CriteriaTriggers.SUMMONED_ENTITY.trigger(player, meta8);
                    }
                    meta8.finalizeSpawn(ticker.getLevel(), ticker.getLevel().getCurrentDifficultyAt(meta8.blockPosition()), SpawnReason.EVENT, null, null);
                    ticker.getLevel().addFreshEntity(meta8);
                }
            }
        });
        onRegisterTickers();
    }

    private static void explosion(WorldTicker ticker, boolean bigExplosion) {
        for (int i = 0; i < 2 + ticker.getRandom().nextInt(3); i++) {
            double x = ticker.getRandom().nextGaussian() * 0.5;
            double y = 0.2 + ticker.getRandom().nextDouble() * 0.8;
            double z = ticker.getRandom().nextGaussian() * 0.5;
            ticker.getLevel().sendParticles(ticker.getRandom().nextDouble() < (bigExplosion ? 0.5 : 0.15) ? ParticleTypes.EXPLOSION_EMITTER : ParticleTypes.EXPLOSION, ticker.getCenter().getX() + x, ticker.getCenter().getY() + y, ticker.getCenter().getZ() + z, 1, ticker.getRandom().nextGaussian() * 0.04, ticker.getRandom().nextGaussian() * 0.02, ticker.getRandom().nextGaussian() * 0.04, 0.2);
        }
    }
}
