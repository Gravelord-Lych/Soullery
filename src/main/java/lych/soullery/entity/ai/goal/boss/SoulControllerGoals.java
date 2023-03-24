package lych.soullery.entity.ai.goal.boss;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ai.goal.IPhaseableGoal;
import lych.soullery.entity.functional.SoulBoltEntity;
import lych.soullery.entity.monster.boss.esv.SoulControllerEntity;
import lych.soullery.entity.monster.boss.esv.VoidwalkerTroop;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.entity.projectile.PursuerEntity;
import lych.soullery.util.ArrayUtils;
import lych.soullery.util.EntityUtils;
import lych.soullery.world.gen.chunkgen.ESVChunkGenerator;
import lych.soullery.world.gen.chunkgen.ESVChunkGenerator.Island;
import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public final class SoulControllerGoals {
    private SoulControllerGoals() {}

    public static class ShootPursuersGoal extends SoulControllerGoal {
        private static final Int2IntMap COOLDOWN_MAP = EntityUtils.intChoiceBuilder().range(1, 2).value(20).range(3).value(19).range(4).value(18).range(5).value(16).build();
        private static final int MAX_ATTACK_TIME = 9;
        private int cooldown = getMaxCooldown();
        private int attackTime;
        private int maxAttackTime;

        public ShootPursuersGoal(SoulControllerEntity controller) {
            super(controller);
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public StopReason getSkipReason() {
            if (EntityUtils.isAlive(controller.getTarget())) {
                maxAttackTime = MAX_ATTACK_TIME + random.nextInt(4);
                return null;
            }
            return StopReason.NO_TARGET;
        }

        @Override
        public void tick() {
            super.tick();
            if (controller.getTarget() == null) {
                return;
            }
            LivingEntity target = controller.getTarget();
            controller.getLookControl().setLookAt(target, 30, 30);
            if (cooldown > 0) {
                cooldown--;
            } else {
                PursuerEntity pursuer = new PursuerEntity(controller, target, controller.getBbHeight() * 0.85, level);
                if (random.nextDouble() < 0.2) {
                    pursuer.setAltType(true);
                }
                level.addFreshEntity(pursuer);
                cooldown = getMaxCooldown();
                attackTime++;
            }
            if (controller.getSneakTarget() == null && controller.getRandom().nextDouble() < 0.1) {
                Vector3d pos = null;
                if (controller.distanceToSqr(target) > 16 * 16) {
                    pos = RandomPositionGenerator.getPosTowards(controller, 12, 6, target.position());
                } else if (controller.distanceToSqr(target) < 4 * 4) {
                    pos = RandomPositionGenerator.getPosAvoid(controller, 16, 8, target.position());
                }
                if (pos != null) {
                    controller.setSneakTarget(pos);
                }
            }
        }

        private int getMaxCooldown() {
            return COOLDOWN_MAP.get(controller.getTier());
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (!EntityUtils.isAlive(controller.getTarget())) {
                return StopReason.NO_TARGET;
            }
            if (attackTime > maxAttackTime) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            attackTime = 0;
            cooldown = getMaxCooldown();
        }
    }

    public static class SpawnVoidwalkersGoal extends SoulControllerGoal {
        private static final Int2IntMap TROOP_LEVEL_MAP = EntityUtils.intChoiceBuilder().range(1).value(1).range(2).value(2).range(3).value(4).range(4).value(6).range(5).value(8).build();
        private static final int MAX_DELAY = 40;
        private static final IntUnaryOperator MAX_VOIDWALKER_COUNT_FUNC = tier -> 2 + 5 * tier;
        private int delayTicks;

        public SpawnVoidwalkersGoal(SoulControllerEntity controller) {
            super(controller);
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (!level.players().isEmpty() && EntityUtils.isAlive(controller.getTarget())) {
                return countVoidwalkers() > MAX_VOIDWALKER_COUNT_FUNC.applyAsInt(controller.getTier()) ? StopReason.NEXT_PHASE : null;
            }
            return StopReason.NO_TARGET;
        }

        public int countVoidwalkers() {
            List<AbstractVoidwalkerEntity> voidwalkers;
            if (level.dimension() == ModDimensions.ESV) {
                voidwalkers = ESVChunkGenerator.getAllIslands().stream()
                        .map(island -> level.getEntitiesOfClass(AbstractVoidwalkerEntity.class, island.getTopBoundingBox()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            } else {
                voidwalkers = level.getEntitiesOfClass(AbstractVoidwalkerEntity.class, controller.getBoundingBox().inflate(40, 20, 40));
            }
            return voidwalkers.size();
        }

        @Override
        public void start() {
            super.start();
            LivingEntity target = controller.getTarget();
            Objects.requireNonNull(target);
            if (level.dimension() == ModDimensions.ESV && target instanceof PlayerEntity) {
                Map<Island, VoidwalkerTroop> troopMap = getIslandsToSpawnTroops();
                for (Map.Entry<Island, VoidwalkerTroop> entry : troopMap.entrySet()) {
                    entry.getValue().spawnAt(entry.getKey(), random, SpawnReason.MOB_SUMMONED);
                }
            } else {
                VoidwalkerTroop troop = VoidwalkerTroop.randomTroop(level, ImmutableSet.of(target.getUUID()), TROOP_LEVEL_MAP.get(controller.getTier()), random);
                troop.spawnAt(target.blockPosition(), 8, random, SpawnReason.MOB_SUMMONED);
            }
        }

        public Map<Island, VoidwalkerTroop> getIslandsToSpawnTroops() {
            Map<Island, VoidwalkerTroop> map = new HashMap<>();
            List<PlayerEntity> players = new ArrayList<>(level.players());
            int troopLevel = TROOP_LEVEL_MAP.get(controller.getTier());
            int[] troopLevelArray = ArrayUtils.split(troopLevel, players.size());
            ArrayUtils.shuffle(troopLevelArray, random);
            int index = 0;
            Map<Island, List<PlayerEntity>> playerCountMap = ESVChunkGenerator.getAllIslands().stream().collect(Collectors.toMap(Function.identity(), i -> i.getPlayersInside(players)));
            for (Map.Entry<Island, List<PlayerEntity>> entry : playerCountMap.entrySet()) {
                int levelUsed = 0;
                int playerCount = entry.getValue().size();
                while (playerCount > 0) {
                    levelUsed += troopLevelArray[index];
                    index++;
                    playerCount--;
                }
                VoidwalkerTroop troop = VoidwalkerTroop.randomTroop(level, entry.getValue().stream().map(Entity::getUUID).collect(ImmutableSet.toImmutableSet()), levelUsed, random);
                map.put(entry.getKey(), troop);
            }
            return map;
        }

        @Override
        public void tick() {
            delayTicks++;
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (delayTicks > MAX_DELAY) {
                return StopReason.NEXT_PHASE;
            }
            return getSkipReason();
        }

        @Override
        public void stop() {
            super.stop();
            delayTicks = 0;
        }
    }

    public static class GuideBoltsGoal extends SoulControllerGoal {
        private static final double SPACING = 6;
        private static final int COUNT = 4;
        private static final int COOLDOWN = 5;
        private static final Int2DoubleMap STRENGTH_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(3).range(2).value(4).range(3).value(5).range(4).value(6).range(5).value(8).build();
        private LivingEntity target;
        private int currentCount;
        private int cooldown;
        private Vector3d pos;
        private Vector3d targetPos;

        public GuideBoltsGoal(SoulControllerEntity controller) {
            super(controller);
        }

        @Nullable
        @Override
        public StopReason getSkipReason() {
            if (!EntityUtils.isAlive(controller.getTarget())) {
                return StopReason.NO_TARGET;
            }
            target = controller.getTarget();
            return null;
        }

        @Override
        public void start() {
            super.start();
            pos = controller.position();
            targetPos = target.position();
        }

        @Override
        public void tick() {
            super.tick();
            if (cooldown > 0) {
                cooldown--;
                return;
            }
            Vector3d stepPos = pos.vectorTo(targetPos).normalize().scale(SPACING).scale(currentCount + 1);
            SoulBoltEntity bolt = ModEntities.SOUL_BOLT.create(level);
            if (bolt != null) {
                bolt.moveTo(recalculate(pos.add(stepPos)));
                bolt.setOwner(controller);
                bolt.setKnockbackModifier(0.4);
                bolt.setKnockbackStrength(STRENGTH_MAP.get(controller.getTier()));
                level.addFreshEntity(bolt);
            }
            currentCount++;
            cooldown = COOLDOWN;
        }

        private Vector3d recalculate(Vector3d o) {
            int height = level.getHeight(Heightmap.Type.WORLD_SURFACE, (int) o.x, (int) o.z);
            return new Vector3d(o.x, height, o.z);
        }

        @Nullable
        @Override
        public StopReason getStopReason() {
            if (!EntityUtils.isAlive(target)) {
                return StopReason.NO_TARGET;
            }
            if (currentCount >= COUNT) {
                return StopReason.NEXT_PHASE;
            }
            return null;
        }

        @Override
        public void stop() {
            super.stop();
            currentCount = 0;
        }
    }

    public static abstract class SoulControllerGoal extends Goal implements IPhaseableGoal {
        protected final SoulControllerEntity controller;
        protected final ServerWorld level;
        protected final Random random;

        public SoulControllerGoal(SoulControllerEntity controller) {
            EntityUtils.checkGoalInstantiationServerside(controller);
            this.controller = controller;
            this.level = (ServerWorld) controller.level;
            this.random = controller.getRandom();
        }

        @Deprecated
        @Override
        public final boolean canUse() {
            return false;
        }

        @Deprecated
        @Override
        public final boolean canContinueToUse() {
            return false;
        }
    }
}
