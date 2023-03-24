package lych.soullery.extension.key;

import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InvokableManager {
    private static final Map<InvokableData, IInvokable> INVOKABLES = new HashMap<>();

    public static void register(FMLClientSetupEvent event, InvokableData data, IInvokable invokable) {
        Objects.requireNonNull(invokable);
        INVOKABLES.put(data, invokable);
        event.enqueueWork(() -> ClientRegistry.registerKeyBinding(data.getKey()));
    }

    public static ImmutableMap<InvokableData, IInvokable> getKeyInvokables() {
        return ImmutableMap.copyOf(INVOKABLES);
    }

    @SuppressWarnings("ConstantConditions")
    public static IInvokable get(UUID uuid) {
        return get(new InvokableData(uuid, null));
    }

    public static IInvokable get(InvokableData data) {
        return getKeyInvokables().get(data);
    }
}
