package lych.soullery.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lych.soullery.Soullery;
import lych.soullery.command.argument.TieredBossArgument;
import lych.soullery.entity.iface.ITieredMob;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public final class ResetBossCommand {
    private ResetBossCommand() {}

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(literal("resetboss").requires(source -> source.hasPermission(2))
                .then(argument("type", new TieredBossArgument())
                        .executes(context -> clearTierOf(getType(context), context))
                        .then(argument("tier", IntegerArgumentType.integer(ITieredMob.MIN_TIER, ITieredMob.MAX_TIER))
                                .executes(context -> clearTierOf(getType(context), context, IntegerArgumentType.getInteger(context, "tier"))))));
    }

    private static EntityType<?> getType(CommandContext<CommandSource> context) throws CommandSyntaxException {
        return Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(TieredBossArgument.getSummonableEntity(context, "type")));
    }

    private static int clearTierOf(EntityType<?> type, CommandContext<CommandSource> context) throws CommandSyntaxException {
        return clearTierOf(type, context, ITieredMob.MIN_TIER);
    }

    private static <T extends Entity> int clearTierOf(EntityType<? extends T> type, CommandContext<CommandSource> context, int tier) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayerOrException();
        ((IPlayerEntityMixin) player).setTier(type, tier);
        context.getSource().sendSuccess(new TranslationTextComponent(Soullery.prefixMsg("commands", "resetboss.success"), new TranslationTextComponent(type.getDescriptionId()).getString(), tier, player.getDisplayName()), true);
        return Command.SINGLE_SUCCESS;
    }
}
