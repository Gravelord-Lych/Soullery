package lych.soullery.extension.highlight;

import lych.soullery.Soullery;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.mixin.IEntityMixin;
import lych.soullery.world.event.manager.EventManager;
import lych.soullery.world.event.manager.NotLoadedException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

public class EntityHighlightManager extends WorldSavedData {
    static final int WAIT_TICKS = 40;
    private static final String NAME = "EntityHighlightManager";
    private static final int SAVE_FREQ = 40;
    public static final Marker HIGHLIGHTER = MarkerManager.getMarker(NAME);
    private final Map<UUID, PriorityQueue<Highlighter>> preparatoryHighlighters = new HashMap<>(64);
    private final Map<UUID, Long> invalidUUIDs = new HashMap<>();
    private final ServerWorld level;
    private int dataSaver = SAVE_FREQ;

    public EntityHighlightManager(ServerWorld level) {
        super(NAME);
        this.level = level;
    }

    public static EntityHighlightManager get(ServerWorld level) {
        DimensionSavedDataManager storage = level.getDataStorage();
        return storage.computeIfAbsent(() -> new EntityHighlightManager(level), NAME);
    }

    public boolean highlight(HighlighterType type, Entity entity) {
        return highlight(type, entity, 1);
    }

    public boolean isHighlighted(Entity entity) {
        return isHighlighted(entity.getUUID());
    }

    public boolean isHighlighted(UUID uuid) {
        return preparatoryHighlighters.containsKey(uuid);
    }

    @Nullable
    private PriorityQueue<Highlighter> getHighlighterQueue(Entity entity) {
        return getHighlighterQueue(entity.getUUID());
    }

    @Nullable
    private PriorityQueue<Highlighter> getHighlighterQueue(UUID uuid) {
        return preparatoryHighlighters.get(uuid);
    }

    /**
     * @return True if added new {@link Highlighter highlighter}
     */
    public boolean highlight(HighlighterType type, Entity entity, long highlightTicks) {
        UUID uuid = entity.getUUID();
        PriorityQueue<Highlighter> queue = getHighlighterQueue(uuid);
//      There have already been some highlighters on the entity.
        if (queue != null && !queue.isEmpty()) {
            Highlighter activeHighlighter = queue.peek();
//          The active highlighter's type is the specified type
            if (activeHighlighter.getType() == type) {
                activeHighlighter.setHighlightTicks(highlightTicks);
                return false;
            } else {
                Highlighter highlighter = queue.stream().filter(h -> h.getType() == type).findFirst().orElse(null);
//              A preparatory highlighter's type is the specified type
                if (highlighter != null) {
                    highlighter.setHighlightTicks(highlightTicks);
                    return false;
                } else {
                    queue.add(type.create(uuid, highlightTicks));
                    return true;
                }
            }
        } else {
            preparatoryHighlighters.put(uuid, new PriorityQueue<>());
            Objects.requireNonNull(getHighlighterQueue(uuid)).add(type.create(uuid, highlightTicks));
            return true;
        }
    }

    public boolean unhighlightAll(Entity entity) {
        boolean removed = preparatoryHighlighters.remove(entity.getUUID()) != null;
        if (removed) {
            syncHighlightColor(entity, null);
        }
        return removed;
    }

    public boolean unhighlightActive(Entity entity) {
        PriorityQueue<Highlighter> queue = getHighlighterQueue(entity);
        if (queue == null || queue.isEmpty()) {
            return clearExistColor(entity);
        }
        queue.remove();
//      Empty queue handler
        if (queue.isEmpty()) {
            preparatoryHighlighters.remove(entity.getUUID());
            syncHighlightColor(entity, null);
        }
        return true;
    }

    public boolean unhighlightType(Entity entity, HighlighterType type) {
        PriorityQueue<Highlighter> queue = getHighlighterQueue(entity);
        if (queue == null || queue.isEmpty()) {
            return clearExistColor(entity);
        }
        if (queue.peek().getType() == type) {
            return unhighlightActive(entity);
        } else {
            queue.removeIf(h -> h.getType() == type);
//          No empty queue handlers here because there is always an active highlighter
            return true;
        }
    }

    private static boolean clearExistColor(Entity entity) {
        boolean hasColor = ((IEntityMixin) entity).getHighlightColor().isPresent();
        syncHighlightColor(entity, null);
        return hasColor;
    }

