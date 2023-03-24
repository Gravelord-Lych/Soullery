package lych.soullery.extension.soulpower.reinforce;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

public class BatReinforcement extends ToolReinforcement {
    private static final Int2FloatMap MAX_SPEED_MULTIPLIER_MAP = EntityUtils.floatChoiceBuilder().range(1).value(1.5f).range(2).value(2).range(3).value(2.5f).build();

    public BatReinforcement() {
        super(EntityType.BAT);
    }

    @Override
    protected void onHarvest(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.HarvestCheck event) {}

    @Override
    protected void onDig(ItemStack stack, PlayerEntity player, BlockState state, int level, PlayerEvent.BreakSpeed event) {
        if (event.getPos().getY() != -1) {
            float lightValue = player.level.getRawBrightness(event.getPos(), 0) / 15f;
            event.setNewSpeed(MathHelper.lerp(lightValue, event.getNewSpeed() * MAX_SPEED_MULTIPLIER_MAP.get(level), event.getNewSpeed()));
        }
    }

    @Override
    protected void onBreak(ItemStack stack, PlayerEntity player, BlockState state, int level, BlockEvent.BreakEvent event) {}
}
