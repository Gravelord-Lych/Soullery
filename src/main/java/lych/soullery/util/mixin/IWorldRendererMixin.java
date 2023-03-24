package lych.soullery.util.mixin;

import lych.soullery.client.LaserRenderingManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.particles.IParticleData;
import org.jetbrains.annotations.Nullable;

public interface IWorldRendererMixin {
    @Nullable
    VertexBuffer getSkyBuffer();

    LaserRenderingManager getLaserRenderingManager();

    @Nullable
    default Particle callAddParticleInternal(IParticleData particle, boolean alwaysVisible,  double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        return callAddParticleInternal(particle, alwaysVisible, false, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Nullable
    Particle callAddParticleInternal(IParticleData particle, boolean alwaysVisible, boolean canDecreaseStatus, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed);
}
