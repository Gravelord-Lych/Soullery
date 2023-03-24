package lych.soullery.listener;

import lych.soullery.Soullery;
import lych.soullery.extension.key.InvokableManager;
import lych.soullery.network.InvokableNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, value = Dist.CLIENT)
public final class KeyInputHandler {
    private KeyInputHandler() {}

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        InvokableManager.getKeyInvokables().keySet().stream()
                .filter(invokable -> invokable.getKey().isDown())
                .forEach(invokable -> InvokableNetwork.INSTANCE.sendToServer(new InvokableNetwork.KeyPacket(invokable.getUUID())));
    }
}
