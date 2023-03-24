package lych.soullery.world.event.manager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.world.event.WorldEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Iterator;

public abstract class EventManager<T extends WorldEvent> extends WorldSavedData implements Iterable<T> {
    private static final Marker EVENTS = MarkerManager.getMarker("EventManager");
    protected final Int2ObjectMap<T> eventMap = new Int2ObjectOpenHashMap<>();
    protected final ServerWorld level;
    private int nextAvailableID;
    private int tick;
    private static boolean canTick;

    public EventManager(String id, ServerWorld level) {
        super(id);
        this.level = level;
        this.nextAvailableID = 1;
        setDirty();
    }

    public static void runEvents() {
        if (canTick) {
            return;
        }
        canTick = true;
        Soullery.LOGGER.info(EVENTS, "Started ticking events");
    }

    public static boolean canTick() {
        return canTick;
    }

    public T get(int key) {
        return eventMap.get(key);
    }

    public void tick() {
//      Tick events after players logged in
        if (!canTick()) {
            return;
        }

        tick++;

        Iterator<T> itr = eventMap.values().iterator();
        while (itr.hasNext()) {
            T event = itr.next();
            if (event.isStopped()) {
                itr.remove();
                setDirty();
            } else {
                event.tick();
            }
        }

        if (tick % 200 == 0) {
            setDirty();
        }
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        nextAvailableID = compoundNBT.getInt("NextAvailableID");
        tick = compoundNBT.getInt("Tick");
        ListNBT listNBT = compoundNBT.getList("Events", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT eventNBT = listNBT.getCompound(i);
            T event = null;
            try {
                event = loadEvent(level, eventNBT);
            } catch (UnknownObjectException e) {
                Soullery.LOGGER.warn(EVENTS, "Unknown Event: {}, ignored", e.getUnknownLocation());
            } catch (NotLoadedException e) {
                if (ConfigHelper.shouldFailhard()) {
                    throw new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + "Event not loaded", e);
                }
                Soullery.LOGGER.error(EVENTS, StringUtils.isBlank(e.getMessage()) ? "Event not loaded, ignored" : String.format("Event not loaded because %s, ignored", e.getMessage()));
            }
            if (event != null) {
                eventMap.put(event.getId(), event);
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        compoundNBT.putInt("NextAvailableID", nextAvailableID);
        compoundNBT.putInt("Tick", tick);
        ListNBT listNBT = new ListNBT();
        for (T event : eventMap.values()) {
            listNBT.add(event.save());
        }
        compoundNBT.put("Events", listNBT);
        return compoundNBT;
    }

    protected abstract T loadEvent(ServerWorld world, CompoundNBT compoundNBT) throws NotLoadedException;

    protected int getUniqueID() {
        return ++nextAvailableID;
    }

    @Nullable
    public T getNearbyEvent(BlockPos pos, double distance) {
        T event = null;
        double requiredDistSqr = distance * distance;
        for (T possibleEvent : eventMap.values()) {
            double distSqr = possibleEvent.getCenter().distSqr(pos);
            if (possibleEvent.isActive() && distSqr <= requiredDistSqr) {
                event = possibleEvent;
                requiredDistSqr = distSqr;
            }
        }
        return event;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return eventMap.values().iterator();
    }
}
