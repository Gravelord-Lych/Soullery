package lych.soullery.listener;

import lych.soullery.Soullery;
import lych.soullery.api.capability.ISoulEnergyStorage;
import lych.soullery.dispenser.ModDispenserBehaviors;
import lych.soullery.entity.ModEntities;
import lych.soullery.extension.control.ControllerType;
import lych.soullery.extension.fire.Fires;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.potion.ModPotions;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventListener {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        ModDispenserBehaviors.registerBehaviors();
        ModEntities.registerEntitySpawnPlacements();
        ModPotions.registerBrewingRecipes();
        registerCapabilities(event);
        Reinforcements.init();
        Fires.init();
        ControllerType.init();
    }

    private static void registerCapabilities(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            CapabilityManager.INSTANCE.register(ISoulEnergyStorage.class, new DummyCapabilityStorage<>(), () -> null);
        });
    }

    /**
     * Capability providers are used to store data, not this dummy.
     */
    private static class DummyCapabilityStorage<T> implements Capability.IStorage<T> {
        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            return null;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {}
    }
}
