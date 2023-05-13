package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.functional.HorcruxEntity;
import lych.soullery.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static lych.soullery.item.ModItems.next;

public class HorcruxCarrierItem extends Item {
    private static final String PREFIX = Utils.snakeToCamel(ModItemNames.HORCRUX_CARRIER) + ".";
    private static final String OWNER = PREFIX + "Owner";
    private static final String OWNER_TEXT = Soullery.prefixMsg("item", ModItemNames.HORCRUX_CARRIER + ".owner");
    private static final String NOT_OWNER_TEXT = Soullery.prefixMsg("item", ModItemNames.HORCRUX_CARRIER + ".not_owner");

    public HorcruxCarrierItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getLevel().isClientSide()) {
            return ActionResultType.SUCCESS;
        }
        UUID owner = getOwner(context.getItemInHand());
        if (owner == null) {
            setOwner(context.getItemInHand(), context.getPlayer().getUUID());
            owner = getOwner(context.getItemInHand());
        }
        if (Objects.equals(context.getPlayer().getUUID(), owner)) {
            HorcruxEntity horcrux = ModEntities.HORCRUX.create(context.getLevel());
            if (horcrux != null) {
                horcrux.moveTo(context.getClickLocation());
                horcrux.yRot = context.getLevel().random.nextFloat() * 360;
                horcrux.setOwner(context.getPlayer());
                context.getLevel().addFreshEntity(horcrux);
                context.getItemInHand().shrink(1);
                return ActionResultType.CONSUME;
            }
            Soullery.LOGGER.error("Horcrux is not summoned");
            return ActionResultType.FAIL;
        }
        context.getPlayer().sendMessage(new TranslationTextComponent(NOT_OWNER_TEXT).withStyle(TextFormatting.RED), Util.NIL_UUID);
        return ActionResultType.FAIL;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if (getOwner(stack) != null) {
            return next(super.getRarity(stack), 2);
        }
        return super.getRarity(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        if (world != null && getOwner(stack) != null) {
            PlayerEntity player = world.getPlayerByUUID(getOwner(stack));
            if (player != null) {
                text.add(new TranslationTextComponent(OWNER_TEXT, player.getDisplayName()));
            }
        }
    }

    @Override
    public void onCraftedBy(ItemStack stack, World world, PlayerEntity player) {
        super.onCraftedBy(stack, world, player);
        setOwner(stack, player.getUUID());
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return true;
    }

    @Nullable
    public static UUID getOwner(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        }
        if (stack.getTag().hasUUID(OWNER)) {
            return stack.getTag().getUUID(OWNER);
        }
        return null;
    }

    public static void setOwner(ItemStack stack, UUID owner) {
        stack.getOrCreateTag().putUUID(OWNER, owner);
    }
}
