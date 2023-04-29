package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class AbstractPhase implements Phase {
    protected final SoulDragonEntity dragon;
    protected final World level;

    public AbstractPhase(SoulDragonEntity dragon) {
        this.dragon = dragon;
        this.level = dragon.level;
    }

    @Override
    public float getTurnSpeed() {
        float hd = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(dragon.getDeltaMovement())) + 1;
        float maxHd = Math.min(hd, 40);
        return 0.7f / maxHd / hd;
    }
}
