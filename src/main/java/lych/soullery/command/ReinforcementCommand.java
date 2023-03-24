package lych.soullery.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lych.soullery.Soullery;
import lych.soullery.command.argument.ReinforcementArgument;
import lych.soullery.config.ConfigHelper;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public final class ReinforcementCommand {
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(player -> new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.no_item"), player));
    private static final DynamicCommandExceptionType ERROR_UNSUPPORTED_ITEM = new DynamicCommandExceptionType(stack -> new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.unsupported_item"), stack));
    private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((level, maxLevel) -> new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.level_too_high"), level, maxLevel));
    private static final DynamicCommandExceptionType ERROR_TOO_MANY_REINFORCEMENTS = new DynamicCommandExceptionType(stack -> new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.too_many"), stack));
    private ReinforcementCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> node = dispatcher.register(literal("reinforcement").requires(source -> source.hasPermission(2))
                .then(literal("add")
                        .then(argument("player", EntityArgument.player())
                                .then(argument("reinforcement", new ReinforcementArgument())
                                        .executes(context -> addReinforcement(context, EntityArgument.getPlayer(context, "player"), 1))
                                        .then(argument("level", IntegerArgumentType.integer(1))
                                                .executes(context -> addReinforcement(context, EntityArgument.getPlayer(context, "player"), IntegerArgumentType.getInteger(context, "level")))))))
                .then(literal("clear")
                        .executes(context -> removeAllReinforcements(context, context.getSource().getPlayerOrException()))
                        .then(argument("player", EntityArgument.player())
                                .executes(context -> removeAllReinforcements(context, EntityArgument.getPlayer(context, "player")))
                                .then(argument("reinforcement", new ReinforcementArgument())
                                        .executes(context -> removeReinforcement(context, EntityArgument.getPlayer(context, "player")))))));

        dispatcher.register(literal("srf").requires(source -> source.hasPermission(2)).redirect(node));
    }


    private static int removeAllReinforcements(CommandContext<CommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            throw ERROR_NO_ITEM.create(player.getDisplayName());
        }
        ReinforcementHelper.putReinforcements(stack, Collections.emptyMap());
        context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.remove_all"), player.getDisplayName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addReinforcement(CommandContext<CommandSource> context, ServerPlayerEntity player, int level) throws CommandSyntaxException {
        Reinforcement reinforcement = ReinforcementArgument.getReinforcement(context, "reinforcement");
        if (level > reinforcement.getMaxLevel()) {
            throw ERROR_LEVEL_TOO_HIGH.create(level, reinforcement.getMaxLevel());
        }
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            throw ERROR_NO_ITEM.create(player.getDisplayName());
        }
        if (ReinforcementHelper.containsReinforcement(stack, reinforcement)) {
            context.getSource().sendFailure(new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.add_failed"), reinforcement.getType().getDescription(), player.getDisplayName()));
            return 0;
        }
        if (ReinforcementHelper.isReinforced(stack)) {
            throw ERROR_TOO_MANY_REINFORCEMENTS.create(stack.getItem().getName(stack).getString());
        }
        if (!reinforcement.isItemSuitable(stack) || ReinforcementHelper.isIncompatible(stack, reinforcement)) {
            throw ERROR_UNSUPPORTED_ITEM.create(stack.getItem().getName(stack).getString());
        }
        ReinforcementHelper.ApplicationStatus status = ReinforcementHelper.addReinforcement(stack, reinforcement, level);
        if (!status.isOk() && ConfigHelper.shouldFailhard()) {
            throw new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + "Reinforcement not added for status " + status);
        }
        context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.add_success"), reinforcement.getType().getDescription().copy().withStyle(reinforcement.getStyle()), player.getDisplayName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int removeReinforcement(CommandContext<CommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        Reinforcement reinforcement = ReinforcementArgument.getReinforcement(context, "reinforcement");
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            throw ERROR_NO_ITEM.create(player.getDisplayName());
        }
        if (ReinforcementHelper.removeReinforcement(stack, reinforcement)) {
            context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.remove_success"), reinforcement.getType().getDescription().copy().withStyle(reinforcement.getStyle()), player.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            context.getSource().sendFailure(new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.remove_failed"), reinforcement.getType().getDescription(), player.getDisplayName()));
            return 0;
        }
    }
}
