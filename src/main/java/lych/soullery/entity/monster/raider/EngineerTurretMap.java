package lych.soullery.entity.monster.raider;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ModEntities;
import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class EngineerTurretMap {
    private final Object2IntMap<EntityType<? extends AbstractRedstoneTurretEntity>> turretsRemainingMap = new Object2IntOpenHashMap<>();

    {
        turretsRemainingMap.defaultReturnValue(-1);
        turretsRemainingMap.put(ModEntities.REDSTONE_TURRET, 2);
        turretsRemainingMap.put(ModEntities.REDSTONE_MORTAR, 1);
    }

    public int get(EntityType<? extends AbstractRedstoneTurretEntity> type) {
        return turretsRemainingMap.getInt(type);
    }

    public int add(EntityType<? extends AbstractRedstoneTurretEntity> type) {
        return add(type, 1);
    }

    public int add(EntityType<? extends AbstractRedstoneTurretEntity> type, int count) {
        return turretsRemainingMap.replace(type, turretsRemainingMap.getInt(type) + count);
    }

    public boolean hasEnough(EntityType<? extends AbstractRedstoneTurretEntity> type) {
        return turretsRemainingMap.getInt(type) > 0;
    }

    /**
     * Remove the specific turret from the turret map and returns the number of remaining turrets
     * matching the type.
     * @param turret The turret
     * @return The number of remaining turrets matching the type, or -1 if the engineer does not
     *         support the turret
     */
    public int remove(AbstractRedstoneTurretEntity turret) {
        return remove(turret.getType(), 1);
    }

    /**
     * Remove the specific turret from the turret map and returns the number of remaining turrets
     * matching the type.
     * @param type The type of the turret
     * @param count The number of turrets that will be removed
     * @return The number of remaining turrets matching the type, or -1 if the engineer does not
     *         support the turret
     */
    public int remove(EntityType<? extends AbstractRedstoneTurretEntity> type, int count) {
        int currentCount = get(type);
        if (currentCount < 0) {
            return -1;
        }
        turretsRemainingMap.replace(type, Math.max(currentCount - count, 0));
        return get(type);
    }

    public boolean isEmpty() {
        if (turretsRemainingMap.isEmpty()) {
            return true;
        }
        boolean empty = true;
        for (EntityType<? extends AbstractRedstoneTurretEntity> type : turretsRemainingMap.keySet()) {
            if (turretsRemainingMap.getInt(type) > 0) {
                empty = false;
            }
        }
        return empty;
    }

    public ListNBT save() {
        ListNBT listNBT = new ListNBT();
        for (EntityType<? extends AbstractRedstoneTurretEntity> type : turretsRemainingMap.keySet()) {
            CompoundNBT singleTurretNBT = new CompoundNBT();
            singleTurretNBT.putString("TurretType", Utils.getRegistryName(type).toString());
            singleTurretNBT.putInt("TurretCount", turretsRemainingMap.getInt(type));
            listNBT.add(singleTurretNBT);
        }
        return listNBT;
    }

    @SuppressWarnings("unchecked")
    public void load(ListNBT listNBT) {
        turretsRemainingMap.clear();
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT singleTurretNBT = listNBT.getCompound(i);
            String typeName = singleTurretNBT.getString("TurretType");
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(ResourceLocation.tryParse(typeName));
            if (type == null) {
                Soullery.LOGGER.warn("Engineer's turret type {} was not found", typeName);
            } else {
                EntityType<? extends AbstractRedstoneTurretEntity> turretType;
                try {
                    turretType = (EntityType<? extends AbstractRedstoneTurretEntity>) type;
                } catch (ClassCastException e) {
                    if (ConfigHelper.shouldFailhard()) {
                        throw new RuntimeException(ConfigHelper.FAILHARD_MESSAGE + typeName + " is not a turret", e);
                    }
                    Soullery.LOGGER.error("{} is not a turret", typeName);
                    continue;
                }
                int count = singleTurretNBT.getInt("TurretCount");
                turretsRemainingMap.put(turretType, count);
            }
        }
    }
}
