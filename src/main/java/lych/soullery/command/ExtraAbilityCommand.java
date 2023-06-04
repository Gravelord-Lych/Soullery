package lych.soullery.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lych.soullery.Soullery;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.command.argument.ExtraAbilityArgument;
import lych.soullery.item.ExtraAbilityCarrierItem;
import lych.soullery.item.ModItems;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public final class ExtraAbilityCommand {
    private ExtraAbilityCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("exa").requires(source -> source.hasPermission(2))
                /*.then(literal("add")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("exa", new ExtraAbilityArgument())
                                        .executes(context -> addExtraAbility(context, EntityArgument.getPlayer(context, "player"))))))
                .then(literal("clear")
                        .executes(context -> removeAllExtraAbilities(context, context.getSource().getPlayerOrException()))
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> removeAllExtraAbilities(context, EntityArgument.getPlayer(context, "player")))
                                .then(argument("exa", new ExtraAbilityArgument())
                                        .executes(context -> removeExtraAbility(context, EntityArgument.getPlayer(context, "player"))))))*/
                .then(literal("show")
                        .executes(context -> showExtraAbilities(context, context.getSource().getPlayerOrException(), false))
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> showExtraAbilities(context, EntityArgument.getPlayer(context, "player"), true))))
                .then(literal("gc")
                        .then(argument("targets", EntityArgument.players())
                                .then(argument("exa", new ExtraAbilityArgument())
                                        .executes(context -> gc(context, EntityArgument.getPlayers(context, "targets"), 1))
                                        .then(argument("count", IntegerArgumentType.integer(1))
                                                .executes(context -> gc(context, EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "count"))))))));
    }

    private static int removeAllExtraAbilities(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        if (((IPlayerEntityMixin) player).getExtraAbilities().isEmpty()) {
            context.getSource().sendFailure(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.remove_all_failed")));
            return 0;
        }
        ((IPlayerEntityMixin) player).setExtraAbilities(Collections.emptySet());
        context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.remove_all"), player.getDisplayName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addExtraAbility(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        IExtraAbility exa = ExtraAbilityArgument.getExa(context, "exa");
        if (exa.addTo(player)) {
            context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.add_success"), exa.getDisplayName().copy().withStyle(exa.getStyle()), player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.add_failed"), exa.getDisplayName(), player.getDisplayName()));
            return 0;
        }
    }

    private static int removeExtraAbility(CommandContext<CommandSource> context, ServerPlayerEntity player) {
        IExtraAbility exa = ExtraAbilityArgument.getExa(context, "exa");
        if (exa.removeFrom(player)) {
            context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.remove_success"), exa.getDisplayName().copy().withStyle(exa.getStyle()), player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.remove_failed"), exa.getDisplayName(), player.getDisplayName()));
            return 0;
        }
    }

    private static int showExtraAbilities(CommandContext<CommandSource> context, ServerPlayerEntity player, boolean broadcastToAdmins) {
        if (((IPlayerEntityMixin) player).getExtraAbilities().isEmpty()) {
            context.getSource().sendFailure(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.no_extra_abilities")));
            return 0;
        }
        int count = 0;
        context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "exa.show_extra_abilities"), player.getDisplayName()), broadcastToAdmins);
        for (IExtraAbility exa : ((IPlayerEntityMixin) player).getExtraAbilities().stream().sorted(IExtraAbility::compareToOnlyByName).collect(Collectors.toList())) {
            context.getSource().sendSuccess(exa.getDisplayName().copy().withStyle(exa.getStyle()), false);
            count++;
        }
        return count;
    }

    private static int gc(CommandContext<CommandSource> context, Collection<ServerPlayerEntity> players, int count) throws CommandSyntaxException {
        IExtraAbility exa = ExtraAbilityArgument.getExa(context, "exa");
        ItemStack stack = new ItemStack(ModItems.EXTRA_ABILITY_CARRIER);
        ExtraAbilityCarrierItem.setExa(stack, exa);
        return giveItem(context.getSource(), new ItemInput(stack.getItem(), stack.getTag()), players, count);
    }

    /**
     * [VanillaCopy] {@link net.minecraft.command.impl.GiveCommand GiveCommand}
     */
    @SuppressWarnings("deprecation")
    private static int giveItem(CommandSource source, ItemInput input, Collection<ServerPlayerEntity> players, int count) throws CommandSyntaxException {
        for (ServerPlayerEntity player : players) {
            int cnt = count;
            while (cnt > 0) {
                int d = Math.min(input.getItem().getMaxStackSize(), cnt);
                cnt -= d;
                ItemStack stack = input.createItemStack(d, false);
                boolean add = player.inventory.add(stack);
                if (add && stack.isEmpty()) {
                    stack.setCount(1);
                    ItemEntity entity = player.drop(stack, false);
                    if (entity != null) {
                        entity.makeFakeItem();
                    }
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1) * 2);
                    player.inventoryMenu.broadcastChanges();
                } else {
                    ItemEntity entity = player.drop(stack, false);
                    if (entity != null) {
                        entity.setNoPickUpDelay();
                        entity.setOwner(player.getUUID());
                    }
                }
            }
        }

        if (players.size() == 1) {
            source.sendSuccess(new TranslationTextComponent("commands.give.success.single", count, input.createItemStack(count, false).getDisplayName(), players.iterator().next().getDisplayName()), true);
        } else {
            source.sendSuccess(new TranslationTextComponent("commands.give.success.single", count, input.createItemStack(count, false).getDisplayName(), players.size()), true);
        }

        return players.size();
    }
}
