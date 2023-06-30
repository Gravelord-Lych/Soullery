package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.util.EntityUtils;
import lych.soullery.util.RedstoneParticles;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;

public interface EnchanterSkill {
    boolean canUse(EnchanterEntity enchanter, LivingEntity target);

    void performSkill(EnchanterEntity enchanter, LivingEntity target);

    ITextComponent getSkillText(LivingEntity target);

    SoundEvent getSound();

    default void particlesAround(EnchanterEntity enchanter, int color) {
        EntityUtils.addParticlesAroundSelfServerside(enchanter, (ServerWorld) enchanter.level, RedstoneParticles.create(color), 5);
    }
}
