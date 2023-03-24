package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.api.event.ArrowSpawnEvent;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class PiglinReinforcement extends BowAttackReinforcement {
    private static final Int2DoubleMap BASE_DAMAGE_MULTIPLIER_MAP = EntityUtils.doubleChoiceBuilder().range(1).value(1.2).range(2).value(1.4).range(3).value(1.6).build();
    private static final double EXTRA_GOLD_DAMAGE_MODIFIER = 0.1;

    public PiglinReinforcement() {
        super(EntityType.PIGLIN);
    }

    @Override
    protected void onSpawn(AbstractArrowEntity arrow, PlayerEntity player, int level, ArrowSpawnEvent event) {
        double baseDamageMultiplier = BASE_DAMAGE_MULTIPLIER_MAP.get(level);
        double goldDamageMultiplier = 1 + getGoldCount(player) * EXTRA_GOLD_DAMAGE_MODIFIER;
        arrow.setBaseDamage(arrow.getBaseDamage() * baseDamageMultiplier * goldDamageMultiplier);
    }

    @Override
    protected void onImpact(AbstractArrowEntity arrow, LivingEntity owner, int level, ProjectileImpactEvent.Arrow event) {}

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
//      Prevents arrow form being too strong.
        return super.isCompatibleWith(reinforcement) && !(reinforcement instanceof SkeletonReinforcement);
    }

    public static boolean isGold(ItemStack stack) {
        if (stack.getItem() instanceof TieredItem && ((TieredItem) stack.getItem()).getTier() == ItemTier.GOLD) {
            return true;
        }
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getMaterial() == ArmorMaterial.GOLD;
    }

    public static int getGoldCount(LivingEntity entity) {
        return (int) Streams.stream(entity.getAllSlots()).filter(PiglinReinforcement::isGold).count();
    }
}
