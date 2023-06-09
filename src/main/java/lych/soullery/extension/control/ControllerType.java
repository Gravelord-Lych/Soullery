package lych.soullery.extension.control;

import lych.soullery.Soullery;
import lych.soullery.api.event.RegisterControllersEvent;
import lych.soullery.extension.control.movement.RegularWaterMobOperator;
import lych.soullery.extension.highlight.SoulControlHighlighter;
import lych.soullery.item.SoulPurifierItem;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.SquidEntity;
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
    public static final ControllerType<MobEntity> CHAOS = new ControllerType<>(Soullery.prefix("chaos"), new float[]{0.30556f, 0.9f, 1}, ChaosController::new, ChaosController::new);
    public static final ControllerType<CreeperEntity> CREEPER_MO = new ControllerType<>(Soullery.prefix("creeper_mo"), CreeperOperator::new, CreeperOperator::new);
    public static final ControllerType<MobEntity> CUSTOM_MO = new ControllerType<>(Soullery.prefix("custom_mo"), ControllerType::createCustom, ControllerType::loadCustom);
    public static final ControllerType<EndermanEntity> ENDERMAN_MO = new ControllerType<>(Soullery.prefix("enderman_mo"), EndermanOperator::new, EndermanOperator::new);
    public static final ControllerType<EvokerEntity> EVOKER_MO = new ControllerType<>(Soullery.prefix("evoker_mo"), EvokerOperator::new, EvokerOperator::new);
    public static final ControllerType<MobEntity> FLYER_MO = new ControllerType<>(Soullery.prefix("flyer"), FlyerMindOperator::new, FlyerMindOperator::new);
    public static final ControllerType<GhastEntity> GHAST_MO = new ControllerType<>(Soullery.prefix("ghast_mo"), GhastOperator::new, GhastOperator::new);
    public static final ControllerType<GuardianEntity> GUARDIAN_MO = new ControllerType<>(Soullery.prefix("guardian_mo"), GuardianOperator::new, GuardianOperator::new);
    public static final ControllerType<MobEntity> HARMLESS_MO = new ControllerType<>(Soullery.prefix("harmless_mo"), HarmlessMindOperator::new, HarmlessMindOperator::new);
    public static final ControllerType<MobEntity> HARMLESS_SPEED_LIMITED_MO = new ControllerType<>(Soullery.prefix("harmless_speed_limited_mo"), HarmlessSpeedLimitedMindOperator::new, HarmlessSpeedLimitedMindOperator::new);
    public static final ControllerType<ShulkerEntity> SHULKER_MO = new ControllerType<>(Soullery.prefix("shulker_mo"), ShulkerOperator::new, ShulkerOperator::new);
    public static final ControllerType<MobEntity> SPEED_INDEPENDENT_FLYER_MO = new ControllerType<>(Soullery.prefix("speed_independent_flyer"), SpeedIndependentFlyerMindOperator::new, SpeedIndependentFlyerMindOperator::new);
    public static final ControllerType<MobEntity> SPEED_LIMITED_MO = new ControllerType<>(Soullery.prefix("speed_limited_mo"), SpeedLimitedMindOperator::new, SpeedLimitedMindOperator::new);
    public static final ControllerType<MobEntity> SOUL_PURIFIER = new ControllerType<>(Soullery.prefix("soul_purifier"),
            new float[]{SoulPurifierItem.HUE, SoulPurifierItem.SATURATION, SoulPurifierItem.BRIGHTNESS},
            SoulPurifierController::new,
            SoulPurifierController::new);
    public static final ControllerType<SquidEntity> SQUID_MO = new ControllerType<>(Soullery.prefix("squid_mo"), SquidOperator::new, SquidOperator::new);
    public static final ControllerType<MobEntity> WATER_MOB_MO = new ControllerType<>(Soullery.prefix("water_mob_mo"), RegularWaterMobOperator::new, RegularWaterMobOperator::new);

    private final ResourceLocation registryName;
    private final float[] colorHSB;
    private final ControllerFactory<T> factory;
    private final ControllerDeserializer<T> deserializer;
    private final int id;

    public ControllerType(ResourceLocation registryName,  ControllerFactory<T> factory, ControllerDeserializer<T> deserializer) {
        this(registryName, SoulControlHighlighter.DEFAULT_COLOR.get(), factory, deserializer);
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

    @SuppressWarnings("unchecked")
    private static Controller<MobEntity> createCustom(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        try {
            return (Controller<MobEntity>) CustomOperator.class.getConstructor(ControllerType.class, UUID.class, UUID.class, ServerWorld.class).newInstance(type, mob, player, level);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Controller<MobEntity> loadCustom(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        try {
            return (Controller<MobEntity>) CustomOperator.class.getConstructor(ControllerType.class, CompoundNBT.class, ServerWorld.class).newInstance(type, compoundNBT, level);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public String toString() {
        return String.format("ControllerType [%s(#%d)]", getRegistryName(), getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControllerType<?> that = (ControllerType<?>) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return getId();
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
