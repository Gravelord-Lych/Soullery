package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.effect.ModEffects;
import lych.soullery.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;

public class SoulDragonReinforcement extends DragonReinforcement {
    public SoulDragonReinforcement() {
        super(ModEntities.SOUL_DRAGON);
    }

    @Override
    protected Effect getEffect(LivingEntity entity, int level) {
        return level > 1 ? ModEffects.PURE_SOUL_FIRED : ModEffects.SOUL_FIRED;
    }

    @Override
    protected int getDuration(int level) {
        return 1;
    }

    @Override
    protected int getAmplifier(int level) {
        return level - 1;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
