package lych.soullery.data.loot;

import lych.soullery.Soullery;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;

public class ModChestLootTables extends ChestLootTables {
    private static ResourceLocation prefix(String name) {
        return Soullery.prefix("chests/" + name);
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {}
}
