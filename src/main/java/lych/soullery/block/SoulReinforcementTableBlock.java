package lych.soullery.block;

import lych.soullery.Soullery;
import lych.soullery.gui.container.SoulReinforcementTableContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class SoulReinforcementTableBlock extends Block {
    private static final ITextComponent CONTAINER_TITLE = new TranslationTextComponent(Soullery.prefixMsg("gui", "soul_reinforcement_table"));

    public SoulReinforcementTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(world, pos));
            return ActionResultType.CONSUME;
        }
    }

    @Override
    public INamedContainerProvider getMenuProvider(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedContainerProvider((containerCounter, inventory, player) -> new SoulReinforcementTableContainer(containerCounter, inventory, new IntArray(5), IWorldPosCallable.create(world, pos)), CONTAINER_TITLE);
    }
}
