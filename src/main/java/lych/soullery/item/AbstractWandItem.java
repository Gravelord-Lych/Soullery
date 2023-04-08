package lych.soullery.item;

import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWandItem extends Item {
    private final int energyCost;

    public AbstractWandItem(Properties properties, int energyCost) {
        super(properties);
        this.energyCost = energyCost;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (level.isClientSide() && shouldSuccessClientSide(player)) {
            return ActionResult.success(player.getItemInHand(hand));
        }
        if (!level.isClientSide()) {
            ActionResultType result = null;
            if (hasEnoughEnergy(player)) {
                result = performWandUse((ServerWorld) level, (ServerPlayerEntity) player, hand);
                if (result != null && result.consumesAction()) {
                    SoulEnergies.cost(player, energyCost);
                    if (getSound() != null) {
                        player.playSound(getSound(), 1, 1);
                    }
                }
            }
            if (result != null) {
                return new ActionResult<>(result, player.getItemInHand(hand));
            }
        }
        return super.use(level, player, hand);
    }

    protected boolean shouldSuccessClientSide(PlayerEntity player) {
        return hasEnoughEnergy(player);
    }

    protected final boolean hasEnoughEnergy(PlayerEntity player) {
        return SoulEnergies.getExtractableSEOf(player) >= energyCost;
    }

    @Nullable
    protected abstract ActionResultType performWandUse(ServerWorld level, ServerPlayerEntity player, Hand hand);

    @Nullable
    protected SoundEvent getSound() {
        return null;
    }
}
