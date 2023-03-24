package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableSet;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;

public class WitherReinforcement extends TickableReinforcement {
    private static final int STEAL_HEALTH_INTERVAL = 30;
    private static final double RANGE = 16;
    private static final int MAX_LEVEL = 5;
    private static final float BASE_HEAL_AMOUNT = 0.25f;
    private static final float HEAL_AMOUNT_STEP = 0.25f;

    public WitherReinforcement() {
        super(EntityType.WITHER);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {
        if (!entity.level.isClientSide() && entity.tickCount % STEAL_HEALTH_INTERVAL == 0) {
            level = Math.min(level, MAX_LEVEL);
            boolean hurt = false;
            for (LivingEntity other : EntityUtils.getEntitiesInRange(LivingEntity.class, entity, RANGE, e -> e instanceof IMob)) {
                if (other.hurt(DamageSource.WITHER, (BASE_HEAL_AMOUNT + HEAL_AMOUNT_STEP * level) * 2)) {
                    hurt = true;
                }
            }
            if (hurt) {
                entity.heal(BASE_HEAL_AMOUNT + HEAL_AMOUNT_STEP * level);
            }
        }
    }

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {}

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return ImmutableSet.copyOf(EquipmentSlotType.values());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
