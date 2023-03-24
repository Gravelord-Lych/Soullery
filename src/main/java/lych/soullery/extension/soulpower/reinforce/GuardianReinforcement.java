package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.ExtraAbilityConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Map;

public class GuardianReinforcement extends DefensiveReinforcement {
    private static final float BASE_THORNS_DAMAGE = 1;
    private static final float THORNS_DAMAGE_STEP = 0.5f;

    public GuardianReinforcement() {
        super(EntityType.GUARDIAN);
    }

    protected GuardianReinforcement(EntityType<?> type) {
        super(type);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && (((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.CHEST || ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.LEGS);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {
        if (EntityUtils.isMelee(source) && !EntityUtils.isThorns(source) && source.getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) source.getEntity();
            float thornsDamage = BASE_THORNS_DAMAGE + level * THORNS_DAMAGE_STEP;
            if (entity instanceof PlayerEntity && ExtraAbility.THORNS_MASTER.isOn((PlayerEntity) entity)) {
                thornsDamage += ExtraAbilityConstants.THORNS_MASTER_DAMAGE;
            }
            attacker.hurt(DamageSource.thorns(entity), thornsDamage * getThornsDamageMultiplier());
        }
    }

    protected float getThornsDamageMultiplier() {
        return 1;
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {}

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }

    @Override
    public int getLevel(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return ReinforcementHelper.getReinforcements(stack).entrySet().stream().filter(e -> e.getKey() instanceof GuardianReinforcement).mapToInt(Map.Entry::getValue).sum();
    }
}
