package lych.soullery.item;

import lych.soullery.Soullery;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.List;

public abstract class AbstractTickableItem extends Item implements IModeChangeable {
    protected static final String PREFIX = "TickableItem.";
    protected static final String STATE = Soullery.prefixMsg("item", "tickable.state");

    public AbstractTickableItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int index, boolean selected) {
        super.inventoryTick(stack, world, entity, index, selected);
        if (!world.isClientSide() && entity instanceof PlayerEntity && isOn(stack)) {
            serverPlayerInventoryTick(stack, world, (PlayerEntity) entity, index, selected);
        }
    }

    protected abstract void serverPlayerInventoryTick(ItemStack stack, World world, PlayerEntity player, int index, boolean selected);

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(new TranslationTextComponent(STATE).append(new StringTextComponent(isOn(stack) ? "ON" : "OFF").setStyle(Style.EMPTY.withColor(isOn(stack) ? TextFormatting.GREEN : TextFormatting.RED))));
    }

    @Override
    public void changeMode(ItemStack stack, ServerPlayerEntity player, boolean reverse) {
        setOn(stack, !isOn(stack));
    }

    public static boolean isOn(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(PREFIX + "On");
    }

    public static boolean isOff(ItemStack stack) {
        return !isOn(stack);
    }

    public static void setOn(ItemStack stack, boolean on) {
        stack.getOrCreateTag().putBoolean(PREFIX + "On", on);
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return isOn(stack);
    }
}
