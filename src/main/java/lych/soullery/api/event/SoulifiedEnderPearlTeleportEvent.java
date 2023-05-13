package lych.soullery.api.event;

import lych.soullery.entity.projectile.SoulifiedEnderPearlEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class SoulifiedEnderPearlTeleportEvent extends EntityTeleportEvent {
    private final ServerPlayerEntity player;
    private final SoulifiedEnderPearlEntity pearlEntity;
    private float attackDamage;

    public SoulifiedEnderPearlTeleportEvent(ServerPlayerEntity entity, double targetX, double targetY, double targetZ, SoulifiedEnderPearlEntity pearlEntity, float attackDamage) {
        super(entity, targetX, targetY, targetZ);
        this.pearlEntity = pearlEntity;
        this.player = entity;
        this.attackDamage = attackDamage;
    }

    public SoulifiedEnderPearlEntity getPearlEntity() {
        return pearlEntity;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
    }
}
