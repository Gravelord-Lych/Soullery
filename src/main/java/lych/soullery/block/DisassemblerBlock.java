package lych.soullery.block;

import lych.soullery.entity.monster.boss.enchanter.EnchantedArmorStandEntity;
import lych.soullery.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DisassemblerBlock extends Block {
    public DisassemblerBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
        if (level.isClientSide()) {
            return ActionResultType.SUCCESS;
        }
        ServerWorld world = (ServerWorld) level;
        WorldUtils.makeFakeExplosionServerside(pos, world);
        world.destroyBlock(pos, false);
        world.destroyBlock(pos.below(), false);
        world.getEntitiesOfClass(EnchantedArmorStandEntity.class, new AxisAlignedBB(pos).inflate(5, 3, 5))
                .stream()
                .filter(eas -> sqr(eas.getX() - pos.getX()) + sqr(eas.getZ() - pos.getZ()) <= sqr(5))
                .forEach(eas -> eas.hurt(DamageSource.playerAttack(player), Float.MAX_VALUE));
        return ActionResultType.CONSUME;
    }

    private static double sqr(double d) {
        return d * d;
    }
}
