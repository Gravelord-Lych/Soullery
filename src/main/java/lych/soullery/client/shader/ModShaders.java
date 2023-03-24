package lych.soullery.client.shader;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.SoulSkeletonEntity;
import lych.soullery.entity.monster.WandererEntity;
import net.minecraft.util.ResourceLocation;

import static net.minecraftforge.fml.client.registry.ClientRegistry.registerEntityShader;

public class ModShaders {
    public static final ResourceLocation REVERSION = Soullery.prefixShader(ModShaderNames.REVERSION);
    public static final ResourceLocation SOUL_MOB = Soullery.prefixShader(ModShaderNames.SOUL_MOB);

    public static void registerShaders() {
        registerEntityShader(SoulSkeletonEntity.class, SOUL_MOB);
        registerEntityShader(WandererEntity.class, SOUL_MOB);
    }
}
