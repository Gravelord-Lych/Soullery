package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.util.InventoryUtils;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.SoulEnergies;
import lych.soullery.util.Utils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.List;

public class EnchantingWandItem extends Item implements IModeChangeable, ISkillPerformable {
    public static final boolean ITEM = true;
    public static final boolean BLOCK = !ITEM;
    private static final boolean DEFAULT_MODE = ITEM;
    private static final int ENERGY_COST = 1500;
    private static final int XPL_COST = 5;
    private static final int COOLDOWN = 20 * 20;
    private static final String TAG_MODE = Utils.snakeToCamel(ModItemNames.ENCHANTING_WAND) + ".Mode";

    public EnchantingWandItem(Properties properties) {
        super(properties);
    }

    public static boolean canEnchantBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof EnchantingWandItem)) {
            return false;
        }
        return getMode(stack) == BLOCK;
    }

    public static boolean getMode(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(TAG_MODE) ? stack.getTag().getBoolean(TAG_MODE) : DEFAULT_MODE;
    }

    public static void setMode(ItemStack stack, boolean mode) {
        stack.getOrCreateTag().putBoolean(TAG_MODE, mode);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tips, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tips, flag);
        tips.add(makeText("mode").append(getModeText(stack)));
    }

    @Override
    public void changeMode(ItemStack stack, ServerPlayerEntity player, boolean reverse) {
        setMode(stack, !getMode(stack));
        player.sendMessage(makeText("set_mode").append(getModeText(stack)), Util.NIL_UUID);
    }

    private static IFormattableTextComponent getModeText(ItemStack stack) {
        return getMode(stack) == ITEM ? makeText("mode.item") : makeText("mode.block");
    }

    private static IFormattableTextComponent makeText(String name) {
        return new TranslationTextComponent(Soullery.prefixMsg("item", String.format("%s.%s", ModItemNames.ENCHANTING_WAND, name)));
    }

    @Override
    public boolean perform(ItemStack stack, ServerPlayerEntity player) {
        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            return false;
        }
        if (!player.abilities.instabuild && player.experienceLevel < XPL_COST) {
            return false;
        }
        if (getMode(stack) == ITEM) {
            for (ItemStack stackIn : InventoryUtils.getSortedList(player.inventory)) {
                if (stackIn != stack && stackIn.isEnchantable() && SoulEnergies.getExtractableSEOf(player) >= ENERGY_COST) {
                    EnchantmentHelper.enchantItem(player.getRandom(), stackIn, 30, false);
                    if (stackIn.isEnchanted() && SoulEnergies.cost(player, ENERGY_COST)) {
                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.ENCHANTING_WAND_USE.get(), player.getSoundSource(), 1, 1);
                        if (!player.abilities.instabuild) {
                            player.setExperienceLevels(player.experienceLevel - XPL_COST);
                        }
                        player.getCooldowns().addCooldown(this, COOLDOWN);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return true;
    }
}
