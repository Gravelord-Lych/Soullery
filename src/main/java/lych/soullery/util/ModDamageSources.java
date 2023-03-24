package lych.soullery.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;

import javax.annotation.Nullable;

public class ModDamageSources {
    public static final DamageSource LASER = new DamageSource("laser");

    public static DamageSource laser(Entity attacker) {
        return new EntityDamageSource("entityLaser", attacker);
    }

    public static DamageSource indirectLightning(Entity attacker, Entity lightning) {
        return new IndirectEntityDamageSource("entityLightning", lightning, attacker);
    }

    public static DamageSource softFireball(Entity fireball, @Nullable Entity owner, boolean scalesWithDifficulty) {
        DamageSource src = (owner == null ? new IndirectEntityDamageSource("onFire", fireball, fireball) : new IndirectEntityDamageSource("fireball", fireball, owner)).setIsFire().setProjectile();
        if (scalesWithDifficulty) {
            src.setScalesWithDifficulty();
        }
        return src;
    }
}
