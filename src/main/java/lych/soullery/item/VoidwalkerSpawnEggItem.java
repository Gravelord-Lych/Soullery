package lych.soullery.item;

import lych.soullery.entity.ModEntityNames;
import lych.soullery.entity.monster.voidwalker.VoidwalkerTier;
import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class VoidwalkerSpawnEggItem extends ForgeSpawnEggItem {
    private static final String TAG = Utils.snakeToCamel(ModEntityNames.VOIDWALKER + ModItems.SPAWN_EGG_SUFFIX) + "." + VoidwalkerTier.class.getSimpleName();
    private static VoidwalkerTier currentTier;

    public VoidwalkerSpawnEggItem(Supplier<? extends EntityType<?>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return max(super.getRarity(stack), getTier(stack).getRarity());
    }

    private static Rarity max(Rarity a, Rarity b) {
        return a.ordinal() >= b.ordinal() ? a : b;
    }

    public static VoidwalkerTier getCurrentTier() {
        return currentTier;
    }

    public static void setCurrentTier(@Nullable VoidwalkerTier currentTier) {
        VoidwalkerSpawnEggItem.currentTier = currentTier;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        setCurrentTier(getTier(stack));
        ActionResult<ItemStack> result = super.use(world, player, hand);
        setCurrentTier(null);
        return result;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (allowdedIn(group)) {
            for (VoidwalkerTier tier : VoidwalkerTier.values()) {
                ItemStack stack = new ItemStack(this);
                setTier(stack, tier);
                stacks.add(stack);
            }
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        setCurrentTier(getTier(context.getItemInHand()));
        ActionResultType type = super.useOn(context);
        setCurrentTier(null);
        return type;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        VoidwalkerTier type = getTier(stack);
        if (type.isOrdinary()) {
            return super.getName(stack);
        }
        return type.makeSpawnEggDescription(this);
    }

    public static VoidwalkerTier getTier(ItemStack stack) {
        if (!stack.hasTag()) {
            return VoidwalkerTier.ORDINARY;
        }
        return VoidwalkerTier.byId(stack.getTag().getInt(TAG));
    }

    public static void setTier(ItemStack stack, VoidwalkerTier tier) {
        stack.getOrCreateTag().putInt(TAG, tier.getId());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || getTier(stack).strongerThan(VoidwalkerTier.EXTRAORDINARY);
    }
}
