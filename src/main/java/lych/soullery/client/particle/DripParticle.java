package lych.soullery.client.particle;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @see net.minecraft.client.particle.DripParticle
 */
@OnlyIn(Dist.CLIENT)
public class DripParticle extends SpriteTexturedParticle {
    private final Fluid type;
    protected boolean isGlowing;

    public DripParticle(ClientWorld world, double x, double y, double z, Fluid fluid) {
        super(world, x, y, z);
        setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.type = fluid;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTicks) {
        return isGlowing ? 240 : super.getLightColor(partialTicks);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        preMoveUpdate();
        if (!removed) {
            yd -= gravity;
            move(xd, yd, zd);
            postMoveUpdate();
            if (!removed) {
                xd *= 0.98F;
                yd *= 0.98F;
                zd *= 0.98F;
                BlockPos pos = new BlockPos(x, y, z);
                FluidState fluid = level.getFluidState(pos);
                if (fluid.getType() == type && y < pos.getY() + fluid.getHeight(level, pos)) {
                    remove();
                }
            }
        }
    }

    protected void preMoveUpdate() {
        if (lifetime-- <= 0) {
            remove();
        }
    }

    protected void postMoveUpdate() {}
}
