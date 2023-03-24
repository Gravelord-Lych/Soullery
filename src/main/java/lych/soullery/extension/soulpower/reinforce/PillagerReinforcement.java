package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PillagerReinforcement extends Reinforcement {
    private static final double BASE_DROP_PROBABILITY = 0.08;

    public PillagerReinforcement() {
        super(EntityType.PILLAGER);
    }

    @SubscribeEvent
    public void onLivingLoot(LivingDropsEvent event) {
        if (event.isRecentlyHit() && event.getSource().getEntity() instanceof LivingEntity && event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            ItemStack stack = Utils.getOrDefault(attacker, ItemStack.EMPTY, LivingEntity::getMainHandItem);
            int level = getLevel(stack);
            if (level > 0 && attacker.getRandom().nextDouble() < BASE_DROP_PROBABILITY * level) {
                ItemStack emerald = new ItemStack(Items.EMERALD);
                event.getEntityLiving().spawnAtLocation(emerald);
            }
        }
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    @Override
    protected boolean hasEvents() {
        return true;
    }
}
