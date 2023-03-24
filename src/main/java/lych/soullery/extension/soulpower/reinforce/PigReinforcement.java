package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;

public class PigReinforcement extends TickableReinforcement {
    private static final int MAX_LEVEL = 6;
    private static final Int2IntMap RESTORE_FREQUENCY_MAP = EntityUtils.intChoiceBuilder().range(1).value(240).range(2).value(210).range(3).value(180).range(4).value(150).range(5).value(120).range(6).value(90).build();

    public PigReinforcement() {
        super(EntityType.PIG);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {}

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {
        if (side.isServer() && player.tickCount % RESTORE_FREQUENCY_MAP.get(Math.min(level, MAX_LEVEL)) == 0) {
            player.getFoodData().eat(1, 0);
        }
    }

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return ImmutableSet.copyOf(EquipmentSlotType.values());
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }
}
