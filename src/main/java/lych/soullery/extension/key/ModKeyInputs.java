package lych.soullery.extension.key;

import lych.soullery.Soullery;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModKeyInputs {
//  TODO - remove
    public static final KeyBinding CHANGE_MODE_KEY = new KeyBinding(Soullery.prefixKeyMessage("change_mode"), KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_Z, Soullery.prefixKeyCategory("tools"));
    public static final KeyBinding DRAGON_WIZARD_KEY = createExtraAbilityKey(GLFW.GLFW_KEY_R, "dragon_wizard");
    public static final KeyBinding FANGS_SUMMONER_KEY = createExtraAbilityKey(GLFW.GLFW_KEY_G, "fangs_summoner");
    public static final InvokableData CHANGE_MODE = new InvokableData(UUID.fromString("1F9C899C-2753-003C-AED7-C9431D8EC1FD"), CHANGE_MODE_KEY);
    public static final InvokableData DRAGON_WIZARD = new InvokableData(UUID.fromString("FEFFB414-DCF7-E7BB-878A-449A2D8F9740"), DRAGON_WIZARD_KEY);
    public static final InvokableData FANGS_SUMMONER = new InvokableData(UUID.fromString("53B6EF2D-EFDD-49FA-842C-2674C2C7B9F2"), FANGS_SUMMONER_KEY);

    private ModKeyInputs() {}

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        InvokableManager.register(event, CHANGE_MODE, ChangeModeInvokable.INSTANCE);
        InvokableManager.register(event, DRAGON_WIZARD, DragonWizardInvokable.INSTANCE);
        InvokableManager.register(event, FANGS_SUMMONER, FangsSummonerInvokable.INSTANCE);
    }

    private static KeyBinding createExtraAbilityKey(int key, String name) {
        return new KeyBinding(Soullery.prefixKeyMessage(name), KeyConflictContext.IN_GAME, KeyModifier.NONE, InputMappings.Type.KEYSYM, key, Soullery.prefixKeyCategory("exa"));
    }
}
