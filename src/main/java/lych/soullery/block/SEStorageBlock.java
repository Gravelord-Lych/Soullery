package lych.soullery.block;

import lych.soullery.api.ItemSEContainer;
import lych.soullery.block.entity.SEStorageTileEntity;
import lych.soullery.capability.ItemSoulEnergyProvider;
import lych.soullery.config.ConfigHelper;
import lych.soullery.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

public class SEStorageBlock extends SimpleTileEntityBlock {
    public SEStorageBlock(Properties properties, Supplier<? extends TileEntity> supplier) {
        super(properties, supplier);
        registerDefaultState(stateDefinition.any().setValue(ModBlockStateProperties.SOUL_ENERGY_LEVEL, 0));
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        if (!world.isClientSide() && hand == Hand.MAIN_HAND) {
            TileEntity entity = world.getBlockEntity(pos);
            if (entity instanceof SEStorageTileEntity) {
                SEStorageTileEntity storage = (SEStorageTileEntity) entity;
                ItemStack itemInHand = player.getItemInHand(hand);
                ItemStack inside = storage.getItemInside();
                boolean putGem = false;
                if (inside.isEmpty() && itemInHand.getItem() instanceof ItemSEContainer && ((ItemSEContainer) itemInHand.getItem()).isTransferable(itemInHand)) {
                    storage.getInventory().setItem(0, itemInHand.copy());
                    player.setItemInHand(hand, inside.copy());
                    putGem = true;
                } else if (!inside.isEmpty() && itemInHand.isEmpty()) {
                    player.setItemInHand(hand, inside.copy());
                    storage.getInventory().setItem(0, ItemStack.EMPTY);
                    putGem = true;
                }
                if (!putGem) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, storage, packerBuffer -> packerBuffer.writeBlockPos(entity.getBlockPos()));
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ModBlockStateProperties.SOUL_ENERGY_LEVEL);
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SEStorageTileEntity) {
            SEStorageTileEntity storage = (SEStorageTileEntity) blockEntity;
            ItemStack itemInside = storage.getItemInside();
            if (!world.isClientSide) {
                if (!player.isCreative() || ConfigHelper.canSEBlocksLoot() && storage.getSoulEnergyStored() > 0) {
                    ItemStack stack = new ItemStack(this);
                    stack.getOrCreateTag().putInt(ItemSoulEnergyProvider.SOUL_ENERGY_TAG, storage.getSoulEnergyStored());
                    EntityUtils.spawnItem(world, pos, stack);
                }
                EntityUtils.spawnItem(world, pos, itemInside);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }
}
