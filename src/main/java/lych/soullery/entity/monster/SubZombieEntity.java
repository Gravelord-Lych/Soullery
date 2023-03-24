package lych.soullery.entity.monster;

import lych.soullery.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soullery.entity.iface.IHasOwner;
import lych.soullery.entity.monster.boss.GiantXEntity;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SubZombieEntity extends ZombieEntity implements IHasOwner<GiantXEntity> {
    private UUID ownerUUID;

    public SubZombieEntity(EntityType<? extends SubZombieEntity> zombie, World world) {
        super(zombie, world);
        xpReward = 1;
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(7, new LookAtGoal(this, GiantXEntity.class, 12));
        goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 8));
        goalSelector.addGoal(8, new LookRandomlyGoal(this));
        addBehaviourGoals();
    }

    @Override
    protected void addBehaviourGoals() {
        goalSelector.addGoal(2, new ZombieAttackGoal(this, 1, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomWalkingGoal(this, 1));
        targetSelector.addGoal(1, new HurtByTargetGoal(this, SubZombieEntity.class, GiantXEntity.class).setAlertOthers(ZombifiedPiglinEntity.class, GiantXEntity.class));
        targetSelector.addGoal(2, new CopyOwnerTargetGoal<>(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolemEntity.class, true));
    }

    @Override
    protected void randomizeReinforcementsChance() {
        EntityUtils.getAttribute(this, Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
