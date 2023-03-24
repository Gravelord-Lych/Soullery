package lych.soullery.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lych.soullery.Soullery;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ReinforcementArgument implements ArgumentType<Reinforcement> {
    private static final Collection<String> EXAMPLES = Arrays.asList("minecraft:zombie", "skeleton");
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_REINFORCEMENT = new DynamicCommandExceptionType(rf -> new TranslationTextComponent(Soullery.prefixMsg("commands", "reinforcement.reinforcement_not_found"), rf));

    public static Reinforcement getReinforcement(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Reinforcement.class);
    }

    @Override
    public Reinforcement parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation registryName = ResourceLocation.read(reader);
        EntityType<?> type = ForgeRegistries.ENTITIES.getValue(registryName);
        if (!ForgeRegistries.ENTITIES.containsKey(registryName)) {
            type = null;
        }
        Reinforcement reinforcement = Utils.applyIfNonnull(type, Reinforcements::get);
        if (reinforcement == null) {
            throw ERROR_UNKNOWN_REINFORCEMENT.create(registryName);
        }
        return reinforcement;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(Reinforcements.getReinforcements().keySet().stream().map(Utils::getRegistryName), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
