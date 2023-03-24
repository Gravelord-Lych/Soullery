package lych.soullery.entity.iface;

import lych.soullery.entity.Soul;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

public interface INecromancer<T extends LivingEntity, S extends Soul<T>> {
    double getReviveDistance();

    boolean canAddSoul(T entity, ServerWorld world);

    Queue<S> getSouls();

    S createSoul(T entity);

    @Nullable
    S loadSoul(CompoundNBT compoundNBT);

    default boolean addSoulDirectly(S soul) {
        return getSouls().offer(soul);
    }

    @SuppressWarnings("unchecked")
    default boolean addSoulIfPossible(Entity entity, ServerWorld world) {
        try {
            T castedEntity = (T) entity;
            if (canAddSoul(castedEntity, world)) {
                addSoulDirectly(createSoul(castedEntity));
            }
        } catch (ClassCastException e) {
            return false;
        }
        return false;
    }

    default void saveSouls(CompoundNBT compoundNBT) {
        ListNBT listNBT = new ListNBT();
        while (!getSouls().isEmpty()) {
            listNBT.add(getSouls().remove().save());
        }
        compoundNBT.put("SoulsForReviving", listNBT);
    }

    default void loadSouls(CompoundNBT compoundNBT) {
        ListNBT listNBT = compoundNBT.getList("SoulsForReviving", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT soulNBT = listNBT.getCompound(i);
            S soul = loadSoul(soulNBT);
            if (soul != null) {
                getSouls().add(soul);
            }
        }
    }

    default double getReviveDistanceSqr() {
        return getReviveDistance() * getReviveDistance();
    }
}
