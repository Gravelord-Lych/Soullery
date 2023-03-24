package lych.soullery.entity;

import com.google.common.base.MoreObjects;
import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

@SuppressWarnings("unchecked")
public class Soul<T extends LivingEntity> {
    private final EntityType<? extends T> type;
    private final CompoundNBT nbt;

    public Soul(T entity) {
        this((EntityType<? extends T>) entity.getType(), entity.saveWithoutId(new CompoundNBT()));
    }

    public Soul(EntityType<? extends T> type, CompoundNBT nbt) {
        this.type = type;
        this.nbt = nbt;
    }

    public static <T extends LivingEntity> List<T> reviveAll(ServerWorld world, boolean spawnRevivedEntity, Queue<Soul<T>> souls) {
        List<T> list = new ArrayList<>();
        for (Soul<T> soul : souls) {
            list.add(soul.revive(world));
        }
        if (spawnRevivedEntity) {
            list.forEach(world::addFreshEntity);
        }
        return list;
    }

    @Nullable
    public T revive(ServerWorld world) {
        T entity = type.create(world);
        if (entity != null) {
            entity.load(nbt);
            entity.setHealth(entity.getMaxHealth());
            acceptEntity(entity);
        }
        return entity;
    }

    protected static void clearEffectsAndFire(LivingEntity entity) {
        entity.removeAllEffects();
        entity.setRemainingFireTicks(0);
        ((IEntityMixin) entity).setOnSoulFire(false);
    }

    protected void acceptEntity(T entity) {
        clearEffectsAndFire(entity);
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putString("EntityType", Utils.getRegistryName(type).toString());
        compoundNBT.put("EntityNBT", nbt);
        return compoundNBT;
    }

    @Nullable
    public static <T extends LivingEntity> Soul<T> load(CompoundNBT compoundNBT) {
        ResourceLocation location;
        try {
            location = new ResourceLocation(compoundNBT.getString("EntityType"));
        } catch (ResourceLocationException e) {
            if (ConfigHelper.shouldFailhard()) {
                throw new RuntimeException(ConfigHelper.FAILHARD_MESSAGE + "Failed to load a soul", e);
            }
            Soullery.LOGGER.error("Failed to load a soul", e);
            return null;
        }
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(location);
        if (type == null) {
            Soullery.LOGGER.warn("Found unknown entity {} when loading a soul, ignored", location);
            return null;
        }
        EntityType<? extends T> castedType;
        try {
            castedType = (EntityType<? extends T>) type;
        } catch (ClassCastException e) {
            if (ConfigHelper.shouldFailhard()) {
                throw new RuntimeException(ConfigHelper.FAILHARD_MESSAGE + "EntityType does not match the entity", e);
            }
            Soullery.LOGGER.error("EntityType does not match the entity because {}", e.getMessage());
            return null;
        }
        CompoundNBT nbt = compoundNBT.getCompound("EntityNBT");
        return new Soul<>(castedType, nbt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Soul<?> soul = (Soul<?>) o;
        return Objects.equals(type, soul.type) && Objects.equals(nbt, soul.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, nbt);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("nbt", nbt)
                .toString();
    }

    public String getSimpleName() {
        return MoreObjects.toStringHelper(this)
                .add("typeName", I18n.get(type.getDescriptionId()))
                .add("nbtSize", nbt.size())
                .toString();
    }
}
