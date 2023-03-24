package lych.soullery.entity.projectile;

public interface IMortarShell {
    float getExplosionPower();

    void setExplosionPower(float explosionPower);

    boolean isBurning();

    void setBurning(boolean burning);
}
