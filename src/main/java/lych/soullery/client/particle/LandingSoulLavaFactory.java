package lych.soullery.client.particle;

import lych.soullery.fluid.ModFluids;
import lych.soullery.util.SoulLavaConstants;
import net.minecraft.client.particle.DripParticle.LandingLavaFactory;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class LandingSoulLavaFactory extends LandingLavaFactory {
    public LandingSoulLavaFactory(IAnimatedSprite sprite) {
        super(sprite);
    }

    @Override
    public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        DripParticle particle = new LandingParticle(world, x, y, z, ModFluids.SOUL_LAVA);
        particle.setColor(SoulLavaConstants.SOUL_LAVA_R, SoulLavaConstants.SOUL_LAVA_G, SoulLavaConstants.SOUL_LAVA_B);
        particle.pickSprite(sprite);
        return particle;
    }
}
