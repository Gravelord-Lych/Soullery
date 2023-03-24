package lych.soullery.entity.ai.brain.sensor;

import lych.soullery.Soullery;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModSensors {
    public static final SensorType<NearestMonsterSensor> NEAREST_MONSTERS = new SensorType<>(NearestMonsterSensor::new);

    private ModSensors() {}

    @SubscribeEvent
    public static void registerSensors(RegistryEvent.Register<SensorType<?>> event) {
        IForgeRegistry<SensorType<?>> registry = event.getRegistry();
        registry.register(make(NEAREST_MONSTERS, "nearest_monsters"));
    }
}
