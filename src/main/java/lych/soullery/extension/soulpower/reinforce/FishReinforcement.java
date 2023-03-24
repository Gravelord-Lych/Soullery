package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;

public class FishReinforcement extends TickableReinforcement {
    protected static final int MAX_LEVEL = 3;

    public FishReinforcement(EntityType<?> type) {
        super(type);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {}

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {}

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return ImmutableSet.of(EquipmentSlotType.HEAD);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.HEAD;
    }

    @Override
    protected boolean hasEvents() {
        return false;
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return super.isCompatibleWith(reinforcement) && !(reinforcement instanceof FishReinforcement);
    }

    public static int getFishReinforcementLevel(Iterable<ItemStack> stack) {
        return Math.min(MAX_LEVEL, Reinforcements.getReinforcements().values().stream().filter(r -> r instanceof FishReinforcement).mapToInt(r -> r.getTotalLevel(stack)).sum());
    }
}
