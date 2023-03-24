package lych.soullery.item;

import lych.soullery.block.ModBlocks;
import lych.soullery.util.DefaultValues;
import lych.soullery.util.SoulEnergies;
import lych.soullery.world.event.manager.WorldTickerManager;
import lych.soullery.world.event.ticker.WorldTickers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class SoulPowderItem extends Item {
    private static final int ENERGY_REQUIRED = 114514;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = state -> state != null && (state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN));
    @Nullable
    private static BlockPattern meta8;

    public SoulPowderItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack stack = context.getItemInHand();
        if (stack.getItem() instanceof SoulPowderItem && stack.getCount() == 63) {
            if (context.getLevel() instanceof ServerWorld && context.getPlayer() != null) {
                return SoulEnergies.cost(context.getPlayer(), ENERGY_REQUIRED, () -> trySpawnMeta08((ServerWorld) context.getLevel(), context.getClickedPos(), stack), DefaultValues.dummyRunnable()) ?
                        ActionResultType.SUCCESS : super.useOn(context);
            }
        }
        return super.useOn(context);
    }

    private static void trySpawnMeta08(ServerWorld world, BlockPos clickedPos, ItemStack stack) {
        @Nullable
        BlockPattern.PatternHelper helper = getMeta08Pattern().find(world, clickedPos);
        if (helper != null) {
            for (int w = 0; w < getMeta08Pattern().getWidth(); ++w) {
                for (int h = 0; h < getMeta08Pattern().getHeight(); ++h) {
                    CachedBlockInfo info = helper.getBlock(w, h, 0);
                    world.setBlock(info.getPos(), Blocks.AIR.defaultBlockState(), 2);
                    world.levelEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, info.getPos(), Block.getId(info.getState()));
                }
            }
            BlockPos pos = helper.getBlock(1, 2, 0).getPos();
            stack.setCount(0);
            WorldTickerManager.start(WorldTickers.SUMMON_META_08, world, pos);
        }
    }

    private static BlockPattern getMeta08Pattern() {
        if (meta8 == null) {
            meta8 = BlockPatternBuilder.start()
                    .aisle("~^~", "###", "~#~")
                    .where('^', CachedBlockInfo.hasState(PUMPKINS_PREDICATE))
                    .where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(ModBlocks.SOUL_METAL_BLOCK).or(BlockStateMatcher.forBlock(ModBlocks.REFINED_SOUL_METAL_BLOCK))))
                    .where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR)))
                    .build();
        }
        return meta8;
    }
}
