package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.gui.container.ExtraAbilityContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.SoftOverride;

public class ExtraAbilityWandItem extends Item {
    private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent(Soullery.prefixMsg("gui", "extra_ability"));
    private final int tier;

    public ExtraAbilityWandItem(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND && player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((containerCounter, inventory, playerIn) -> new ExtraAbilityContainer(containerCounter, inventory, tier), CONTAINER_TITLE), data -> data.writeVarInt(tier));
            return ActionResult.sidedSuccess(player.getMainHandItem(), world.isClientSide());
        }
        return super.use(world, player, hand);
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return getRarity(stack) != Rarity.COMMON;
    }
}
