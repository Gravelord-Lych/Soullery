package lych.soullery.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;

public class MooshroomReinforcement extends CowReinforcement {
    public MooshroomReinforcement() {
        super(EntityType.MOOSHROOM);
    }

    @Override
    protected double getInvulnerableProbability(int level) {
        return super.getInvulnerableProbability(level) * 1.2;
    }
}
