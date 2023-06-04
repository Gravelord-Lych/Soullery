package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;

public class EnderDragonReinforcement extends DragonReinforcement {
    public EnderDragonReinforcement() {
        super(EntityType.ENDER_DRAGON);
    }

    @Override
    protected Effect getEffect(LivingEntity entity, int level) {
        return entity.isInvertedHealAndHarm() ? Effects.HEAL : Effects.HARM;
    }
}
