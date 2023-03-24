package lych.soullery.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import lych.soullery.Soullery;
import lych.soullery.util.SoulEnergies;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.mutable.MutableInt;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public final class SoulEnergyCommand {
    private static final SimpleCommandExceptionType ERROR_EMPTY = new SimpleCommandExceptionType(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.empty_stack")));
    private static final SimpleCommandExceptionType ERROR_SAME_STACK = new SimpleCommandExceptionType(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.same_stack")));
    private static final DynamicCommandExceptionType ERROR_STACK_NOT_FOUND = new DynamicCommandExceptionType(ordinal -> new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.stack_not_found"), ordinal));
    private static final Dynamic2CommandExceptionType ILLEGAL_VALUE = new Dynamic2CommandExceptionType((expected, provided) -> new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.illegal_value"), provided, expected));

    private SoulEnergyCommand() {}

    /**
     * Items in players' hands will be the first to find by IntegerArgumentType "ordinal" (start from 0), then players' armors and items in their hotbars, and finally their whole inventory.
     */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> node = dispatcher.register(literal("senergy").requires(source -> source.hasPermission(2))
                .then(literal("give")
                        .then(argument("amount", IntegerArgumentType.integer())
                                .executes(SoulEnergyCommand::giveEnergy)
                                .then(argument("ordinal", IntegerArgumentType.integer(0))
                                        .executes(context -> giveEnergy(context, IntegerArgumentType.getInteger(context, "ordinal"))))))
                .then(literal("set")
                        .then(argument("value", IntegerArgumentType.integer(0))
                                .executes(SoulEnergyCommand::setEnergy)
                                .then(argument("ordinal", IntegerArgumentType.integer(0))
                                        .executes(context -> setEnergy(context, IntegerArgumentType.getInteger(context, "ordinal"))))))
                .then(literal("transfer")
                        .then(argument("amount", IntegerArgumentType.integer())
                                .then(argument("sourceOrdinal", IntegerArgumentType.integer(0))
                                        .then(argument("targetOrdinal", IntegerArgumentType.integer(0))
                                                .executes(context -> transferEnergy(context, IntegerArgumentType.getInteger(context, "sourceOrdinal"), IntegerArgumentType.getInteger(context, "targetOrdinal"), IntegerArgumentType.getInteger(context, "amount"))))))
                ));
    }

    private static int giveEnergy(CommandContext<CommandSource> context) throws CommandSyntaxException {
        return giveEnergy(context, SoulEnergies.getSEContainer(context.getSource().getPlayerOrException()));
    }

    private static int giveEnergy(CommandContext<CommandSource> context, int ordinal) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        NonNullList<ItemStack> stacks = SoulEnergies.getSEContainers(player);
        if (stacks.isEmpty()) {
            throw ERROR_EMPTY.create();
        } else if (ordinal < 0 || ordinal >= stacks.size()) {
            throw ERROR_STACK_NOT_FOUND.create(ordinal);
        }
        return giveEnergy(context, stacks.get(ordinal));
    }

    private static int giveEnergy(CommandContext<CommandSource> context, ItemStack stack) throws CommandSyntaxException {
        if (stack.isEmpty()) {
            throw ERROR_EMPTY.create();
        }
        int amount = IntegerArgumentType.getInteger(context, "amount");
        MutableInt actualChanged = new MutableInt();
//        SoulCraft.LOGGER.info(amount);
        SoulEnergies.of(stack).ifPresent(ses -> actualChanged.setValue(ses.forceChangeSoulEnergy(amount)));
        if (amount == actualChanged.intValue()) {
            context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.changed_success" + (amount > 0 ? "_receive" : "_extract")), Math.abs(amount)), true);
        } else {
            if (amount >= 0 && actualChanged.intValue() >= 0) {
                context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.changed_half_success_receive"), amount, actualChanged.intValue()), true);
            } else if (amount <= 0 && actualChanged.intValue() <= 0){
                context.getSource().sendSuccess(
                        new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.changed_half_success_extract"), amount, actualChanged.intValue()), true);
            } else {
                throw new IllegalStateException(String.format("Illegal changed amount: %d, original is %d", actualChanged.intValue(), amount));
            }
        }
        return actualChanged.intValue();
    }

    private static int setEnergy(CommandContext<CommandSource> context) throws CommandSyntaxException {
        return setEnergy(context, SoulEnergies.getSEContainer(context.getSource().getPlayerOrException()));
    }

    private static int setEnergy(CommandContext<CommandSource> context, int ordinal) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        NonNullList<ItemStack> stacks = SoulEnergies.getSEContainers(player);
        if (stacks.isEmpty()) {
            throw ERROR_EMPTY.create();
        } else if (ordinal < 0 || ordinal >= stacks.size()) {
            throw ERROR_STACK_NOT_FOUND.create(ordinal);
        }
        return setEnergy(context, stacks.get(ordinal));
    }

    private static int setEnergy(CommandContext<CommandSource> context, ItemStack stack) throws CommandSyntaxException {
        if (stack.isEmpty()) {
            throw ERROR_EMPTY.create();
        }
        int expectedNewValue = IntegerArgumentType.getInteger(context, "value");
        if (expectedNewValue < 0) {
            throw new IllegalStateException(String.format("ExpectedNewValue is negative, maybe %s is broken", Soullery.MOD_NAME));
        }
        MutableInt changed = new MutableInt();
        MutableInt max = new MutableInt();
        SoulEnergies.of(stack).ifPresent(ses -> {
            max.setValue(ses.getMaxSoulEnergyStored());
            changed.setValue(expectedNewValue - ses.getSoulEnergyStored());
        });
        if (expectedNewValue > max.intValue()) {
            throw ILLEGAL_VALUE.create(max.intValue(), expectedNewValue);
        }
        SoulEnergies.of(stack).ifPresent(ses -> ses.forceChangeSoulEnergy(changed.getValue()));
        context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.set_success"), expectedNewValue), true);
        return expectedNewValue;
    }

    private static int transferEnergy(CommandContext<CommandSource> context, int src, int target, int amount) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        NonNullList<ItemStack> stacks = SoulEnergies.getSEContainers(player);
        if (stacks.isEmpty()) {
            throw ERROR_EMPTY.create();
        } else if (src < 0 || src >= stacks.size()) {
            throw ERROR_STACK_NOT_FOUND.create(src);
        } else if (target < 0 || target >= stacks.size()) {
            throw ERROR_STACK_NOT_FOUND.create(target);
        }
        return transferEnergy(context, stacks.get(src), stacks.get(target), amount);
    }

    private static int transferEnergy(CommandContext<CommandSource> context, ItemStack src, ItemStack target, int amount) throws CommandSyntaxException {
        if (src.isEmpty() || target.isEmpty()) {
            throw ERROR_EMPTY.create();
        } else if (src == target) {
            throw ERROR_SAME_STACK.create();
        }
        int actualTransferValue = SoulEnergies.transfer(src, target, amount);
        if (amount == actualTransferValue) {
            context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.transferred_success"), amount), true);
        } else if (actualTransferValue >= 0) {
            context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "soulenergy.transferred_half_success"), amount, actualTransferValue), true);
        } else {
            throw new IllegalStateException("Illegal actualTransferValue value: " + actualTransferValue);
        }
        return actualTransferValue;
    }
}
