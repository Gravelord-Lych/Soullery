package lych.soullery.capability;

import lych.soullery.Soullery;
import lych.soullery.api.capability.IControlledMobData;
import lych.soullery.extension.control.Controller;
import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.control.SoulManager;
import lych.soullery.extension.highlight.EntityHighlightManager;
import lych.soullery.extension.highlight.HighlighterType;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.mixin.IBrainMixin;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static lych.soullery.extension.control.SoulManager.MARKER;

public class ControlledMobData<T extends MobEntity> implements IControlledMobData<T> {
    private final PriorityQueue<Controller<?>> controllers = new PriorityQueue<>();
    private final T mob;
    private final ServerWorld level;
    private final Timer timer = new TimerImpl();

    public ControlledMobData(T mob, ServerWorld level) {
        this.mob = mob;
        this.level = level;
    }

    @Override
    public PriorityQueue<Controller<?>> getControllers() {
        return controllers;
    }

    @Nullable
    @Override
    public Controller<? super T> add(PlayerEntity player, @Nullable ControllerType<? super T> type) {
        if (type == null) {
            return null;
        }
        if (getPlayerController() != null && getPlayerController() != player) {
            Soullery.LOGGER.info(MARKER, "Player {} is trying to control {} who has been already controlled by {}", player.getDisplayName(), mob.getDisplayName(), getPlayerController().getDisplayName());
            return null;
        }
        PriorityQueue<Controller<?>> controllers = getControllers();
//      A mob can only be controlled by one player and one controller for each type.
        if (getControllers().stream().anyMatch(c -> c.getType() == type)) {
            return null;
        }
        Controller<? super T> controller = type.create(mob, player);
        Controller<?> oldController = controllers.peek();
        if (oldController != null) {
            stopControlling(controller, true);
        }
        controllers.add(controller);
        startControlling(controller);
        return controller;
    }

    @Override
    public void tick() {
        PriorityQueue<Controller<?>> controllers = getControllers();

        timer.tick();
        if (controllers.isEmpty()) {
            return;
        }

        MobEntity mob = this.mob;
        ServerWorld level = this.level;

        Controller<?> controller = controllers.element();
        PlayerEntity playerController = getPlayerController();

        if (!updateExistence(mob, controller, playerController)) {
            return;
        }

        if (!tickActiveController(controllers, mob, controller)) {
            return;
        }

        highlight(mob, level, playerController);
    }

    private boolean updateExistence(MobEntity mob, Controller<?> controller, @Nullable PlayerEntity playerController) {
        boolean dead = EntityUtils.isDead(mob);
        boolean noPlayerFound = !controller.isPreparing() && !EntityUtils.isAlive(playerController);
        if (dead || noPlayerFound) {
            stopControlling(controller, true);
            if (dead) {
                controller.handleDeathRaw(mob, playerController);
            }
            return false;
        }
        if (controller.isPreparing() && playerController != null) {
            startControlling(controller);
            controller.setPreparing(false);
        }
        return true;
    }

    private boolean tickActiveController(PriorityQueue<Controller<?>> controllers, MobEntity mob, Controller<?> controller) {
        if (!controller.tick()) {
            controllers.remove();
            stopControlling(controller, true);
            if (!controllers.isEmpty()) {
                startControlling(controllers.element());
            }
            return false;
        }
        return true;
    }

    private static void highlight(MobEntity mob, ServerWorld level, @Nullable PlayerEntity playerController) {
        EntityHighlightManager.get(level).highlight(HighlighterType.SOUL_CONTROL, mob);
        if (playerController != null) {
            EntityHighlightManager.get(level).highlight(HighlighterType.SOUL_CONTROLLER, playerController);
        }
    }

