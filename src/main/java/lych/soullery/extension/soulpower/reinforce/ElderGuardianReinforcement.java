package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class ElderGuardianReinforcement extends GuardianReinforcement {
    private static final double DETERRENCE_RANGE = 32;
    private static final double DISABLE_MONSTER_GRIEF_RANGE = 12;
    private static final double MIN_DETERRENCE_AMPLIFIER = -0.06;
    private static final double DETERRENCE_AMPLIFIER_STEP = -0.04;
    private static final UUID MONSTER_DETERRENCE_UUID = UUID.fromString("4854A9AE-2909-4CF3-996A-8958E4BC6BD0");

    public ElderGuardianReinforcement() {
        super(EntityType.ELDER_GUARDIAN);
    }

    @Override
    protected float getThornsDamageMultiplier() {
        return 1.25f;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @SubscribeEvent
    public void disableMobGrief(EntityMobGriefingEvent event) {
        if (event.getEntity() instanceof IMob) {
            PlayerEntity player = event.getEntity().level.getNearestPlayer(event.getEntity(), DISABLE_MONSTER_GRIEF_RANGE);
            if (player != null) {
                int level = getTotalLevel(player.getArmorSlots());
                if (level > 0) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity().canChangeDimensions() && event.getEntity() instanceof LivingEntity && event.getEntity() instanceof IMob) {
            int level = event.getEntity().level.players().stream()
                    .filter(player -> player.distanceToSqr(event.getEntity()) <= DETERRENCE_RANGE * DETERRENCE_RANGE)
                    .map(PlayerEntity::getArmorSlots)
                    .mapToInt(this::getTotalLevel)
                    .max()
                    .orElse(0);
            ModifiableAttributeInstance maxHealth = EntityUtils.getAttribute((LivingEntity) event.getEntity(), Attributes.MAX_HEALTH);
            AttributeModifier modifier = getMonsterDeterrenceModifier(level);
            if (level > 0 && !maxHealth.hasModifier(modifier)) {
                maxHealth.addPermanentModifier(modifier);
                ((LivingEntity) event.getEntity()).setHealth(((LivingEntity) event.getEntity()).getMaxHealth());
            }
        }
    }

    private AttributeModifier getMonsterDeterrenceModifier(int level) {
        return new AttributeModifier(MONSTER_DETERRENCE_UUID,
                "Monster deterrence",
                MIN_DETERRENCE_AMPLIFIER + level * DETERRENCE_AMPLIFIER_STEP,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
