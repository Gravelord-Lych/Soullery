package lych.soullery.api.exa;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public interface PlayerBuff {
    /**
     * Called when a player gets its corresponding {@link lych.soullery.api.exa.IExtraAbility ExtraAbility}.<br>
     * Will be called when reloading world, but will not be called when the player dies.
     * @param player The player
     * @param world The world
     */
    void startApplyingTo(PlayerEntity player, World world);

    /**
     * Called when a player loses its corresponding {@link lych.soullery.api.exa.IExtraAbility ExtraAbility}.<br>
     * Will not be called when syncing data.
     * @param player The player
     * @param world The world
     */
    void stopApplyingTo(PlayerEntity player, World world);

    /**
     * Reloads the buff when a player dies or game restarts
     * @param oldPlayer The old player, <code>null</code> if the method is called when game restarts
     * @param newPlayer The new player
     */
    default void reload(@Nullable PlayerEntity oldPlayer, PlayerEntity newPlayer) {}

    void serverTick(ServerPlayerEntity player, ServerWorld world);

    default void clientTick(ClientPlayerEntity player, ClientWorld world) {}
}
