package lych.soullery.data.loot;

import lych.soullery.entity.ModEntities;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootContext.EntityTarget;
import net.minecraft.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

import static lych.soullery.item.ModItems.*;
import static net.minecraft.item.Items.*;
import static net.minecraft.loot.ConstantRange.exactly;
import static net.minecraft.loot.ItemLootEntry.lootTableItem;
import static net.minecraft.loot.LootPool.lootPool;
import static net.minecraft.loot.LootTable.lootTable;
import static net.minecraft.loot.RandomValueRange.between;
import static net.minecraft.loot.conditions.EntityHasProperty.hasProperties;
import static net.minecraft.loot.conditions.KilledByPlayer.killedByPlayer;
import static net.minecraft.loot.conditions.RandomChanceWithLooting.randomChanceAndLootingBoost;
import static net.minecraft.loot.functions.LootingEnchantBonus.lootingMultiplier;
import static net.minecraft.loot.functions.SetCount.setCount;
import static net.minecraft.loot.functions.Smelt.smelted;

public class ModEntityLootTables extends EntityLootTables {
    private final Set<EntityType<?>> knownEntities = new HashSet<>();

    @Override
    protected void add(EntityType<?> type, LootTable.Builder builder) {
        knownEntities.add(type);
        super.add(type, builder);
    }

    @Override
    protected void addTables() {
        add(ModEntities.SOUL_SKELETON, lootTable()
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(SOUL_METAL_PARTICLE)
                                .apply(setCount(between(0, 6)))
                                .apply(lootingMultiplier(between(0, 2)))))
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(BONE)
                                .apply(setCount(between(0, 1)))
                                .apply(lootingMultiplier(between(0, 1)))))
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(SOUL_POWDER)
                                .apply(setCount(between(0, 1)))
                                .apply(lootingMultiplier(between(0, 1)))))
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(SOUL_METAL_INGOT))
                        .when(killedByPlayer())
                        .when(randomChanceAndLootingBoost(0.05f, 0.02f))));
        add(ModEntities.WANDERER, lootTable()
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(REFINED_SOUL_METAL_NUGGET)
                                .apply(setCount(between(0, 1)))
                                .apply(lootingMultiplier(between(0, 1)))))
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(REFINED_SOUL_METAL_INGOT))
                        .when(killedByPlayer())
                        .when(randomChanceAndLootingBoost(0.02f, 0.01f))));
        add(ModEntities.SOUL_RABBIT, lootTable()
                .withPool(lootPool()
                        .setRolls(exactly(1))
                        .add(lootTableItem(RABBIT_HIDE)
                                .apply(setCount(between(0, 1)))
                                .apply(lootingMultiplier(between(0, 1)))))
                .withPool(lootPool().setRolls(exactly(1))
                        .add(lootTableItem(RABBIT)
                                .apply(setCount(between(0, 1)))
                                .apply(smelted()
                                        .when(hasProperties(EntityTarget.THIS, ENTITY_ON_FIRE)))
                                .apply(lootingMultiplier(between(0, 1)))))
                .withPool(lootPool().setRolls(exactly(1))
                        .add(lootTableItem(SOUL_POWDER))
                        .when(randomChanceAndLootingBoost(0.2f, 0.1f)))
                .withPool(lootPool().setRolls(exactly(1))
                        .add(lootTableItem(RABBIT_FOOT))
                        .when(killedByPlayer())
                        .when(randomChanceAndLootingBoost(0.05f, 0.02f))));
    }

    @Override
    protected Iterable<EntityType<?>> getKnownEntities() {
        return knownEntities;
    }
}
