package lych.soullery.extension.control;

import lych.soullery.mixin.client.ActiveRenderInfoAccessor;
import lych.soullery.network.MindOperatorNetwork;
import lych.soullery.network.MovementData;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.vector.Vector3d;
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
    public static void handleMovementC(MobEntity operatingMob, ClientPlayerEntity player, MovementInput movement) {
        MindOperatorNetwork.MOVEMENTS.sendToServer(new MovementData(operatingMob.getId(), player.isAutoJumpEnabled(), movement));
    }

    public static <T extends MobEntity> void handleMovementS(T operatingMob, ServerPlayerEntity player, MindOperator<? super T> operator, MovementData movement) {
        operator.handleMovement(operatingMob, player, movement);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setupCamera(MobEntity operatingMob, PlayerEntity player, EntityViewRenderEvent.CameraSetup event) {
        if (!getCameraType().isFirstPerson()) {
            Vector3d target = operatingMob.getEyePosition((float) event.getRenderPartialTicks());
            ActiveRenderInfoAccessor infoA = (ActiveRenderInfoAccessor) event.getInfo();
            infoA.callSetPosition(target.x, target.y, target.z);
            infoA.callMove(-infoA.callGetMaxZoom(Math.max(4, operatingMob.getBoundingBox().getSize() + 2.5)), 0, 0);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void resetCamera(EntityViewRenderEvent.CameraSetup event) {}

    @OnlyIn(Dist.CLIENT)
    private static PointOfView getCameraType() {
        return Minecraft.getInstance().options.getCameraType();
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleFOVModifier(FOVUpdateEvent event) {
        if (getCameraType().isFirstPerson()) {
            event.setNewfov(event.getNewfov() * 1.4f);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void resetPlayerMovement(MovementInput movement) {
        movement.jumping = false;
        movement.forwardImpulse = 0;
        movement.leftImpulse = 0;
        movement.up = false;
        movement.down = false;
        movement.left = false;
        movement.right = false;
        movement.shiftKeyDown = false;
    }

    public static void handleMelee(ServerPlayerEntity player, MobEntity operatingMob) {
        MindOperator<?> operator = (MindOperator<?>) SoulManager.getControllers(operatingMob).peek();
        operator.handleMeleeRaw(operatingMob, player);
    }

    public static void handleRightClick(ServerPlayerEntity player, MobEntity operatingMob) {
        MindOperator<?> operator = (MindOperator<?>) SoulManager.getControllers(operatingMob).peek();
        operator.handleRightClickRaw(operatingMob, player);
    }

    @Nullable
    public static Controller<?> getActiveController(ServerWorld world, MobEntity mob) {
        return SoulManager.getControllers(mob).peek();
    }
}
