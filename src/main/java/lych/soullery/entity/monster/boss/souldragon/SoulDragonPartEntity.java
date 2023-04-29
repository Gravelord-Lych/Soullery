package lych.soullery.entity.monster.boss.souldragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraftforge.entity.PartEntity;

public class SoulDragonPartEntity extends PartEntity<SoulDragonEntity> {
    private final EntitySize size;

    public SoulDragonPartEntity(SoulDragonEntity dragon, float width, float height) {
        this(dragon, EntitySize.scalable(width, height));
    }

    public SoulDragonPartEntity(SoulDragonEntity dragon, EntitySize size) {
        super(dragon);
        this.size = size;
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {}

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return !isInvulnerableTo(source) && getParent().hurt(this, source, amount);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || getParent() == entity;
    }

    @Override
    public EntitySize getDimensions(Pose pose) {
        return size;
    }
}
