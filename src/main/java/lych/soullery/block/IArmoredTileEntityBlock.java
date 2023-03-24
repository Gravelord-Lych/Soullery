package lych.soullery.block;

import lych.soullery.Soullery;
import lych.soullery.block.entity.IArmoredBlockEntity;
import lych.soullery.config.ConfigHelper;
import lych.soullery.util.ArrayUtils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface IArmoredTileEntityBlock<C extends TileEntity, P extends TileEntity & IArmoredBlockEntity<C>> extends IArmoredBlock {
    @Nullable
    BlockState doGetChild(World level, BlockPos pos, BlockState parent, P oldBlockEntity);

    @Override
    default void restoreFrom(World level, BlockPos pos, BlockState parent, BlockState child, @Nullable TileEntity oldBlockEntity0) {
        TileEntity newBlockEntity0 = level.getBlockEntity(pos);
        if (oldBlockEntity0 == null || newBlockEntity0 == null) {
            ExceptionHandler.handleNull(pos, parent);
            return;
        }
        try {
            @SuppressWarnings("unchecked")
            P oldBlockEntity = (P) oldBlockEntity0;
            @SuppressWarnings("unchecked")
            C newBlockEntity = (C) newBlockEntity0;
            doRestore(level, pos, parent, child, oldBlockEntity, newBlockEntity);
        } catch (ClassCastException e) {
            ExceptionHandler.handleMismatch(pos, parent, e);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    default BlockState getChild(World level, BlockPos pos, BlockState parent, @Nullable TileEntity oldBlockEntity0) {
        if (oldBlockEntity0 == null) {
            ExceptionHandler.handleNull(pos, parent);
            return null;
        }
        try {
            return doGetChild(level, pos, parent, (P) oldBlockEntity0);
        } catch (ClassCastException e) {
            ExceptionHandler.handleMismatch(pos, parent, e);
            return null;
        }
    }

    default void doRestore(World level, BlockPos pos, BlockState parent, BlockState child, P oldBlockEntity, C newBlockEntity) {
        oldBlockEntity.handleChild(level, pos, parent, child, newBlockEntity);
    }
}
class ExceptionHandler {
    static void handleNull(BlockPos pos, BlockState parent) {
        if (ConfigHelper.shouldFailhard()) {
            throw new NullPointerException(ConfigHelper.FAILHARD_MESSAGE + String.format("Exception in handling armored block %s at [%s, %s, %s] -> null", parent.getBlock().getClass().getName(), pos.getX(), pos.getY(), pos.getZ()));
        }
        Soullery.LOGGER.error("Failed to handle armored block {} at [{}, {}, {}] because BlockEntity does not exist. ", parent.getBlock().getRegistryName(), pos.getX(), pos.getY(), pos.getZ());
    }

    static void handleMismatch(BlockPos pos, BlockState parent, ClassCastException e) {
        if (ConfigHelper.shouldFailhard()) {
            throw new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + String.format("Exception in handling armored block %s at [%s, %s, %s] -> Type mismatch", parent.getBlock().getClass().getName(), pos.getX(), pos.getY(), pos.getZ()), e);
        }
        String[] msg = e.getMessage().split(" ");
        Soullery.LOGGER.error("Failed to handle armored block {} at [{}, {}, {}] " +
                        "because BlockEntities' type do not match. " +
                        "Required: {}, Provided: {}",
                parent.getBlock().getRegistryName(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                ArrayUtils.last(ArrayUtils.last(msg).split("\\.")),
                ArrayUtils.last(msg[0].split("\\.")));
    }
}
