package lych.soullery.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class PursuerRailParticle extends SimpleAnimatedParticle {
    private static final Color COLOR = new Color(0x98D6FF);
    private static final Color FADE_COLOR = new Color(0xb7e3ff);
    private static final Color COLOR_TYPE_2 = new Color(0x98A1FF);
    private static final Color FADE_COLOR_TYPE_2 = new Color(0xB7BBFF);

    public PursuerRailParticle(ClientWorld world, double x, double y, double z, double xd, double yd, double zd, IAnimatedSprite sprite, float baseGravity, boolean type2) {
        super(world, x, y + 0.5, z, sprite, baseGravity);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        quadSize *= 1.2;
        lifetime = 80 + random.nextInt(21);
        setColor(type2 ? COLOR_TYPE_2.getRGB() : COLOR.getRGB());
        setFadeColor(type2 ? FADE_COLOR_TYPE_2.getRGB() : FADE_COLOR.getRGB());
        setSpriteFromAge(sprite);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;
        private final boolean type2;

        public Factory(IAnimatedSprite sprite, boolean type2) {
            this.sprites = sprite;
            this.type2 = type2;
        }

        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xd, double yd, double zd) {
            return new PursuerRailParticle(world, x, y, z, xd, yd, zd, sprites, -4.0E-4f, type2);
        }
    }
}
