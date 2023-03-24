package lych.soullery.network;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModNetworkingRegistry {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ClickHandlerNetwork::register);
        event.enqueueWork(InvokableNetwork::register);
        event.enqueueWork(LaserNetwork::register);
        event.enqueueWork(MindOperatorNetwork::register);
        event.enqueueWork(StaticStatusHandler::register);
    }
}
