package lych.soullery.item;

import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.dict.ControlDictionaries;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;

public class MindOperatorItem extends Item {
    public MindOperatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return super.interactLivingEntity(stack, player, entity, hand);
        }
        if (entity instanceof MobEntity) {
            if (player.level.isClientSide()) {
                return ActionResultType.SUCCESS;
            }
            if (SoulEnergies.cost(player, MindOperator.SE_COST)) {
                ControlDictionaries.MIND_OPERATOR.control((MobEntity) entity, player, 400);
                return ActionResultType.CONSUME;
            }
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }
}
