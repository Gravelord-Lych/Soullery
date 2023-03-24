package lych.soullery.extension.control;

import lych.soullery.network.MindOperatorNetwork;
import lych.soullery.network.MovementData;
import lych.soullery.network.RotationData;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import org.jetbrains.annotations.Nullable;

public final class MindOperatorSynchronizer {
    private MindOperatorSynchronizer() {}

    @Nullable
    public static MobEntity getOperatingMob(PlayerEntity player) {
        return ((IPlayerEntityMixin) player).getOperatingMob();
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleMovementC(MobEntity operatingMob, PlayerEntity player, MovementInput movement) {
        MindOperatorNetwork.MOVEMENTS.sendToServer(new MovementData(operatingMob.getId(), movement));
    }

    public static <T extends MobEntity> void handleMovementS(T operatingMob, ServerPlayerEntity player, MindOperator<? super T> operator, MovementData movement) {
        operator.handleMovement(operatingMob, player, movement);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setupCamera(MobEntity operatingMob, PlayerEntity player, EntityViewRenderEvent.CameraSetup event) {}

    @OnlyIn(Dist.CLIENT)
    public static void handleFOVModifier(FOVUpdateEvent event) {
        event.setNewfov(event.getNewfov() * 1.4f);
    }

    @OnlyIn(Dist.CLIENT)
    public static void resetPlayerMovement(MovementInput movement) {
        if (!movement.shiftKeyDown) {
            movement.jumping = false;
        }
        movement.forwardImpulse *= movement.shiftKeyDown ? 0.3 : 0;
        movement.leftImpulse *= movement.shiftKeyDown ? 0.3 : 0;
        movement.up = false;
        movement.down = false;
        movement.left = false;
        movement.right = false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleRotationOffsetC(MobEntity operatingMob, double amount) {
        if (amount != 0) {
            MindOperatorNetwork.ROTATIONS.sendToServer(new RotationData(operatingMob.getId(), amount));
        }
    }

    public static <T extends MobEntity> void handleRotationOffsetS(T mob, ServerPlayerEntity player, MindOperator<? super T> operator, double amount) {
        operator.handleRotationOffset(mob, player, amount);
    }

    public static void handleMelee(ServerPlayerEntity player, MobEntity operatingMob) {
        MindOperator<?> operator = (MindOperator<?>) SoulManager.get(player.getLevel()).getControllers(operatingMob).peek();
        operator.handleMeleeRaw(operatingMob, player);
    }

    public static void handleRightClick(ServerPlayerEntity player, MobEntity operatingMob) {
        MindOperator<?> operator = (MindOperator<?>) SoulManager.get(player.getLevel()).getControllers(operatingMob).peek();
        operator.handleRightClickRaw(operatingMob, player);
    }

    @Nullable
    public static Controller<?> getActiveController(ServerWorld world, MobEntity mob) {
        return SoulManager.get(world).getControllers(mob).peek();
    }
}
