package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.function.Supplier;

public class CatReinforcement extends ToolReinforcement {
    private static final Int2FloatMap SPEED_MULTIPLIER_MAP = EntityUtils.floatChoiceBuilder().range(1).value(1.4f).range(2).value(1.8f).range(3).value(2.2f).build();
    private static final double EXTRA_DURABILITY_COST_PROBABILITY = 0.25;

    public CatReinforcement(EntityType<?> type) {
        super(type);
    }

    public CatReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    public CatReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event) {}

    @Override
    protected void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(event.getNewSpeed() * SPEED_MULTIPLIER_MAP.get(level));
    }

    @Override
    protected void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event) {
        if (player.getRandom().nextDouble() < EXTRA_DURABILITY_COST_PROBABILITY) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(Hand.MAIN_HAND));
        }
    }

    @Override
    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return super.isCompatibleWith(reinforcement) && !(reinforcement instanceof CatReinforcement);
    }
}
