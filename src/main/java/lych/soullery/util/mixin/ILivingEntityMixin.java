package lych.soullery.util.mixin;

public interface ILivingEntityMixin {
    double getKnockupStrength();

    void setKnockupStrength(double knockupStrength);

    long getSheepReinforcementTickCount();

    long getSheepReinforcementLastHurtByTimestamp();

    void setSheepReinforcementLastHurtByTimestamp(long sheepReinforcementLastHurtByTimestamp);
}
