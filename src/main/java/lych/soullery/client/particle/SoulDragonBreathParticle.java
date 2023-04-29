package lych.soullery.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * [VanillaCopy]
 * {@link DragonBreathParticle}
 */
public class SoulDragonBreathParticle extends SpriteTexturedParticle {
    private boolean hasHitGround;
    private final IAnimatedSprite sprites;
    private final boolean pure;

    public SoulDragonBreathParticle(ClientWorld world, double x, double y, double z, double xd, double yd, double zd, boolean pure, IAnimatedSprite sprite) {
        super(world, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.rCol = 0;
        this.pure = pure;
        if (pure) {
            this.gCol = MathHelper.nextFloat(random, 0.34f, 0.41f);
        } else {
            this.gCol = MathHelper.nextFloat(random, 0.78f, 0.85f);
        }
        this.bCol = MathHelper.nextFloat(random, 0.85f, 0.92f);
        this.quadSize *= 0.75F;
        this.lifetime = (int)(20.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D));
        this.hasHitGround = false;
        this.hasPhysics = false;
        this.sprites = sprite;
        this.setSpriteFromAge(sprite);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
        } else {
            setSpriteFromAge(sprites);
            if (onGround) {
                yd = 0;
                hasHitGround = true;
            }
            if (hasHitGround) {
                yd += 0.002;
            }
            double mul = pure ? 0.7 : 1;
            move(xd * mul, yd * mul, zd * mul);
            if (y == yo) {
                xd *= 1.1;
                zd *= 1.1;
            }
            xd *= 0.96;
            zd *= 0.96;
            if (hasHitGround) {
                yd *= 0.96;
            }
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float p_217561_1_) {
        return this.quadSize * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public static class CommonFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public CommonFactory(IAnimatedSprite sprite) {
            this.sprites = sprite;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xd, double yd, double zd) {
            return new SoulDragonBreathParticle(world, x, y, z, xd, yd, zd, false, sprites);
        }
    }

    public static class PureFactory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public PureFactory(IAnimatedSprite sprite) {
            this.sprites = sprite;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xd, double yd, double zd) {
            return new SoulDragonBreathParticle(world, x, y, z, xd, yd, zd, true, sprites);
        }
    }
}