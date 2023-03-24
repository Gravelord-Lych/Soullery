package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;

public class PhantomReinforcement extends TickableReinforcement {
    private static final Int2DoubleMap REPAIR_CHANCE = EntityUtils.doubleChoiceBuilder().range(1).value(0.0015).range(2).value(0.003).range(3).value(0.0045).build();
    private static final Int2DoubleMap ARMOR_REPAIR_CHANCE = EntityUtils.doubleChoiceBuilder().range(1).value(0.0006).range(2).value(0.0012).range(3).value(0.0018).build();

    public PhantomReinforcement() {
        super(EntityType.PHANTOM);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {
        if (stack.getDamageValue() > 0 && entity.getRandom().nextDouble() < (stack.getItem() instanceof ArmorItem ? ARMOR_REPAIR_CHANCE.get(level) : REPAIR_CHANCE.get(level))) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {}

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return ImmutableSet.copyOf(EquipmentSlotType.values());
    }
}
