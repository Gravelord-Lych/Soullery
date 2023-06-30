package lych.soullery.extension.control;

import lych.soullery.Soullery;
import lych.soullery.api.capability.APICapabilities;
import lych.soullery.api.capability.IControlledMobData;
import lych.soullery.config.ConfigHelper;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Predicate;

public final class SoulManager {
    private static final String NAME = "SoulManager";
    public static final Marker MARKER = MarkerManager.getMarker(NAME);

    private SoulManager() {}

    @Nullable
    public static <T extends MobEntity> Controller<? super T> add(T mob, PlayerEntity player, @Nullable ControllerType<? super T> type) {
        MutableObject<Controller<? super T>> controller = new MutableObject<>();
        getData(mob).ifPresent(cd -> controller.setValue(cd.add(player, type)));
        return controller.getValue();
    }

    public static PriorityQueue<Controller<?>> getControllers(@Nullable MobEntity mob) {
        if (mob == null) {
            return new PriorityQueue<>();
        }
        return getData(mob).map(IControlledMobData::getControllers).orElseGet(PriorityQueue::new);
    }

    public static boolean hasControllers(@Nullable MobEntity mob) {
        if (mob == null) {
            return false;
        }
        return getData(mob).map(IControlledMobData::hasControllers).orElse(false);
    }

    public static Controller<?> remove(MobEntity mob, ControllerType<?> type) {
        return removeIf(mob, c -> c.getType() == type, true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Controller<?>> T remove(MobEntity mob, Class<T> cls) {
        return (T) removeIf(mob, cls::isInstance, false);
    }

    @SuppressWarnings("unchecked")
    public static  <T extends Controller<?>> T remove(MobEntity mob, T controller) {
        return (T) removeIf(mob, c -> c == controller, true);
    }

    public static Controller<?> removeIf(MobEntity mob, Predicate<? super Controller<?>> removePredicate) {
        return removeIf(mob, removePredicate, false);
    }

    @Nullable
    private static Controller<?> removeIf(MobEntity mob, Predicate<? super Controller<?>> removePredicate, boolean checkDuplication) {
        MutableObject<Controller<?>> controller = new MutableObject<>();
        getData(mob).ifPresent(cd -> controller.setValue(cd.removeIf(removePredicate, checkDuplication)));
        return controller.getValue();
    }

    @Nullable
    public static ControllerType<?> loadControllerType(String name) {
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

    public static void setTime(MobEntity mob, ControllerType<?> type, int time) {
        getTimer(mob).ifPresent(timer -> timer.setTime(type, time));
    }

    public static int timeRemaining(MobEntity mob, ControllerType<?> type) {
        return getTimer(mob).map(timer -> timer.timeRemaining(type)).orElse(Integer.MAX_VALUE);
    }

    public static double getRemainingPercent(MobEntity mob, ControllerType<?> type) {
        return getTimer(mob).map(timer -> timer.getRemainingPercent(type)).orElse(1.0);
    }

    public static <T extends MobEntity> LazyOptional<IControlledMobData<T>> getData(T mob) {
        return mob.getCapability(APICapabilities.CONTROLLED_MOB).cast();
    }

    @NotNull
    private static Optional<IControlledMobData.Timer> getTimer(MobEntity mob) {
        return getData(mob).map(IControlledMobData::getTimer);
    }
}
