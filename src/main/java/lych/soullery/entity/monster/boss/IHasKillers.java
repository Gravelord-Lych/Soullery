package lych.soullery.entity.monster.boss;

import lych.soullery.util.EntityUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IHasKillers {
    Set<UUID> getKillers();

    default void iterateKillers(World world,  Consumer<? super ServerPlayerEntity> consumer) {
        iterateKillers(world, EntityUtils::isSurvival, consumer);
    }

    default void iterateKillers(World world, Predicate<? super ServerPlayerEntity> predicate, Consumer<? super ServerPlayerEntity> consumer) {
        if (!world.isClientSide()) {
            for (UUID uuid : getKillers()) {
                ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUUID(uuid);
                if (predicate.test(player)) {
                    consumer.accept(player);
                }
            }
        }
    }

    default float getAddKillerThreshold() {
        return 0;
    }

    default boolean requiresSurvivalModeKiller() {
        return true;
    }

    default void markNonSurvivalPlayersInvalid(World world, Set<UUID> invalidPlayers) {
        markNonSurvivalPlayersInvalid(world, getKillers(), invalidPlayers);
    }

    static void markNonSurvivalPlayersInvalid(World world, Set<UUID> players, Set<UUID> invalidPlayers) {
        if (!world.isClientSide()) {
            for (UUID uuid : players) {
                ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUUID(uuid);
                if (!EntityUtils.isSurvival(player)) {
                    invalidPlayers.add(uuid);
                }
            }
        }
    }
}
