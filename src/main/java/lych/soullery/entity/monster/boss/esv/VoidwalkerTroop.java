package lych.soullery.entity.monster.boss.esv;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.util.CollectionUtils;
import lych.soullery.world.gen.chunkgen.ESVChunkGenerator.Island;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.*;

public class VoidwalkerTroop {
    private final ImmutableMap<EntityType<? extends AbstractVoidwalkerEntity>, Integer> spawnMap;
    private final ServerWorld level;
    private final ImmutableSet<UUID> troopTarget;

    public VoidwalkerTroop(ImmutableMap<EntityType<? extends AbstractVoidwalkerEntity>, Integer> spawnMap, ServerWorld level, ImmutableSet<UUID> troopTarget) {
        this.spawnMap = spawnMap;
        this.level = level;
        this.troopTarget = troopTarget;
    }

    public static VoidwalkerTroop randomTroop(ServerWorld world, Set<UUID> troopTarget, int troopLevel, Random random) {
        return randomTroop(world, troopTarget, troopLevel, false, random);
    }

    public static VoidwalkerTroop randomTroop(ServerWorld world, Set<UUID> troopTarget, int troopLevel, boolean spawnAdvancedVoidwalkers, Random random) {
        Builder builder = in(world);
        builder.setTroopTarget(troopTarget);
        if (troopLevel > 0) {
            int voidwalkerCount = 1 + troopLevel / 2 + randomBonus(troopLevel, random);
            builder.add(ModEntities.VOIDWALKER, voidwalkerCount);
            int voidArcherCount = troopLevel / 3 + randomBonus(troopLevel, random);
            builder.add(ModEntities.VOID_ARCHER, voidArcherCount);
            int voidDefenderCount = (int) (troopLevel / 2.5f) + randomBonus(troopLevel, random);
            builder.add(ModEntities.VOID_DEFENDER, voidDefenderCount);
            if (spawnAdvancedVoidwalkers) {
                int voidAlchemistCount = troopLevel / 4 + randomBonus(troopLevel, random);
                builder.add(ModEntities.VOID_ALCHEMIST, voidAlchemistCount);
            }
        }
        return builder.build();
    }

    private static int randomBonus(int troopLevel, Random random) {
        return random.nextInt((int) Math.ceil(troopLevel / 2f + 1));
    }

    public boolean hasMembers() {
        return !spawnMap.isEmpty();
    }

    public void spawnAt(Island island, Random random, SpawnReason reason) {
        spawnAt(island.getCenter(), (int) (island.getRadius() * 0.75f), random, reason);
    }

    public void spawnAt(BlockPos center, int spacing, Random random, SpawnReason reason) {
        for (Map.Entry<EntityType<? extends AbstractVoidwalkerEntity>, Integer> entry : spawnMap.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                AbstractVoidwalkerEntity voidwalker = entry.getKey().create(level);
                if (voidwalker != null) {
                    BlockPos pos = getRandomSpawnPos(center, spacing, random);
                    if (!troopTarget.isEmpty()) {
                        voidwalker.setMainTarget(CollectionUtils.getNonnullRandom(troopTarget, random));
                    }
                    voidwalker.moveTo(pos, random.nextFloat() * 360, 0);
                    voidwalker.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), reason, null, null);
                    level.addFreshEntity(voidwalker);
                }
            }
        }
    }

    private BlockPos getRandomSpawnPos(BlockPos center, int spacing, Random random) {
        int x = center.getX() - spacing + random.nextInt(spacing * 2 + 1);
        int z = center.getZ() - spacing + random.nextInt(spacing * 2 + 1);
        int y = level.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }

    public static Builder in(World world) {
        Preconditions.checkState(world instanceof ServerWorld);
        return new Builder((ServerWorld) world);
    }

    public static class Builder {
        private final Map<EntityType<? extends AbstractVoidwalkerEntity>, Integer> spawnMap = new HashMap<>();
        private final ServerWorld level;
        private Set<UUID> troopTarget;

        public Builder(ServerWorld level) {
            this.level = level;
        }

        public Builder add(EntityType<? extends AbstractVoidwalkerEntity> type, int count) {
            spawnMap.put(type, count);
            return this;
        }

        public Builder setTroopTarget(UUID... troopTarget) {
            return setTroopTarget(ImmutableSet.copyOf(troopTarget));
        }

        public Builder setTroopTarget(Set<UUID> troopTarget) {
            this.troopTarget = troopTarget;
            return this;
        }

        public VoidwalkerTroop build() {
            return new VoidwalkerTroop(ImmutableMap.copyOf(spawnMap), level, ImmutableSet.copyOf(troopTarget));
        }
    }
}
