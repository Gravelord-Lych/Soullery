package lych.soullery.world.spawner;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import lych.soullery.util.ArrayUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

public class FeaturedSpawner extends CustomizableSpawner {
    public static final int NO_LIGHT_RESTRICTIONS = 0b1;
    public static final int DOUBLED_SPAWN = 0b10;
    public static final int HALF_INTERVAL = 0b100;
    public static final int ARMORED = 0b1000;
    private final IntConsumer eventFirer;
    private final Supplier<? extends World> worldGetter;
    private final Supplier<? extends BlockPos> posGetter;
    private int characteristics;
    private boolean armored;

    public FeaturedSpawner(IntConsumer eventFirer, Supplier<? extends World> worldGetter, Supplier<? extends BlockPos> posGetter) {
        this.eventFirer = eventFirer;
        this.worldGetter = worldGetter;
        this.posGetter = posGetter;
    }

    public void addRandomCharacteristics(Random random, int min, int max, int... characteristics) {
        Preconditions.checkArgument(min >= 0, "Min must not be negative");
        Preconditions.checkArgument(max >= min, "Max must not be smaller than min");
        int randomValue = min + random.nextInt(max - min + 1);
        if (randomValue == 0) {
            return;
        }
        characteristics = characteristics.clone();
        ArrayUtils.shuffle(characteristics, random);
        for (int i = 0; i < randomValue; i++) {
            addCharacteristics(characteristics[i]);
        }
    }

    public void addCharacteristics(int... characteristics) {
        Objects.requireNonNull(characteristics);
        if (characteristics.length == 0) {
            return;
        }
        if (characteristics.length == 1) {
            this.characteristics |= characteristics[0];
            updateFromCharacteristics(characteristics[0]);
            return;
        }
        int oldCharacteristics = this.characteristics;
        this.characteristics |= calculateCharacteristics(characteristics);
        updateFromCharacteristics(getNewCharacteristics(oldCharacteristics, this.characteristics));
    }

    private static int calculateCharacteristics(int... characteristics) {
        return Arrays.stream(characteristics).reduce(0, (c1, c2) -> c1 | c2);
    }

    @Override
    public void broadcastEvent(int eventId) {
        eventFirer.accept(eventId);
    }

    @Override
    public World getLevel() {
        return worldGetter.get();
    }

    @Override
    public BlockPos getPos() {
        return posGetter.get();
    }

    private void updateFromCharacteristics(int addedCharacteristics) {
        if (hasCharacteristic(addedCharacteristics, DOUBLED_SPAWN)) {
            setSpawnCount(spawnCount * 2);
            setMaxNearbyEntities(Ints.saturatedCast(Math.round(maxNearbyEntities * 1.5)));
        }
        if (hasCharacteristic(addedCharacteristics, ARMORED)) {
            armored = true;
        }
    }

    private static int getNewCharacteristics(int oldCharacteristics, int newCharacteristics) {
        if ((oldCharacteristics | newCharacteristics) != newCharacteristics) {
            throw new IllegalArgumentException("Detected removed characteristics, that's not allowed");
        }
        return oldCharacteristics ^ newCharacteristics;
    }

    public boolean hasCharacteristic(int characteristic) {
        return hasCharacteristic(characteristics, characteristic);
    }

    public static boolean hasCharacteristic(int existCharacteristics, int characteristic) {
        return (existCharacteristics & characteristic) == characteristic;
    }

    public void dearmor() {
        armored = false;
    }

    @Override
    protected int randomSpawnDelay() {
        return super.randomSpawnDelay() / (hasCharacteristic(HALF_INTERVAL) ? 2 : 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean checkSpawnRules(World level, EntityType<?> type, double x, double y, double z, ServerWorld serverLevel) {
        if (hasCharacteristic(NO_LIGHT_RESTRICTIONS)) {
            return MonsterEntity.checkAnyLightMonsterSpawnRules((EntityType<? extends MonsterEntity>) type, level, SpawnReason.SPAWNER, new BlockPos(x, y, z), level.getRandom());
        }
        return super.checkSpawnRules(level, type, x, y, z, serverLevel);
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        super.load(compoundNBT);
        if (compoundNBT.contains("Armored")) {
            armored = compoundNBT.getBoolean("Armored");
        }
        if (compoundNBT.contains("Characteristics")) {
            characteristics = compoundNBT.getInt("Characteristics");
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        CompoundNBT saved = super.save(compoundNBT);
        saved.putBoolean("Armored", armored);
        saved.putInt("Characteristics", characteristics);
        return saved;
    }
}
