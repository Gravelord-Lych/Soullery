package lych.soullery.client.particle;

import lych.soullery.Soullery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModParticles {
    public static final BasicParticleType DRIPPING_SOUL_LAVA = new BasicParticleType(false);
    public static final BasicParticleType FALLING_SOUL_LAVA = new BasicParticleType(false);
    public static final BasicParticleType LANDING_SOUL_LAVA = new BasicParticleType(false);
    public static final BasicParticleType PURSUER_RAIL = new BasicParticleType(false);
    public static final BasicParticleType PURSUER_RAIL_TYPE_2 = new BasicParticleType(false);
    public static final BasicParticleType SOUL_LAVA = new BasicParticleType(false);

    private ModParticles() {}

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> event) {
        IForgeRegistry<ParticleType<?>> registry = event.getRegistry();
        registry.register(make(DRIPPING_SOUL_LAVA, ModParticleNames.DRIPPING_SOUL_LAVA));
        registry.register(make(FALLING_SOUL_LAVA, ModParticleNames.FALLING_SOUL_LAVA));
        registry.register(make(LANDING_SOUL_LAVA, ModParticleNames.LANDING_SOUL_LAVA));
        registry.register(make(PURSUER_RAIL, ModParticleNames.PURSUER_RAIL));
        registry.register(make(PURSUER_RAIL_TYPE_2, ModParticleNames.PURSUER_RAIL_TYPE_2));
        registry.register(make(SOUL_LAVA, ModParticleNames.SOUL_LAVA));
    }

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
        ParticleManager manager = Minecraft.getInstance().particleEngine;
        manager.register(DRIPPING_SOUL_LAVA, DrippingSoulLavaFactory::new);
        manager.register(FALLING_SOUL_LAVA, FallingSoulLavaFactory::new);
        manager.register(LANDING_SOUL_LAVA, LandingSoulLavaFactory::new);
        manager.register(PURSUER_RAIL, sprite -> new PursuerRailParticle.Factory(sprite, false));
        manager.register(PURSUER_RAIL_TYPE_2, sprite -> new PursuerRailParticle.Factory(sprite, true));
        manager.register(SOUL_LAVA, LavaParticle.Factory::new);
    }
}
