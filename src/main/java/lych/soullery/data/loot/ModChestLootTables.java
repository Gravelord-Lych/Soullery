package lych.soullery.data.loot;

import lych.soullery.Soullery;
import lych.soullery.item.ModItems;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;

import static net.minecraft.loot.ItemLootEntry.lootTableItem;
import static net.minecraft.loot.LootPool.lootPool;
import static net.minecraft.loot.LootTable.lootTable;
import static net.minecraft.loot.RandomValueRange.between;
import static net.minecraft.loot.functions.SetCount.setCount;

public class ModChestLootTables extends ChestLootTables {
    public static final ResourceLocation SKY_CITY = prefixChest("nether_bridge");

    private static ResourceLocation prefixChest(String name) {
        return Soullery.prefix("chests/" + name);
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        consumer.accept(SKY_CITY, lootTable()
                .withPool(lootPool()
                        .setRolls(between(3, 6))
                        .add(lootTableItem(ModItems.REFINED_SOUL_METAL_INGOT)
                                .setWeight(5)
                                .apply(setCount(between(1, 3))))
                        .add(lootTableItem(Items.DIAMOND)
                                .setWeight(3)
                                .apply(setCount(between(1, 2))))
                        .add(lootTableItem(ModItems.SOUL_METAL_INGOT)
                                .setWeight(15)
                                .apply(setCount(between(1, 6))))
                        .add(lootTableItem(ModItems.REFINED_SOUL_METAL_SWORD)
                                .setWeight(5))
                        .add(lootTableItem(ModItems.REFINED_SOUL_METAL_CHESTPLATE)
                                .setWeight(5))
                        .add(lootTableItem(ModItems.SOUL_WART)
                                .setWeight(5)
                                .apply(setCount(between(2, 6))))
                        .add(lootTableItem(Items.SADDLE)
                                .setWeight(5))
                        .add(lootTableItem(ModItems.REFINED_SOUL_METAL_HORSE_ARMOR)
                                .setWeight(10))
                        .add(lootTableItem(Items.DIAMOND_HORSE_ARMOR)
                                .setWeight(3))
                        .add(lootTableItem(ModItems.SOUL_OBSIDIAN)
                                .setWeight(2)
                                .apply(setCount(between(2, 4))))));
    }
}
