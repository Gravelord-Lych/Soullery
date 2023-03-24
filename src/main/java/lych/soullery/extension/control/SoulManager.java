package lych.soullery.extension.control;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.extension.highlight.EntityHighlightManager;
import lych.soullery.extension.highlight.HighlighterType;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IBrainMixin;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SoulManager extends WorldSavedData {
    static final Marker MARKER = MarkerManager.getMarker("SoulManager");
    private static final String NAME = "SoulManager";
    private final ServerWorld level;
    private final Map<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> controllers = new HashMap<>();
    private final Times times = new Times(this);

    public SoulManager(ServerWorld level) {
        super(NAME);
        this.level = level;
    }

    private static PriorityQueue<Controller<?>> makeQueue() {
        return new PriorityQueue<>(Comparator.comparingInt(Controller::getPriority));
    }

    public void tick() {
        for (Iterator<Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>>> itr = controllers.entrySet().iterator(); itr.hasNext(); ) {
            Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry = itr.next();
            MobEntity mob = getMob(entry);
            PriorityQueue<Controller<?>> controllers = getControllers(entry);
            Controller<?> controller = controllers.element();
            PlayerEntity playerController = Utils.applyIfNonnull(mob, this::getPlayerController);
            boolean dead = EntityUtils.isDead(mob);
            boolean noMobFound = !controller.isPreparing() && mob == null;
            boolean noPlayerFound = !controller.isPreparing() && mob != null && !EntityUtils.isAlive(playerController);
            if (dead || noMobFound || noPlayerFound) {
                itr.remove();
                stopControlling(mob, controller, true, EntityUtils.isAlive(mob));
                setDirty();
                continue;
            }
            if (controller.isPreparing() && mob != null && playerController != null) {
                startControlling(mob, controller);
                controller.setPreparing(false);
                setDirty();
            }
            if (!controller.tick()) {
                itr.remove();
                stopControlling(mob, controller, true, EntityUtils.isAlive(mob));
                setDirty();
                continue;
            }
            if (mob != null) {
                EntityHighlightManager.get(level).highlight(HighlighterType.SOUL_CONTROL, mob);
            }
        }
        times.tick();
    }

    private UUID getMobUUID(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        return entry.getKey();
    }

    @Nullable
    private MobEntity getMob(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        UUID uuid = getMobUUID(entry);
        return Optional.ofNullable(level.getEntity(uuid)).filter(entity -> entity instanceof MobEntity).map(MobEntity.class::cast).orElse(null);
    }

    private UUID getPlayerUUID(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        return entry.getValue().getFirst();
    }

    private PriorityQueue<Controller<?>> getControllers(Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry) {
        return entry.getValue().getSecond();
    }

    public static SoulManager get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(() -> new SoulManager(world), NAME);
    }

    @Nullable
    public Pair<UUID, PriorityQueue<Controller<?>>> getControllerData(MobEntity mob) {
        return controllers.get(mob.getUUID());
    }

    @Nullable
    public <T extends MobEntity> Controller<? super T> add(T mob, PlayerEntity player, @Nullable ControllerType<? super T> type) {
        if (type == null) {
            return null;
        }
//      A mob can only be controlled by one player.
        if (controllers.containsKey(mob.getUUID()) && controllers.get(mob.getUUID()).getSecond().stream().anyMatch(c -> c.getType() == type)) {
            return null;
        }
        Pair<UUID, PriorityQueue<Controller<?>>> pair = controllers.computeIfAbsent(mob.getUUID(), uuid -> Pair.of(player.getUUID(), makeQueue()));
        Controller<? super T> controller = type.create(mob, player);
        pair.getSecond().add(controller);
        setDirty();
        startControlling(mob, controller);
        return controller;
    }

    public List<Controller<?>> getActiveControllersFlatly(PlayerEntity player) {
        return getActiveControllers(player).stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<PriorityQueue<Controller<?>>> getActiveControllers(PlayerEntity player) {
        return controllers.entrySet().stream().filter(e -> Objects.equals(getPlayerUUID(e), player.getUUID())).map(e -> e.getValue().getSecond()).collect(Collectors.toList());
    }

    public Set<MobEntity> getControllingMobs(PlayerEntity player) {
        return controllers.entrySet().stream().filter(e -> Objects.equals(getPlayerUUID(e), player.getUUID())).map(this::getMob).filter(EntityUtils::isAlive).collect(Collectors.toSet());
    }

    @Nullable
    public PlayerEntity getPlayerController(MobEntity mob) {
        Pair<UUID, PriorityQueue<Controller<?>>> pair = controllers.get(mob.getUUID());
        if (pair == null) {
            return null;
        }
        return Utils.applyIfNonnull(pair.getFirst(), level::getPlayerByUUID);
    }

    public PriorityQueue<Controller<?>> getControllers(@Nullable MobEntity mob) {
        if (mob == null) {
            return new PriorityQueue<>();
        }
        Pair<UUID, PriorityQueue<Controller<?>>> pair = controllers.get(mob.getUUID());
        if (pair == null) {
            return new PriorityQueue<>();
        }
        return pair.getSecond();
    }

    public boolean hasControllers(@Nullable MobEntity mob) {
        if (mob == null) {
            return false;
        }
        if (controllers.get(mob.getUUID()) == null) {
            return false;
        }
        return !getControllers(mob).isEmpty();
    }

    public void remove(MobEntity mob) {
        Pair<UUID, PriorityQueue<Controller<?>>> removedPair = controllers.remove(mob.getUUID());
        if (removedPair != null) {
            Controller<?> activeController = removedPair.getSecond().peek();
            removedPair.getSecond().forEach(c -> stopControlling(mob, c, c == activeController, EntityUtils.isAlive(mob)));
            setDirty();
        }
    }

    public void remove(MobEntity mob, ControllerType<?> type) {
        removeIf(mob, c -> c.getType() == type, true);
    }

    public void removeIf(MobEntity mob, Predicate<? super Controller<?>> removePredicate) {
        removeIf(mob, removePredicate, false);
    }

    private void removeIf(MobEntity mob, Predicate<? super Controller<?>> removePredicate, boolean checkDuplication) {
        if (controllers.get(mob.getUUID()) == null) {
            return;
        }

        PriorityQueue<Controller<?>> queue = controllers.get(mob.getUUID()).getSecond();

        Controller<?> activeController = queue.peek();
        Controller<?> removedController = null;
        List<Controller<?>> removedControllers = new ArrayList<>();

        Iterator<Controller<?>> itr = queue.iterator();
        while (itr.hasNext()) {
            Controller<?> next = itr.next();
            if (removePredicate.test(next)) {
                if (removedController != null && checkDuplication) {
                    throw new AssertionError(String.format("Controller %s exists", removedController));
                }
                removedController = next;
                removedControllers.add(next);
                itr.remove();
            }
        }

        removedControllers.sort(Comparator.comparingInt(Controller::getPriority));
        if (removedController != null) {
            boolean active = activeController == removedControllers.get(0);
            if (queue.isEmpty()) {
                for (Controller<?> removedControllerIn : removedControllers) {
                    postRemoval(mob, removedControllerIn, active);
//                  The rest elements can never be active because their priority cannot be the smallest
                    active = false;
                }
                remove(mob);
            } else {
                for (Controller<?> removedControllerIn : removedControllers) {
                    postRemoval(mob, removedControllerIn, active);
                    active = false;
                }
                startControlling(mob, queue.element());
            }
        }
    }

    private void postRemoval(MobEntity mob, Controller<?> controller, boolean active) {
        stopControlling(mob, controller, active, EntityUtils.isAlive(mob));
        setDirty();
    }

    void startControlling(MobEntity mob, Controller<?> controller) {
        ((IBrainMixin<?>) mob.getBrain()).setDisabledIfValid(true);
        controller.startControlling(mob, ((IGoalSelectorMixin) mob.goalSelector).getAlt(), (((IGoalSelectorMixin) mob.targetSelector)).getAlt());
        if (!controller.overrideBehaviorGoals()) {
            ((IGoalSelectorMixin) mob.goalSelector).transferGoals();
        }
        if (!controller.overrideTargetGoals()) {
            ((IGoalSelectorMixin) mob.targetSelector).transferGoals();
        }
    }

    void stopControlling(MobEntity mob, Controller<?> controller, boolean stoppedActiveController, boolean mobAlive) {
        if (stoppedActiveController && mobAlive) {
            ((IBrainMixin<?>) mob.getBrain()).setDisabledIfValid(false);
            ((IGoalSelectorMixin) mob.goalSelector).removeAllAltGoals();
            ((IGoalSelectorMixin) mob.targetSelector).removeAllAltGoals();
        }
        controller.stopControlling(mob, mobAlive);
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("Controllers", Constants.NBT.TAG_LIST)) {
            controllers.clear();
            ListNBT controllersNBT = compoundNBT.getList("Controllers", Constants.NBT.TAG_LIST);
            for (int i = 0; i < controllersNBT.size(); i++) {
                ListNBT controllersForOneMobNBT = controllersNBT.getList(i);
                for (int j = 0; j < controllersForOneMobNBT.size(); j++) {
                    CompoundNBT singleNBT = controllersForOneMobNBT.getCompound(j);
                    loadOne(singleNBT);
                }
            }
        }
        if (compoundNBT.contains("Times", Constants.NBT.TAG_COMPOUND)) {
            times.deserializeNBT(compoundNBT.getCompound("Times"));
        }
    }

    private void loadOne(CompoundNBT singleNBT) {
        String name = singleNBT.getString("Type");
        ControllerType<?> type = loadControllerType(name);
        if (type == null) {
            return;
        }
        CompoundNBT controllerData = singleNBT.getCompound("ControllerData");
        Controller<?> controller = type.load(controllerData, level);
        controllers.computeIfAbsent(controller.getMobUUID(), u -> Pair.of(controller.getPlayerUUID(), makeQueue())).getSecond().add(controller);
        controller.setPreparing(true);
    }

    @Nullable
    private static ControllerType<?> loadControllerType(String name) {
        ResourceLocation location;
        try {
            location = new ResourceLocation(name);
        } catch (ResourceLocationException e) {
            if (ConfigHelper.shouldFailhard()) {
                throw new ResourceLocationException(ConfigHelper.FAILHARD_MESSAGE + "Failed to parse a controller's registry name: " + e.getMessage());
            }
            Soullery.LOGGER.error(MARKER, "Failed to parse a controller's registry name", e);
            return null;
        }
        ControllerType<?> type = ControllerType.byRegistryName(location);
        if (type == null) {
            Soullery.LOGGER.warn(MARKER, "Found unknown controller {}, ignored", location);
            return null;
        }
        return type;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ListNBT controllersNBT = new ListNBT();
        for (Map.Entry<UUID, Pair<UUID, PriorityQueue<Controller<?>>>> entry : controllers.entrySet()) {
            ListNBT controllersForOneMobNBT = new ListNBT();
            for (Controller<?> controller : getControllers(entry)) {
                CompoundNBT singleNBT = new CompoundNBT();
                singleNBT.putString("Type", controller.getRegistryName().toString());
                singleNBT.put("ControllerData", controller.save());
                controllersForOneMobNBT.add(singleNBT);
            }
            controllersNBT.add(controllersForOneMobNBT);
        }
        compoundNBT.put("Controllers", controllersNBT);
        compoundNBT.put("Times", times.serializeNBT());
        return compoundNBT;
    }

    public Times getTimes() {
        return times;
    }

    public static class Times implements INBTSerializable<CompoundNBT> {
        private final SoulManager manager;
        private final Table<UUID, ControllerType<?>, TimeEntry> timeLimits = HashBasedTable.create();
        private int globalTickCount;
        private int tickCount;
        private boolean preparing;

        public Times(SoulManager manager) {
            this.manager = manager;
        }

        public void tick() {
            globalTickCount++;
            tickCount++;
            if (preparing) {
                if (timeLimits.rowKeySet().stream().map(manager.level::getEntity).allMatch(Objects::nonNull)) {
                    preparing = false;
                } else if (tickCount > 400) {
                    Soullery.LOGGER.warn(MARKER, "Some entities that were controlled seem to be missing. So ignore them");
                    preparing = false;
                }
                return;
            }
            for (Iterator<Table.Cell<UUID, ControllerType<?>, TimeEntry>> itr = timeLimits.cellSet().iterator(); itr.hasNext(); ) {
                Table.Cell<UUID, ControllerType<?>, TimeEntry> cell = itr.next();
                Pair<UUID, PriorityQueue<Controller<?>>> controllersPair = manager.controllers.get(cell.getRowKey());
                if (controllersPair == null || controllersPair.getSecond().stream().noneMatch(c -> c.getType() == cell.getColumnKey())) {
                    itr.remove();
                    continue;
                }
                if (timeRemaining(cell.getValue().end) <= 0) {
                    Entity entity = manager.level.getEntity(cell.getRowKey());
                    if (entity instanceof MobEntity) {
                        manager.remove((MobEntity) entity, cell.getColumnKey());
                    }
                    itr.remove();
                }
            }
        }

        public void setTime(MobEntity mob, ControllerType<?> type, int time) {
            timeLimits.put(mob.getUUID(), type, new TimeEntry(globalTickCount, globalTickCount + time));
        }

        public int timeRemaining(MobEntity mob, ControllerType<?> type) {
            if (!timeLimits.contains(mob.getUUID(), type)) {
                return 0;
            }
            return timeRemaining(timeLimits.get(mob.getUUID(), type).end);
        }

        public double getRemainingPercent(MobEntity mob, ControllerType<?> type) {
            if (!timeLimits.contains(mob.getUUID(), type)) {
                return 0;
            }
            TimeEntry entry = timeLimits.get(mob.getUUID(), type);
            return timeRemaining(entry.end) / (double) entry.getDuration();
        }

        private int timeRemaining(int timeEnd) {
            return timeEnd - globalTickCount;
        }

        public boolean hasTimeRemaining(MobEntity mob, ControllerType<?> type) {
            return timeRemaining(mob, type) > 0;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putInt("TickCount", globalTickCount);
            ListNBT timeLimitsNBT = new ListNBT();
            for (Table.Cell<UUID, ControllerType<?>, TimeEntry> cell : timeLimits.cellSet()) {
                CompoundNBT singleEntryNBT = new CompoundNBT();
                singleEntryNBT.putUUID("MobUUID", cell.getRowKey());
                singleEntryNBT.putString("Type", cell.getColumnKey().getRegistryName().toString());
                singleEntryNBT.putInt("StartTime", cell.getValue().start);
                singleEntryNBT.putInt("EndTime", cell.getValue().end);
                timeLimitsNBT.add(singleEntryNBT);
            }
            compoundNBT.put("TimeLimits", timeLimitsNBT);
            return compoundNBT;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            globalTickCount = nbt.getInt("TickCount");
            if (nbt.contains("TimeLimits", Constants.NBT.TAG_LIST)) {
                timeLimits.clear();
                ListNBT timeLimitsNBT = nbt.getList("TimeLimits", Constants.NBT.TAG_COMPOUND);
                for (int i = 0; i < timeLimitsNBT.size(); i++) {
                    CompoundNBT singleEntryNBT = timeLimitsNBT.getCompound(i);
                    ControllerType<?> type = loadControllerType(singleEntryNBT.getString("Type"));
                    if (type == null) {
                        continue;
                    }
                    UUID uuid = singleEntryNBT.getUUID("MobUUID");
                    int startTime = singleEntryNBT.getInt("StartTime");
                    int endTime = singleEntryNBT.getInt("EndTime");
                    timeLimits.put(uuid, type, new TimeEntry(startTime, endTime));
                }
            }
            preparing = true;
        }

        private static class TimeEntry {
            private final int start;
            private final int end;

            private TimeEntry(int start, int end) {
                this.start = start;
                this.end = end;
            }

            private int getDuration() {
                return end - start;
            }
        }
    }
}
