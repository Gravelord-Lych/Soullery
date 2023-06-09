package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.entity.projectile.SoulifiedEnderPearlEntity;
import lych.soullery.entity.projectile.SoulifiedEnderPearlEntity.Gravity;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class EnderLauncherItem extends AbstractWandItem<EnderLauncherItem> implements IModeChangeable {
    public static final int ROTATE_TICKS = 16;
    public static final int COUNT = 8;
    public static final ResourceLocation ROTATION = Soullery.prefix(ModItemNames.ENDER_LAUNCHER + "_rotation");
    private static final String TAG = Utils.snakeToCamel(ModItemNames.ENDER_LAUNCHER) + ".";

    public EnderLauncherItem(Properties properties, int tier) {
        super(properties, stack -> getGravity(stack).isUnstable() ? 400 : 200, tier);
    }

    @Nullable
    @Override
    protected ActionResultType performWandUse(ServerWorld level, ServerPlayerEntity player, Hand hand) {
        getTierMap().values().forEach(item -> player.getCooldowns().addCooldown(item, 20));
        player.getCooldowns().addCooldown(Items.ENDER_PEARL, 20);

        SoulifiedEnderPearlEntity pearl = new SoulifiedEnderPearlEntity(level, player);
        pearl.setPurified(getTier() > 1);
        pearl.shootFromRotation(player, player.xRot, player.yRot, 0, 1.5f, 1);
        ItemStack stack = player.getItemInHand(hand);
        if (stack.hasCustomHoverName()) {
            pearl.setCustomName(stack.getHoverName());
            pearl.setCustomNameVisible(true);
        }
        pearl.setGravity(getGravity(stack));
        level.addFreshEntity(pearl);
        player.awardStat(Stats.ITEM_USED.get(this));

        getUUID(stack);
        ((IPlayerEntityMixin) player).setUsingEnderLauncher(stack);
        return ActionResultType.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tips, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tips, flag);
        if (getTier() > 1) {
            ITextComponent gravity = Gravity.GRAVITY.copy().append(getGravity(stack).makeText());
            tips.add(gravity);
        }
    }

    @Nullable
    @Override
    public SoundEvent getSound() {
        return SoundEvents.ENDER_PEARL_THROW;
    }

    public static Gravity getGravity(ItemStack stack) {
        return Gravity.byId(stack.getTag().getInt(TAG + "Gravity"));
    }

    public static void setGravity(ItemStack stack, Gravity gravity) {
        stack.getOrCreateTag().putInt(TAG + "Gravity", gravity.getId());
    }

    public static UUID getUUID(ItemStack stack) {
        if (!(stack.getItem() instanceof EnderLauncherItem)) {
            return Util.NIL_UUID;
        }
        if (stack.hasTag() && stack.getTag().contains(TAG + "UUID")) {
            return stack.getTag().getUUID(TAG + "UUID");
        }
        return initUUID(stack);
    }

    @NotNull
    private static UUID initUUID(ItemStack stack) {
        UUID uuid = MathHelper.createInsecureUUID(random);
        stack.getOrCreateTag().putUUID(TAG + "UUID", uuid);
        return uuid;
    }

    @Override
    public void changeMode(ItemStack stack, ServerPlayerEntity player, boolean reverse) {
        if (getTier() > 1) {
            Gravity gravity = reverse ? getGravity(stack).last() : getGravity(stack).next();
            setGravity(stack, gravity);
            gravity.sendSetGravityMessage(player);
        }
    }
}