    public void tick() {
        if (!EventManager.canTick()) {
            return;
        }
        if (dataSaver > 0) {
            dataSaver--;
        } else {
            setDirty();
            dataSaver = SAVE_FREQ;
        }
        Iterator<Map.Entry<UUID, PriorityQueue<Highlighter>>> itr = preparatoryHighlighters.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<UUID, PriorityQueue<Highlighter>> entry = itr.next();
            UUID uuid = entry.getKey();
            Entity entity = level.getEntity(uuid);
            PriorityQueue<Highlighter> queue = entry.getValue();
            if (queue == null || queue.isEmpty()) {
                itr.remove();
                continue;
            }
            if (entity == null) {
                invalidUUIDs.putIfAbsent(uuid, level.getGameTime() + WAIT_TICKS);
            } else {
                invalidUUIDs.remove(entity.getUUID());
            }
            if (invalidUUIDs.containsKey(uuid)) {
                continue;
            }
            Highlighter highlighter = queue.element();
            Color color = highlighter.getColor(level, new PriorityQueue<>(queue));
            boolean alive = EntityUtils.isAlive(entity);
            if (alive) {
                syncHighlightColor(entity, color);
            }
            boolean dead = EntityUtils.isDead(entity);
            if (dead || color == null) {
                itr.remove();
                if (dead) {
                    continue;
                }
                syncHighlightColor(entity, null);
            }
//          Tick preparatory highlighters
            for (Iterator<Highlighter> iterator = queue.iterator(); iterator.hasNext(); ) {
                Highlighter ph = iterator.next();
                if (ph != highlighter) {
                    if (ph.getHighlightTicks() < 0) {
                        iterator.remove();
                    } else {
                        ph.setHighlightTicks(ph.getHighlightTicks() - 1);
                    }
                }
            }
        }
        Iterator<Map.Entry<UUID, Long>> invItr = invalidUUIDs.entrySet().iterator();
        while (invItr.hasNext()) {
            Map.Entry<UUID, Long> entry = invItr.next();
            if (level.getGameTime() > entry.getValue()) {
                preparatoryHighlighters.remove(entry.getKey());
                invItr.remove();
            }
        }
    }

    private static void syncHighlightColor(@Nullable Entity entity, @Nullable Color color) {
        if (entity != null) {
            ((IEntityMixin) entity).setHighlightColor(color);
        }
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        if (compoundNBT.contains(NAME + "Data", Constants.NBT.TAG_LIST)) {
            preparatoryHighlighters.clear();
            ListNBT listNBT = compoundNBT.getList(NAME + "Data", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT singleNBT = listNBT.getCompound(i);
                UUID entityUUID = singleNBT.getUUID("EntityUUID");
                try {
                    loadPreparatoryHighlighters(entityUUID, singleNBT.getList("PreparatoryHighlighters", Constants.NBT.TAG_COMPOUND));
                } catch (NotLoadedException e) {
                    Soullery.LOGGER.warn(HIGHLIGHTER, "Failed to highlight entity " + entityUUID, e.getCause());
                }
            }
        }
    }

    private void loadPreparatoryHighlighters(UUID uuid, ListNBT preparatoryHighlightersNBT) throws NotLoadedException {
        PriorityQueue<Highlighter> queue = new PriorityQueue<>();
        for (int i = preparatoryHighlightersNBT.size() - 1; i >= 0; i--) {
            CompoundNBT preparatoryNBT = preparatoryHighlightersNBT.getCompound(i);
            Highlighter highlighter = loadSingleHighlighter(preparatoryNBT, uuid);
            queue.add(highlighter);
        }
        if (!queue.isEmpty()) {
            preparatoryHighlighters.put(uuid, queue);
        }
    }

    private Highlighter loadSingleHighlighter(CompoundNBT singleNBT, UUID entityUUID) throws NotLoadedException {
        UUID highlighterTypeUUID = singleNBT.getUUID("HighlighterTypeUUID");
        HighlighterType type = HighlighterType.get(highlighterTypeUUID);
        CompoundNBT highlighterData = singleNBT.getCompound("HighlighterData");
        return type.load(entityUUID, highlighterData);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ListNBT listNBT = new ListNBT();
        for (Map.Entry<UUID, PriorityQueue<Highlighter>> entry : preparatoryHighlighters.entrySet()) {
            CompoundNBT singleNBT = new CompoundNBT();
            singleNBT.putUUID("EntityUUID", entry.getKey());
            ListNBT preparatoryHighlightersNBT = new ListNBT();
            savePreparatoryHighlighters(entry.getKey(), preparatoryHighlightersNBT);
            singleNBT.put("PreparatoryHighlighters", preparatoryHighlightersNBT);
            listNBT.add(singleNBT);
        }
        compoundNBT.put(NAME + "Data", listNBT);
        return compoundNBT;
    }

    private void savePreparatoryHighlighters(UUID uuid, ListNBT preparatoryHighlightersNBT) {
        PriorityQueue<Highlighter> queue = preparatoryHighlighters.get(uuid);
        if (queue == null || queue.isEmpty()) {
            return;
        }
        queue = new PriorityQueue<>(queue);
        while (!queue.isEmpty()) {
            Highlighter highlighter = queue.remove();
            CompoundNBT preparatoryNBT = new CompoundNBT();
            saveSingleHighlighter(highlighter, preparatoryNBT);
            preparatoryHighlightersNBT.add(preparatoryNBT);
        }
    }

    private void saveSingleHighlighter(Highlighter highlighter, CompoundNBT singleNBT) {
        singleNBT.putUUID("HighlighterTypeUUID", highlighter.getType().getUUID());
        singleNBT.put("HighlighterData", highlighter.save());
    }
}
