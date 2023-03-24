package lych.soullery.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lych.soullery.Soullery;
import lych.soullery.tag.ModEntityTags;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TieredBossArgument extends EntitySummonArgument {
    private static final Collection<String> EXAMPLES = Arrays.asList("soullery:giant_x", "soullery:skeleton_king");
    private static final DynamicCommandExceptionType NON_TIERED = new DynamicCommandExceptionType(obj -> new TranslationTextComponent(Soullery.prefixMsg("commands", "resetboss.non_tiered_boss"), obj));

    public static ResourceLocation getSummonableEntity(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return verifyCanSummon(context.getArgument(name, ResourceLocation.class));
    }

    @SuppressWarnings("deprecation")
    private static ResourceLocation verifyCanSummon(ResourceLocation registryName) throws CommandSyntaxException {
        EntityType<?> type = Registry.ENTITY_TYPE.getOptional(registryName).filter(EntityType::canSummon).orElseThrow(() -> NON_TIERED.create(registryName));
        Optional.of(type).filter(TieredBossArgument::isTiered).orElseThrow(() -> NON_TIERED.create(registryName));
        return registryName;
    }

    @Override
    public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
        return verifyCanSummon(ResourceLocation.read(reader));
    }

    private static <T extends Entity> boolean isTiered(EntityType<? extends T> type) {
        return type.is(ModEntityTags.TIERED_BOSS);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestResource(ForgeRegistries.ENTITIES.getKeys().stream()
                .filter(location -> get(location) != null)
                .filter(location -> getNonnull(location).canSummon())
                .filter(location -> isTiered(getNonnull(location))), builder);
    }

    @Nullable
    private EntityType<?> get(ResourceLocation location) {
        return ForgeRegistries.ENTITIES.getValue(location);
    }

    private EntityType<?> getNonnull(ResourceLocation location) {
        return Objects.requireNonNull(get(location));
    }
}
