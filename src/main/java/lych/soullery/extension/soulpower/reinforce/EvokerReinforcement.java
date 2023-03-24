package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.InventoryUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EvokerReinforcement extends Reinforcement {
    private static final int DURABILITY_COST = 16;
    private static final Int2IntMap GOLD_INGOT_COUNT_MAP = EntityUtils.intChoiceBuilder().range(1).value(15).range(2).value(10).range(3).value(5).build();
    private static final Int2IntMap EMERALD_COUNT_MAP = EntityUtils.intChoiceBuilder().range(1).value(9).range(2).value(6).range(3).value(3).build();

    public EvokerReinforcement() {
        super(EntityType.EVOKER);
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().isBypassInvul()) {
            return;
        }
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            ItemStack stack = player.getMainHandItem();
            int level = getLevel(stack);
            if (level > 0 && stack.getDamageValue() < stack.getMaxDamage() - DURABILITY_COST - 1) {
                List<ItemStack> items = InventoryUtils.getList(player.inventory);
                int goldSum = items.stream().filter(s -> s.getItem() == Items.GOLD_INGOT).mapToInt(ItemStack::getCount).sum();
                int emeraldSum = items.stream().filter(s -> s.getItem() == Items.EMERALD).mapToInt(ItemStack::getCount).sum();
                if (goldSum >= GOLD_INGOT_COUNT_MAP.get(level) && emeraldSum >= EMERALD_COUNT_MAP.get(level)) {
                    cost(items, GOLD_INGOT_COUNT_MAP.get(level), EMERALD_COUNT_MAP.get(level));
                    event.setCanceled(true);
                    player.setHealth(1);
                    player.removeAllEffects();
                    player.addEffect(new EffectInstance(Effects.REGENERATION, 20 * 10, 1));
                    player.addEffect(new EffectInstance(Effects.ABSORPTION, 20 * 5, 1));
                    stack.hurtAndBreak(DURABILITY_COST, player, p -> p.broadcastBreakEvent(Hand.MAIN_HAND));
                    if (!player.level.isClientSide()) {
                        EntityUtils.spawnAnimServerside(player, (ServerWorld) player.level);
                    }
                }
            }
        }
    }

    private void cost(List<ItemStack> items, int gold, int emerald) {
        for (ItemStack stack : items) {
            if (stack.getItem() == Items.GOLD_INGOT && gold > 0) {
                gold = costSingle(stack, gold);
            } else if (stack.getItem() == Items.EMERALD && emerald > 0) {
                emerald = costSingle(stack, emerald);
            }
        }
    }

    private int costSingle(ItemStack stack, int count) {
        if (stack.getCount() >= count) {
            stack.shrink(count);
            count = 0;
        } else {
            count -= stack.getCount();
            stack.shrink(stack.getCount());
        }
        return count;
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}
