package lych.soullery.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;

public class FallingNectarParticle extends DripParticle {
    public FallingNectarParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z, fluid);
        lifetime = (int) (64 / (Math.random() * 0.8 + 0.2));
    }

    @Override
    protected void postMoveUpdate() {
        if (onGround) {
            remove();
        }
    }
}
