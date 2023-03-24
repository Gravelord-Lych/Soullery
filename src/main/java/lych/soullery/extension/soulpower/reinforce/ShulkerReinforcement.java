package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ShulkerReinforcement extends AggressiveReinforcement {
    private static final double TELEPORT_PROBABILITY = 0.35;
    private static final int TELEPORT_RADIUS = 3;
    private static final int MAX_TRY_TIME = 10;
    private static final Int2ObjectMap<RangedInteger> LEVITATION_DURATION_MAP;

    static {
        EntityUtils.TierChoiceBuilder<RangedInteger> choiceBuilder = EntityUtils.choiceBuilder();
        choiceBuilder.range(1);
        choiceBuilder.value(RangedInteger.of(10, 25));
        choiceBuilder.range(2);
        choiceBuilder.value(RangedInteger.of(20, 45));
        choiceBuilder.range(3);
        choiceBuilder.value(RangedInteger.of(40, 60));
        LEVITATION_DURATION_MAP = choiceBuilder.build();
    }

    public ShulkerReinforcement() {
        super(EntityType.SHULKER);
    }

    @Override
    protected boolean allowsDamageSource(DamageSource source) {
//      Allows all damages EXCEPT explosion.
        return (EntityUtils.isMelee(source) || source.isProjectile()) && !source.isExplosion();
    }

    @Override
    public boolean isItemPosSuitable(ItemStack stack) {
        return super.isItemPosSuitable(stack) || stack.getItem() instanceof BowItem;
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingHurtEvent event) {}

    @Override
    protected void onDamage(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, LivingDamageEvent event) {}

    @Override
    protected void postHurt(ItemStack stack, LivingEntity attacker, LivingEntity target, int level, PostLivingHurtEvent event) {
        super.postHurt(stack, attacker, target, level, event);
        if (event.isSuccessfullyHurt()) {
            target.addEffect(new EffectInstance(Effects.LEVITATION, LEVITATION_DURATION_MAP.get(level).randomValue(attacker.getRandom())));
            doRandomTeleport(attacker, target);
        }
    }

    @SuppressWarnings("deprecation")
    private void doRandomTeleport(LivingEntity attacker, LivingEntity target) {
        if (attacker.getRandom().nextDouble() < TELEPORT_PROBABILITY) {
            for (int i = 0; i < MAX_TRY_TIME; i++) {
                int x = target.blockPosition().getX() - TELEPORT_RADIUS + target.getRandom().nextInt(TELEPORT_RADIUS * 2 + 1);
                int z = target.blockPosition().getZ() - TELEPORT_RADIUS + target.getRandom().nextInt(TELEPORT_RADIUS * 2 + 1);
                int y = target.blockPosition().getY() + target.getRandom().nextInt(2);
                if (target.level.getBlockState(new BlockPos(x, y, z)).isAir()) {
                    target.randomTeleport(x, y, z, true);
                    break;
                }
            }
        }
    }
}
