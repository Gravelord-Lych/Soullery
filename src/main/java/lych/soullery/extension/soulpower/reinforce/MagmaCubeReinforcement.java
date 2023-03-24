package lych.soullery.extension.soulpower.reinforce;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;

public class MagmaCubeReinforcement extends TickableReinforcement {
    private static final Int2IntMap FIRE_SECONDS_MAP = EntityUtils.intChoiceBuilder().range(1).value(2).range(2).value(3).range(3).value(4).build();
    private static final Vector3d BASE_INFLATE_AMOUNT = new Vector3d(1, 0, 1);
    private static final int HURT_PLAYER_INTERVAL = 100;

    public MagmaCubeReinforcement() {
        super(EntityType.MAGMA_CUBE);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {}

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {
        if (side.isClient()) {
            return;
        }
        for (LivingEntity entity : player.level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(BASE_INFLATE_AMOUNT.x * level, BASE_INFLATE_AMOUNT.y * level, BASE_INFLATE_AMOUNT.z * level), e -> e instanceof IMob)) {
            if (entity == player) {
                continue;
            }
            entity.setSecondsOnFire(FIRE_SECONDS_MAP.get(level));
        }
        if (player.tickCount % HURT_PLAYER_INTERVAL == 0) {
            player.hurt(DamageSource.ON_FIRE, 1);
        }
    }

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return ImmutableSet.of(EquipmentSlotType.MAINHAND);
    }
}
