package lych.soullery.extension.control.attack;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;

public class EndermanRightClickHandler implements TargetNotNeededRightClickHandler<EndermanEntity> {
    private int cooldown = 10;

    @Override
    public void handleRightClick(EndermanEntity operatingEnderman, ServerPlayerEntity player) {
        if (cooldown > 0) {
            return;
        }
        Vector3d viewVector = operatingEnderman.getViewVector(1);
        double x = viewVector.x * 10;
        double y = operatingEnderman.getY();
        double z = viewVector.z * 10;
        double dist = Math.sqrt(x * x + z * z);
        if (dist <= 1.0E-04) {
            return;
        }
        x /= dist;
        z /= dist;
        callTeleport(operatingEnderman, x, y, z);
        cooldown = 20;
    }

    @Override
    public void handleRightClick(EndermanEntity operatingEnderman, LivingEntity target, ServerPlayerEntity player) {
        if (operatingEnderman.distanceToSqr(target) >= 3 * 3) {
            callTeleport(operatingEnderman, target.getX(), target.getY(), target.getZ());
            cooldown = 20;
        } else {
            TargetNotNeededRightClickHandler.super.handleRightClick(operatingEnderman, target, player);
        }
    }

    @Override
    public void tick(EndermanEntity operatingEnderman, ServerPlayerEntity player) {
        TargetNotNeededRightClickHandler.super.tick(operatingEnderman, player);
        if (cooldown > 0) {
            cooldown--;
        }
    }

    private boolean callTeleport(EndermanEntity enderman, double x, double y, double z) {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(x, y, z);
        while (mutablePos.getY() > 0 && !enderman.level.getBlockState(mutablePos).getMaterial().blocksMotion()) {
            mutablePos.move(Direction.DOWN);
        }

        BlockState state = enderman.level.getBlockState(mutablePos);
        boolean onSolid = state.getMaterial().blocksMotion();
        boolean water = state.getFluidState().is(FluidTags.WATER);

        if (onSolid && !water) {
            EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(enderman, x, y, z);
            if (event.isCanceled()) {
                return false;
            }
            boolean teleported = enderman.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
            if (teleported && !enderman.isSilent()) {
                enderman.level.playSound(null, enderman.xo, enderman.yo, enderman.zo, SoundEvents.ENDERMAN_TELEPORT, enderman.getSoundSource(), 1, 1);
                enderman.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
            }
            return teleported;
        }
        return false;
    }
}
