package lych.soullery.util;

import net.minecraft.particles.RedstoneParticleData;

import java.awt.*;

public final class RedstoneParticles {
    public static final RedstoneParticleData RED = RedstoneParticleData.REDSTONE;
    public static final RedstoneParticleData ORANGE = create(255, 128, 0);
    public static final RedstoneParticleData YELLOW = create(255, 255, 0);
    public static final RedstoneParticleData YELLOW_GREEN = create(128, 255, 0);
    public static final RedstoneParticleData GREEN = create(0, 255, 0);
    public static final RedstoneParticleData SPRING_GREEN = create(0, 255, 128);
    public static final RedstoneParticleData CYAN = create(0, 255, 255);
    public static final RedstoneParticleData LIGHT_BLUE = create(0, 128, 255);
    public static final RedstoneParticleData BLUE = create(0, 0, 255);
    public static final RedstoneParticleData PURPLE = create(128, 0, 255);
    public static final RedstoneParticleData MAGENTA = create(255, 0, 255);
    public static final RedstoneParticleData PINK = create(255, 0, 128);
    public static final RedstoneParticleData BLACK = create(0, 0, 0);
    public static final RedstoneParticleData GRAY = create(71, 71, 71);
    public static final RedstoneParticleData LIGHT_GRAY = create(157, 157, 157);
    public static final RedstoneParticleData WHITE = create(255, 255, 255);
    public static final RedstoneParticleData ERROR = create(248, 0, 248);

    private RedstoneParticles() {}

    public static RedstoneParticleData create(int color) {
        return create(new Color(color));
    }

    public static RedstoneParticleData create(Color color) {
        return create(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static RedstoneParticleData create(int r, int g, int b) {
        return createDirectly(r / 255f, g / 255f, b / 255f);
    }

    public static RedstoneParticleData createDirectly(float r, float g, float b) {
        return createDirectly(r, g, b, 1);
    }

    public static RedstoneParticleData createDirectly(float r, float g, float b, float scale) {
        return new RedstoneParticleData(r, g, b, scale);
    }
}
