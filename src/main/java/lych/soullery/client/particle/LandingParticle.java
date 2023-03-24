package lych.soullery.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;

public class LandingParticle extends DripParticle {
    public LandingParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z, fluid);
        lifetime = (int) (16 / (Math.random() * 0.8 + 0.2));
    }
}