    @Nullable
    @Override
    public Controller<?> removeIf(Predicate<? super Controller<?>> removePredicate, boolean checkDuplication) {
        PriorityQueue<Controller<?>> controllers = getControllers();

        Controller<?> activeController = controllers.peek();
        Controller<?> removedController = null;
        List<Controller<?>> removedControllers = new ArrayList<>();

        Iterator<Controller<?>> itr = controllers.iterator();
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
            handleAllAndRestartInactive(controllers, activeController, removedControllers);
        }
        return removedController;
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    private void handleAllAndRestartInactive(PriorityQueue<Controller<?>> controllers, Controller<?> activeController, List<Controller<?>> removedControllers) {
        boolean active = activeController == removedControllers.get(0);
        for (Controller<?> removedControllerIn : removedControllers) {
            stopControlling(removedControllerIn, active);
//          The rest elements can never be active because their priority cannot be the highest
            active = false;
        }
        if (!controllers.isEmpty()) {
            startControlling(controllers.element());
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        ListNBT controllersNBT = new ListNBT();
        for (Controller<?> controller : getControllers()) {
            CompoundNBT singleNBT = new CompoundNBT();
            singleNBT.putString("Type", controller.getRegistryName().toString());
            singleNBT.put("ControllerData", controller.save());
            controllersNBT.add(singleNBT);
        }
        compoundNBT.put("Controllers", controllersNBT);
        compoundNBT.put("Timer", getTimer().serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("Controllers", Constants.NBT.TAG_LIST)) {
            controllers.clear();
            ListNBT controllersNBT = nbt.getList("Controllers", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < controllersNBT.size(); i++) {
                CompoundNBT singleNBT = controllersNBT.getCompound(i);
                String name = singleNBT.getString("Type");
                ControllerType<?> type = SoulManager.loadControllerType(name);
                if (type == null) {
                    return;
                }
                CompoundNBT controllerData = singleNBT.getCompound("ControllerData");
                Controller<?> controller = type.load(controllerData, level);
                controllers.add(controller);
                controller.setPreparing(true);
            }
        }
        if (nbt.contains("Timer", Constants.NBT.TAG_COMPOUND)) {
            getTimer().deserializeNBT(nbt.getCompound("Timer"));
        }
    }

    private void startControlling(Controller<?> controller) {
        if (((IBrainMixin<?>) mob.getBrain()).isValidBrain()) {
            if (controller.shouldDisableBrain()) {
                ((IBrainMixin<?>) mob.getBrain()).setDisabledIfValid(true);
            }
            if (controller.shouldDisableTargetTasksAdditionally()) {
                ((IBrainMixin<?>) mob.getBrain()).disableTargetTasksRaw(level, mob, level.getGameTime());
            }
        }
        controller.startControlling(mob, ((IGoalSelectorMixin) mob.goalSelector).getAlt(), (((IGoalSelectorMixin) mob.targetSelector)).getAlt());
        if (!controller.overrideBehaviorGoals()) {
            ((IGoalSelectorMixin) mob.goalSelector).transferGoals();
        }
        if (!controller.overrideTargetGoals()) {
            ((IGoalSelectorMixin) mob.targetSelector).transferGoals();
        }
    }

    private void stopControlling(Controller<?> controller, boolean stoppedActiveController) {
        stopControlling(controller, stoppedActiveController, EntityUtils.isAlive(mob));
    }

    private void stopControlling(Controller<?> controller, boolean stoppedActiveController, boolean mobAlive) {
        if (stoppedActiveController && mobAlive) {
            if (((IBrainMixin<?>) mob.getBrain()).isValidBrain()) {
                if (controller.shouldDisableBrain()) {
                    ((IBrainMixin<?>) mob.getBrain()).setDisabledIfValid(false);
                }
                if (controller.shouldDisableTargetTasksAdditionally()) {
                    ((IBrainMixin<?>) mob.getBrain()).restartTargetTasks();
                }
            }
            ((IGoalSelectorMixin) mob.goalSelector).removeAllAltGoals();
            ((IGoalSelectorMixin) mob.targetSelector).removeAllAltGoals();
        }
        controller.stopControlling(mob, mobAlive);
    }

    private class TimerImpl implements Timer {
        private final Map<ControllerType<?>, TimeEntry> timeLimits = new HashMap<>();
        private int globalTickCount;

        @Override
        public void tick() {
            globalTickCount++;
            for (Iterator<Map.Entry<ControllerType<?>, TimeEntry>> itr = timeLimits.entrySet().iterator(); itr.hasNext(); ) {
                Map.Entry<ControllerType<?>, TimeEntry> entry = itr.next();
                if (getControllers().stream().noneMatch(c -> c.getType() == entry.getKey())) {
                    Soullery.LOGGER.debug(MARKER, "TimeEntry for controller {} is invalid because the controller was removed", entry.getKey());
                    itr.remove();
                    continue;
                }
                if (timeRemaining(entry.getValue().end) <= 0) {
                    SoulManager.remove(mob, entry.getKey());
                    itr.remove();
                }
            }
        }

        @Override
        public void setTime(ControllerType<?> type, int time) {
            timeLimits.put(type, new TimeEntry(globalTickCount, globalTickCount + time));
        }

        @Override
        public int timeRemaining(ControllerType<?> type) {
            if (!timeLimits.containsKey(type)) {
                return Integer.MAX_VALUE;
            }
            return timeRemaining(timeLimits.get(type).end);
        }

        @Override
        public double getRemainingPercent(ControllerType<?> type) {
            if (!timeLimits.containsKey(type)) {
                return 1;
            }
            TimeEntry entry = timeLimits.get(type);
            return timeRemaining(entry.end) / (double) entry.getDuration();
        }

        private int timeRemaining(int timeEnd) {
            return timeEnd - globalTickCount;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putInt("TickCount", globalTickCount);
            ListNBT timeLimitsNBT = new ListNBT();
            for (Map.Entry<ControllerType<?>, TimeEntry> entry : timeLimits.entrySet()) {
                CompoundNBT singleEntryNBT = new CompoundNBT();
                singleEntryNBT.putString("Type", entry.getKey().getRegistryName().toString());
                singleEntryNBT.putInt("StartTime", entry.getValue().start);
                singleEntryNBT.putInt("EndTime", entry.getValue().end);
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
                    ControllerType<?> type = SoulManager.loadControllerType(singleEntryNBT.getString("Type"));
                    if (type == null) {
                        continue;
                    }
                    int startTime = singleEntryNBT.getInt("StartTime");
                    int endTime = singleEntryNBT.getInt("EndTime");
                    timeLimits.put(type, new TimeEntry(startTime, endTime));
                }
            }
        }
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
