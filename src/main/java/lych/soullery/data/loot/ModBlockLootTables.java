package lych.soullery.data.loot;

import lych.soullery.item.ModItems;
import lych.soullery.util.blg.BlockGroup;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.functions.ApplyBonus;
import net.minecraft.loot.functions.SetCount;

import java.util.HashSet;
import java.util.Set;

import static lych.soullery.block.ModBlocks.*;
import static net.minecraft.block.Blocks.SOUL_SOIL;

public class ModBlockLootTables extends BlockLootTables {
    private final Set<Block> knownBlocks = new HashSet<>();

    @Override
    protected void add(Block block, LootTable.Builder builder) {
        knownBlocks.add(block);
        super.add(block, builder);
    }

    @Override
    protected void addTables() {
        dropSelf(CHISELED_SOUL_STONE_BRICKS);
        dropSelf(CRACKED_SOUL_STONE_BRICK_SLAB);
        dropSelf(CRACKED_SOUL_STONE_BRICK_STAIRS);
        dropSelf(CRACKED_SOUL_STONE_BRICK_WALL);
        dropSelf(CRACKED_SOUL_STONE_BRICKS);
        otherWhenSilkTouch(CRIMSON_HYPHAL_SOIL, SOUL_SOIL);
        dropSelf(DECAYED_STONE);
        dropSelf(DECAYED_STONE_BRICK_SLAB);
        dropSelf(DECAYED_STONE_BRICK_STAIRS);
        dropSelf(DECAYED_STONE_BRICK_WALL);
        dropSelf(DECAYED_STONE_BRICKS);
        dropSelf(DECAYED_STONE_SLAB);
        dropSelf(DECAYED_STONE_STAIRS);
        dropSelf(DECAYED_STONE_WALL);
        dropSelf(PARCHED_SOIL);
        dropPottedContents(POTTED_SOULIFIED_BUSH);
        dropSelf(REFINED_SOUL_METAL_BLOCK);
        dropSelf(REFINED_SOUL_SAND);
        dropSelf(REFINED_SOUL_SOIL);
        dropSelf(SMOOTH_SOUL_STONE);
        dropSelf(SMOOTH_SOUL_STONE_SLAB);
        dropSelf(SMOOTH_SOUL_STONE_STAIRS);
        dropSelf(SMOOTH_SOUL_STONE_WALL);
        dropSelf(SOUL_METAL_BLOCK);
        dropWhenSilkTouch(MAGNETIC_FIELD_GENERATOR);
        dropSelf(REFINED_SOUL_METAL_BARS);
        dropOther(CHIPPED_REFINED_SOUL_METAL_BARS, REFINED_SOUL_METAL_BARS);
        dropOther(DAMAGED_REFINED_SOUL_METAL_BARS, REFINED_SOUL_METAL_BARS);
        dropOther(BROKEN_REFINED_SOUL_METAL_BARS, REFINED_SOUL_METAL_BARS);
        dropSelf(SOUL_METAL_BARS);
        dropOther(CHIPPED_SOUL_METAL_BARS, SOUL_METAL_BARS);
        dropOther(DAMAGED_SOUL_METAL_BARS, SOUL_METAL_BARS);
        dropSelf(SOUL_OBSIDIAN);
        dropSelf(SOUL_REINFORCEMENT_TABLE);
        dropSelf(SOUL_STONE);
        dropSelf(SOUL_STONE_BRICK_SLAB);
        dropSelf(SOUL_STONE_BRICK_STAIRS);
        dropSelf(SOUL_STONE_BRICK_WALL);
        dropSelf(SOUL_STONE_BRICKS);
        dropSelf(SOUL_STONE_SLAB);
        dropSelf(SOUL_STONE_STAIRS);
        dropSelf(SOUL_STONE_WALL);
        add(SOUL_WART, block -> LootTable.lootTable().withPool(
                applyExplosionDecay(block, LootPool.lootPool()
                        .setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(ModItems.SOUL_WART)
                                .apply(SetCount.setCount(RandomValueRange.between(2, 4))
                                        .when(BlockStateProperty.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(NetherWartBlock.AGE, 3))))
                                .apply(ApplyBonus.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)
                                        .when(BlockStateProperty.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(NetherWartBlock.AGE, 3))))))));
        add(SOULIFIED_BUSH, block -> createShearsDispatchTable(block, applyExplosionDecay(block, ItemLootEntry.lootTableItem(Items.STICK).apply(SetCount.setCount(RandomValueRange.between(0, 2))))));
        otherWhenSilkTouch(WARPED_HYPHAL_SOIL, SOUL_SOIL);
        otherWhenSilkTouch(CRIMSON_HYPHAL_SOIL, SOUL_SOIL);

        for (BlockGroup<?> group : BlockGroup.getBlockGroups()) {
            group.fillBlockLootTables(this);
        }
    }

    @Override
    public Set<Block> getKnownBlocks() {
        return knownBlocks;
    }
}
