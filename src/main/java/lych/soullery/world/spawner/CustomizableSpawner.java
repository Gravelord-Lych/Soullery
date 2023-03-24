package lych.soullery.world.spawner;

import com.google.common.collect.Lists;
import lych.soullery.Soullery;
import lych.soullery.util.EntityEventConstants;
import lych.soullery.util.Utils;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class CustomizableSpawner extends AbstractSpawner {
    protected static final Marker MARKER = MarkerManager.getMarker("CustomizableSpawner");
    protected int spawnDelay = 20;
    protected final List<WeightedSpawnerEntity> spawnPotentials = Lists.newArrayList();
    protected WeightedSpawnerEntity nextSpawnData = new WeightedSpawnerEntity();
    protected double spin;
    protected double oSpin;
    protected int minSpawnDelay = 200;
    protected int maxSpawnDelay = 800;
    protected int spawnCount = 4;
    @Nullable
    protected Entity displayEntity;
    protected int maxNearbyEntities = 6;
    protected int requiredPlayerRange = 16;
    protected int spawnRange = 4;

    @Nullable
    public ResourceLocation getEntityId() {
        String id = nextSpawnData.getTag().getString("id");
        try {
            return StringUtils.isNullOrEmpty(id) ? null : new ResourceLocation(id);
        } catch (ResourceLocationException resourcelocationexception) {
            BlockPos pos = getPos();
            Soullery.LOGGER.warn(MARKER, "Invalid entity id '{}' at spawner {}:[{},{},{}]", id, getLevel().dimension().location(), pos.getX(), pos.getY(), pos.getZ());
            return null;
        }
    }

    @Override
    public void setEntityId(EntityType<?> type) {
        nextSpawnData.getTag().putString("id", getRegistryName(type).toString());
    }

    @Override
    public void tick() {
        if (isNearPlayer()) {
            World level = getLevel();
            BlockPos pos = this.getPos();
            if (level instanceof ServerWorld) {
                if (spawnDelay == -1) {
                    delay();
                }
                if (spawnDelay > 0) {
                    spawnDelay--;
                    return;
                }
                boolean spawned = false;
                for (int i = 0; i < spawnCount; i++) {
                    CompoundNBT spawnDataTag = nextSpawnData.getTag();
                    Optional<EntityType<?>> type = EntityType.by(spawnDataTag);
                    if (type.isPresent()) {
                        ListNBT posNBT = spawnDataTag.getList("Pos", 6);
                        int size = posNBT.size();
                        double x = size >= 1 ? posNBT.getDouble(0) : (double) pos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                        double y = size >= 2 ? posNBT.getDouble(1) : (double) (pos.getY() + level.random.nextInt(3) - 1);
                        double z = size >= 3 ? posNBT.getDouble(2) : (double) pos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * (double) this.spawnRange + 0.5D;
                        if (level.noCollision(type.get().getAABB(x, y, z))) {
                            ServerWorld serverLevel = (ServerWorld) level;
                            if (checkSpawnRules(level, type.get(), x, y, z, serverLevel)) {
                                Entity entity = EntityType.loadEntityRecursive(spawnDataTag, level, entityIn -> {
                                    entityIn.moveTo(x, y, z, entityIn.yRot, entityIn.xRot);
                                    return entityIn;
                                });
                                if (entity == null) {
                                    delay();
                                    return;
                                }
                                if (getNearbyEntityCount(level, pos, entity) >= maxNearbyEntities) {
                                    delay();
                                    return;
                                }
                                doRandomYRotOffset(level, entity);
                                if (entity instanceof MobEntity) {
                                    MobEntity mob = (MobEntity) entity;
                                    if (!ForgeEventFactory.canEntitySpawnSpawner(mob, level, (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), this)) {
                                        continue;
                                    }
                                    if (nextSpawnData.getTag().size() == 1 && nextSpawnData.getTag().contains("id", 8)) {
                                        if (!ForgeEventFactory.doSpecialSpawn(mob, level, (float) entity.getX(), (float) entity.getY(), (float) entity.getZ(), this, SpawnReason.SPAWNER)) {
                                            finalizeSpawnMob(level, serverLevel, entity);
                                        }
                                    }
                                }
                                if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                                    delay();
                                    return;
                                }
                                level.levelEvent(Constants.WorldEvents.MOB_SPAWNER_PARTICLES, pos, 0);
                                postSuccessfulSpawnServerside(entity);
                                spawned = true;
                            }
                        }
                    } else {
                        delay();
                        return;
                    }
                }
                if (spawned) {
                    delay();
                }
            } else {
                tickClientside(level, pos);
            }
        } else {
            oSpin = spin;
        }
    }

    protected void postSuccessfulSpawnServerside(Entity entity) {
        if (entity instanceof MobEntity) {
            ((MobEntity) entity).spawnAnim();
        }
    }

    protected void finalizeSpawnMob(World level, ServerWorld serverLevel, Entity entity) {
        ((MobEntity) entity).finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.SPAWNER, null, null);
    }

    protected void doRandomYRotOffset(World level, Entity entity) {
        entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), level.random.nextFloat() * 360, 0);
    }

    protected int getNearbyEntityCount(World level, BlockPos pos, Entity entity) {
        return level.getEntitiesOfClass(entity.getClass(), new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(spawnRange)).size();
    }

    protected boolean checkSpawnRules(World level, EntityType<?> type, double x, double y, double z, ServerWorld serverLevel) {
        return EntitySpawnPlacementRegistry.checkSpawnRules(type, serverLevel, SpawnReason.SPAWNER, new BlockPos(x, y, z), level.getRandom());
    }

    protected void tickClientside(World level, BlockPos pos) {
        double x = pos.getX() + level.random.nextDouble();
        double y = pos.getY() + level.random.nextDouble();
        double z = pos.getZ() + level.random.nextDouble();
        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        if (spawnDelay > 0) {
            spawnDelay--;
        }
        oSpin = spin;
        spin = (spin + 1000.0 / (spawnDelay + 200.0)) % 360.0;
    }

    protected boolean isNearPlayer() {
        BlockPos pos = getPos();
        return getLevel().hasNearbyAlivePlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, requiredPlayerRange);
    }

    protected void delay() {
        if (maxSpawnDelay <= minSpawnDelay) {
            spawnDelay = minSpawnDelay;
        } else {
            spawnDelay = randomSpawnDelay();
        }
        if (!spawnPotentials.isEmpty()) {
            setNextSpawnData(WeightedRandom.getRandomItem(getLevel().getRandom(), spawnPotentials));
        }
        broadcastEvent(EntityEventConstants.SPAWNER_DELAY);
    }

    protected int randomSpawnDelay() {
        return minSpawnDelay + getLevel().random.nextInt(maxSpawnDelay - minSpawnDelay);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void load(CompoundNBT compoundNBT) {
        spawnDelay = compoundNBT.getInt("Delay");
        spawnPotentials.clear();
        if (compoundNBT.contains("SpawnPotentials", 9)) {
            ListNBT potentials = compoundNBT.getList("SpawnPotentials", 10);
            for (int i = 0; i < potentials.size(); ++i) {
                spawnPotentials.add(new WeightedSpawnerEntity(potentials.getCompound(i)));
            }
        }
        if (compoundNBT.contains("SpawnData", 10)) {
            setNextSpawnData(new WeightedSpawnerEntity(1, compoundNBT.getCompound("SpawnData")));
        } else if (!spawnPotentials.isEmpty()) {
            setNextSpawnData(WeightedRandom.getRandomItem(getLevel().random, spawnPotentials));
        }
        if (compoundNBT.contains("MinSpawnDelay", 99)) {
            minSpawnDelay = compoundNBT.getInt("MinSpawnDelay");
            maxSpawnDelay = compoundNBT.getInt("MaxSpawnDelay");
            spawnCount = compoundNBT.getInt("SpawnCount");
        }
        if (compoundNBT.contains("MaxNearbyEntities", 99)) {
            maxNearbyEntities = compoundNBT.getInt("MaxNearbyEntities");
            requiredPlayerRange = compoundNBT.getInt("RequiredPlayerRange");
        }
        if (compoundNBT.contains("SpawnRange", 99)) {
            spawnRange = compoundNBT.getInt("SpawnRange");
        }
        if (getLevel() != null) {
            displayEntity = null;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ResourceLocation location = getEntityId();
        if (location != null) {
            compoundNBT.putInt("Delay", spawnDelay);
            compoundNBT.putInt("MinSpawnDelay", minSpawnDelay);
            compoundNBT.putInt("MaxSpawnDelay", maxSpawnDelay);
            compoundNBT.putInt("SpawnCount", spawnCount);
            compoundNBT.putInt("MaxNearbyEntities", maxNearbyEntities);
            compoundNBT.putInt("RequiredPlayerRange", requiredPlayerRange);
            compoundNBT.putInt("SpawnRange", spawnRange);
            compoundNBT.put("SpawnData", nextSpawnData.getTag().copy());
            ListNBT listNBT = new ListNBT();
            if (spawnPotentials.isEmpty()) {
                listNBT.add(nextSpawnData.save());
            } else {
                for (WeightedSpawnerEntity entity : spawnPotentials) {
                    listNBT.add(entity.save());
                }
            }
            compoundNBT.put("SpawnPotentials", listNBT);
        }
        return compoundNBT;
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    @Override
    public Entity getOrCreateDisplayEntity() {
        if (displayEntity == null) {
            displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), this.getLevel(), Function.identity());
        }
        return displayEntity;
    }

    @Override
    public boolean onEventTriggered(int eventId) {
        if (eventId == 1 && getLevel().isClientSide()) {
            spawnDelay = minSpawnDelay;
            return true;
        }
        return false;
    }

    @Override
    public void setNextSpawnData(WeightedSpawnerEntity entity) {
        nextSpawnData = entity;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getSpin() {
        return spin;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getoSpin() {
        return oSpin;
    }

    public void setMinSpawnDelay(int minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }

    public void setMaxSpawnDelay(int maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public void setMaxNearbyEntities(int maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    public void setRequiredPlayerRange(int requiredPlayerRange) {
        this.requiredPlayerRange = requiredPlayerRange;
    }

    public void setSpawnRange(int spawnRange) {
        this.spawnRange = spawnRange;
    }

    protected static ResourceLocation getRegistryName(EntityType<?> type) {
        return Utils.getOrDefault(ForgeRegistries.ENTITIES.getKey(type), Utils.getRegistryName(EntityType.PIG));
    }
}
