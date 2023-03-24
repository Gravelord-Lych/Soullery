package lych.soullery.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;

public class FallingLiquidParticle extends FallingNectarParticle {
    protected final IParticleData landParticle;

    public FallingLiquidParticle(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData data) {
        super(world, x, y, z, fluid);
        this.landParticle = data;
    }

    @Override
    protected void postMoveUpdate() {
        if (onGround) {
            remove();
            level.addParticle(landParticle, x, y, z, 0, 0, 0);
        }
    }
}
