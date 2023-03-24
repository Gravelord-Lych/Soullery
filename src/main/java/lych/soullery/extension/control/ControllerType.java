package lych.soullery.extension.control;

import lych.soullery.Soullery;
import lych.soullery.api.event.RegisterControllersEvent;
import lych.soullery.extension.highlight.SoulControlHighlighter;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ControllerType<T extends MobEntity> {
    private static ControllerType<?>[] CONTROLLER_ARRAY = new ControllerType<?>[0];
    private static Map<ResourceLocation, ControllerType<?>> CONTROLLERS;


    public static final ControllerType<MobEntity> DEFAULT = new ControllerType<>(Soullery.prefix("default"), DefaultController::new, DefaultController::new);
    public static final ControllerType<MobEntity> DEFAULT_MO = new ControllerType<>(Soullery.prefix("default_mo"), DefaultMindOperator::new, DefaultMindOperator::new);
    public static final ControllerType<MobEntity> AGGRESSIVE_FLYER_MO = new ControllerType<>(Soullery.prefix("aggressive_flyer_mo"), AggressiveFlyerMindOperator::new, AggressiveFlyerMindOperator::new);
    public static final ControllerType<BlazeEntity> BLAZE_MO = new ControllerType<>(Soullery.prefix("blaze_mo"), BlazeOperator::new, BlazeOperator::new);
    public static final ControllerType<EndermanEntity> ENDERMAN_MO = new ControllerType<>(Soullery.prefix("enderman_mo"), EndermanOperator::new, EndermanOperator::new);
    public static final ControllerType<MobEntity> FLYER_MO = new ControllerType<>(Soullery.prefix("flyer"), FlyerMindOperator::new, FlyerMindOperator::new);
    public static final ControllerType<GhastEntity> GHAST_MO = new ControllerType<>(Soullery.prefix("ghast_mo"), GhastOperator::new, GhastOperator::new);
    public static final ControllerType<MobEntity> HARMLESS_MO = new ControllerType<>(Soullery.prefix("harmless_mo"), HarmlessMindOperator::new, HarmlessMindOperator::new);
    public static final ControllerType<MobEntity> HARMLESS_SPEED_LIMITED_MO = new ControllerType<>(Soullery.prefix("harmless_speed_limited_mo"), HarmlessSpeedLimitedMindOperator::new, HarmlessSpeedLimitedMindOperator::new);
    public static final ControllerType<MobEntity> SPEED_INDEPENDENT_FLYER_MO = new ControllerType<>(Soullery.prefix("speed_independent_flyer"), SpeedIndependentFlyerMindOperator::new, SpeedIndependentFlyerMindOperator::new);
    public static final ControllerType<MobEntity> SPEED_LIMITED_MO = new ControllerType<>(Soullery.prefix("speed_limited_mo"), SpeedLimitedMindOperator::new, SpeedLimitedMindOperator::new);

    private final ResourceLocation registryName;
    private final float[] colorHSB;
    private final ControllerFactory<T> factory;
    private final ControllerDeserializer<T> deserializer;
    private final int id;

    public ControllerType(ResourceLocation registryName,  ControllerFactory<T> factory, ControllerDeserializer<T> deserializer) {
        this(registryName, SoulControlHighlighter.DEFAULT_COLOR, factory, deserializer);
    }

    public ControllerType(ResourceLocation registryName, float[] colorHSB, ControllerFactory<T> factory, ControllerDeserializer<T> deserializer) {
        this.registryName = registryName;
        this.colorHSB = colorHSB;
        this.factory = factory;
        this.deserializer = deserializer;
        this.id = CONTROLLER_ARRAY.length;
        CONTROLLER_ARRAY = Arrays.copyOf(CONTROLLER_ARRAY, id + 1);
        CONTROLLER_ARRAY[id] = this;
    }

    public static void init() {
        Soullery.LOGGER.info(SoulManager.MARKER, "Registering Controllers...");
        autoRegister();
        FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterControllersEvent());
    }

    private static void autoRegister() {
        try {
            for (Field field : ControllerType.class.getFields()) {
                if (field.getType() == ControllerType.class) {
                    register((ControllerType<?>) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to auto-register controllers", e);
        }
    }

    public static void register(ControllerType<?> type) {
        if (CONTROLLERS == null) {
            CONTROLLERS = new HashMap<>();
        }
        CONTROLLERS.put(type.getRegistryName(), type);
    }

    public Controller<T> create(T mob, PlayerEntity player) {
        if (mob.level.isClientSide()) {
            throw new IllegalStateException("Cannot create a controller clientside");
        }
        return factory.create(this, mob, player);
    }

    public Controller<T> load(CompoundNBT compoundNBT, ServerWorld level) {
        return deserializer.load(this, compoundNBT, level);
    }

    @Nullable
    public static ControllerType<?> byRegistryName(ResourceLocation registryName) {
        if (CONTROLLERS == null) {
            CONTROLLERS = new HashMap<>();
        }
        return CONTROLLERS.get(registryName);
    }

    @Nullable
    public static ControllerType<?> byId(int id) {
        if (id < 0 || id >= CONTROLLER_ARRAY.length) {
            return null;
        }
        return CONTROLLER_ARRAY[id];
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public int getId() {
        return id;
    }

    public float[] getColor() {
        return colorHSB;
    }

    @FunctionalInterface
    public interface ControllerFactory<T extends MobEntity> {
        Controller<T> create(ControllerType<T> type, UUID mob, UUID player, ServerWorld level);

        default Controller<T> create(ControllerType<T> type, MobEntity mob, PlayerEntity player) {
            return create(type, mob.getUUID(), player.getUUID(), (ServerWorld) mob.level);
        }
    }

    @FunctionalInterface
    public interface ControllerDeserializer<T extends MobEntity> {
        Controller<T> load(ControllerType<T> type, CompoundNBT compoundNBT, ServerWorld level);
    }
}
