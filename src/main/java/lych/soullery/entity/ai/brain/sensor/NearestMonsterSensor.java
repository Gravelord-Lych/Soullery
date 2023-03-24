package lych.soullery.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import lych.soullery.entity.ai.brain.memory.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.server.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NearestMonsterSensor extends Sensor<LivingEntity> {
    private static final double RANGE = 16;

    @Override
    protected void doTick(ServerWorld world, LivingEntity entity) {
        AxisAlignedBB bb = entity.getBoundingBox().inflate(RANGE, RANGE, RANGE);
        List<MobEntity> allMobs = world.getEntitiesOfClass(MobEntity.class, bb, otherEntity -> otherEntity != entity && otherEntity.isAlive() && otherEntity instanceof IMob);
        allMobs.sort(Comparator.comparingDouble(entity::distanceToSqr));
        Brain<?> brain = entity.getBrain();
        brain.setMemory(ModMemories.MONSTERS, allMobs);
        brain.setMemory(ModMemories.NEAREST_MONSTER, allMobs.isEmpty() ? null : allMobs.get(0));
        List<MobEntity> visibleMobs = allMobs.stream().filter(otherEntity -> isEntityTargetable(entity, otherEntity)).collect(Collectors.toList());
        brain.setMemory(ModMemories.VISIBLE_MONSTERS, visibleMobs);
        brain.setMemory(ModMemories.NEAREST_VISIBLE_MONSTER, visibleMobs.isEmpty() ? null : visibleMobs.get(0));
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(ModMemories.MONSTERS, ModMemories.NEAREST_MONSTER, ModMemories.VISIBLE_MONSTERS, ModMemories.NEAREST_VISIBLE_MONSTER);
    }
}
