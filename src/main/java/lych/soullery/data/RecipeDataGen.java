package lych.soullery.data;

import com.google.gson.JsonObject;
import lych.soullery.Soullery;
import lych.soullery.block.ModBlockNames;
import lych.soullery.item.ModItemNames;
import lych.soullery.item.SoulContainerItem;
import lych.soullery.item.crafting.ModRecipeSerializers;
import lych.soullery.util.blg.BlockGroup;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.data.ForgeRecipeProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static lych.soullery.Soullery.prefix;
import static lych.soullery.block.ModBlocks.GLOWSTONE_BRICKS;
import static lych.soullery.item.ModItems.*;
import static net.minecraft.data.ShapedRecipeBuilder.shaped;
import static net.minecraft.data.ShapelessRecipeBuilder.shapeless;
import static net.minecraft.item.Items.*;

public class RecipeDataGen extends ForgeRecipeProvider {
    public RecipeDataGen(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
        makeSword(REFINED_SOUL_METAL_AXE, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeBoots(REFINED_SOUL_METAL_BOOTS, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeChestplate(REFINED_SOUL_METAL_CHESTPLATE, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeHelmet(REFINED_SOUL_METAL_HELMET, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeHoe(REFINED_SOUL_METAL_HOE, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeLeggings(REFINED_SOUL_METAL_LEGGINGS, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makePickaxe(REFINED_SOUL_METAL_PICKAXE, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeShovel(REFINED_SOUL_METAL_SHOVEL, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        makeSword(REFINED_SOUL_METAL_SWORD, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        create9(REFINED_SOUL_METAL_BLOCK, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        create9(REFINED_SOUL_METAL_INGOT, REFINED_SOUL_METAL_NUGGET).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_NUGGET), has(REFINED_SOUL_METAL_NUGGET)).save(consumer, prefix(ModItemNames.REFINED_SOUL_METAL_INGOT + "_from_nuggets"));
        to9(REFINED_SOUL_METAL_INGOT, REFINED_SOUL_METAL_BLOCK).unlockedBy(stHas(ModBlockNames.REFINED_SOUL_METAL_BLOCK), has(REFINED_SOUL_METAL_BLOCK)).save(consumer);
        to9(REFINED_SOUL_METAL_NUGGET, REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(SOUL_METAL_INGOT)).save(consumer);
        shapeless(REFINED_SOUL_METAL_INGOT).requires(Tags.Items.GEMS_DIAMOND).requires(SOUL_METAL_INGOT).requires(SOUL_BLAZE_POWDER).unlockedBy(stHas(ModItemNames.SOUL_METAL_INGOT), has(SOUL_METAL_INGOT)).save(consumer, prefix(ModItemNames.REFINED_SOUL_METAL_INGOT + "_from_refining"));
        shapeless(SOUL_BLAZE_POWDER).requires(BLAZE_POWDER).requires(SOUL_POWDER).unlockedBy(stHasTwo(Objects.requireNonNull(BLAZE_POWDER.getRegistryName()).getPath(), ModItemNames.SOUL_POWDER), hasAll(BLAZE_POWDER, SOUL_POWDER)).save(consumer);
        shapeless(SOUL_BLAZE_POWDER, 2).requires(SOUL_BLAZE_ROD).unlockedBy(stHas(ModItemNames.SOUL_BLAZE_ROD), has(SOUL_BLAZE_ROD)).save(consumer, prefix(ModItemNames.SOUL_BLAZE_POWDER + "_from_rod"));
        shapeless(SOUL_BLAZE_ROD).requires(Tags.Items.RODS_BLAZE).requires(SOUL_POWDER).unlockedBy(stHasTwo(Objects.requireNonNull(BLAZE_ROD.getRegistryName()).getPath(), ModItemNames.SOUL_POWDER), hasAll(BLAZE_ROD, SOUL_POWDER)).save(consumer);
        shaped(SOUL_ENERGY_GEM).pattern(" X ").pattern("X#X").pattern(" X ").define('X', SOUL_POWDER).define('#', Tags.Items.GEMS_EMERALD).unlockedBy(stHas(ModItemNames.SOUL_POWDER), has(SOUL_POWDER)).save(consumer);
        create9(SOUL_METAL_BLOCK, SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.SOUL_METAL_INGOT), has(SOUL_METAL_INGOT)).save(consumer);
        create9(SOUL_METAL_INGOT, SOUL_METAL_NUGGET).unlockedBy(stHas(ModItemNames.SOUL_METAL_NUGGET), has(SOUL_METAL_NUGGET)).save(consumer, prefix(ModItemNames.SOUL_METAL_INGOT + "_from_nuggets"));
        to9(SOUL_METAL_INGOT, SOUL_METAL_BLOCK).unlockedBy(stHas(ModBlockNames.SOUL_METAL_BLOCK), has(SOUL_METAL_BLOCK)).save(consumer);
        create9(SOUL_METAL_NUGGET, SOUL_METAL_PARTICLE).unlockedBy(stHas(ModItemNames.SOUL_METAL_PARTICLE), has(SOUL_METAL_PARTICLE)).save(consumer, prefix(ModItemNames.SOUL_METAL_NUGGET + "_from_particles"));
        to9(SOUL_METAL_NUGGET, SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.SOUL_METAL_INGOT), has(SOUL_METAL_INGOT)).save(consumer);
        to9(SOUL_METAL_PARTICLE, SOUL_METAL_NUGGET).unlockedBy(stHas(ModItemNames.SOUL_METAL_NUGGET), has(SOUL_METAL_NUGGET)).save(consumer);
        buildStoneRecipes(consumer);
        buildSmeltingRecipes(consumer);
        shapeless(SOUL_POWDER, 3).requires(SOUL_PIECE).unlockedBy(stHas(ModItemNames.SOUL_PIECE), has(SOUL_PIECE)).save(consumer);
        shapeless(SOUL_POWDER, 27).requires(SOUL_CONTAINER).unlockedBy(stHas(ModItemNames.SOUL_CONTAINER), has(SOUL_CONTAINER)).save(consumer, prefix(ModItemNames.SOUL_POWDER + "_from_soul_container"));
        shapeless(SOUL_POWDER, 2).requires(SOUL_WART).unlockedBy(stHas(ModBlockNames.SOUL_WART), has(SOUL_WART)).save(consumer, prefix(ModItemNames.SOUL_POWDER + "_from_soul_warts"));
        shapeless(SOUL_STONE).requires(SOUL_POWDER).requires(STONE).unlockedBy(stHas(ModItemNames.SOUL_POWDER), has(SOUL_POWDER)).save(consumer, prefix(ModBlockNames.SOUL_STONE + "_from_stone"));
        shapeless(SOUL_STONE).requires(SOUL_POWDER).requires(COBBLESTONE).unlockedBy(stHas(ModItemNames.SOUL_POWDER), has(SOUL_POWDER)).save(consumer, prefix(ModBlockNames.SOUL_STONE + "_from_cobblestone"));
        shapeless(SOUL_METAL_INGOT).requires(SOUL_POWDER).requires(Tags.Items.INGOTS_IRON).unlockedBy(stHas(ModItemNames.SOUL_POWDER), has(SOUL_POWDER)).save(consumer, prefix(ModItemNames.SOUL_METAL_INGOT + "_from_powder"));
        specialSave(ModRecipeSerializers.SOUL_CONTAINER.get(), prefix(ModItemNames.SOUL_CONTAINER + "_creation"), consumer);
        shaped(SOUL_REINFORCEMENT_TABLE).pattern("---").pattern("#W#").pattern("###").define('-', SOUL_METAL_INGOT).define('#', SOUL_STONE).define('W', CRAFTING_TABLE).unlockedBy(stHas(ModBlockNames.SOUL_REINFORCEMENT_TABLE), has(SOUL_REINFORCEMENT_TABLE)).save(consumer);
        shaped(SOUL_METAL_BARS, 16).pattern("###").pattern("###").define('#', SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.SOUL_METAL_INGOT), has(SOUL_METAL_INGOT)).save(consumer);
        shaped(REFINED_SOUL_METAL_BARS, 16).pattern("###").pattern("###").define('#', REFINED_SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.REFINED_SOUL_METAL_INGOT), has(REFINED_SOUL_METAL_INGOT)).save(consumer);
        shaped(SEGEN).pattern("---").pattern("-#-").pattern("---").define('-', SOUL_METAL_INGOT).define('#', SOUL_STONE).unlockedBy(stHasTwo(ModItemNames.SOUL_METAL_INGOT, ModBlockNames.SOUL_STONE), hasAll(SOUL_METAL_INGOT, SOUL_STONE)).save(consumer);
        shaped(EXTRA_ABILITY_CARRIER).pattern("###").pattern("# #").pattern("###").define('#', SOUL_METAL_INGOT).unlockedBy(stHas(ModItemNames.SOUL_METAL_INGOT), has(SOUL_METAL_INGOT)).save(consumer);
        shaped(MIND_OPERATOR).pattern(" IS").pattern(" II").pattern("I  ").define('I', GOLD_INGOT).define('S', SOUL_CONTAINER).unlockedBy(stHas(ModItemNames.SOUL_CONTAINER), has(SOUL_CONTAINER)).save(consumer);
        shaped(CHAOS_WAND).pattern(" ES").pattern(" IE").pattern("I  ").define('I', SOUL_BLAZE_ROD).define('E', ENDER_PEARL).define('S', SOUL_CONTAINER).unlockedBy(stHas(ModItemNames.SOUL_CONTAINER), has(SOUL_CONTAINER)).save(consumer);
        shaped(SOUL_PURIFIER).pattern("  S").pattern(" I ").pattern("I  ").define('I', SOUL_BLAZE_ROD).define('S', SOUL_CONTAINER).unlockedBy(stHas(ModItemNames.SOUL_CONTAINER), has(SOUL_CONTAINER)).save(consumer);
        shaped(HORCRUX_CARRIER).pattern("#X#").pattern("XOX").pattern("#X#").define('#', REFINED_SOUL_METAL_INGOT).define('X', SOUL_METAL_INGOT).define('O', SOUL_CONTAINER).unlockedBy(stHasTwo(ModItemNames.REFINED_SOUL_METAL_INGOT, ModItemNames.SOUL_CONTAINER), hasAll(REFINED_SOUL_METAL_INGOT, SOUL_CONTAINER)).save(consumer);
        shaped(HORCRUX_CARRIER).pattern("X#X").pattern("#O#").pattern("X#X").define('#', REFINED_SOUL_METAL_INGOT).define('X', SOUL_METAL_INGOT).define('O', SOUL_CONTAINER).unlockedBy(stHasTwo(ModItemNames.REFINED_SOUL_METAL_INGOT, ModItemNames.SOUL_CONTAINER), hasAll(REFINED_SOUL_METAL_INGOT, SOUL_CONTAINER)).save(consumer, prefix(ModItemNames.HORCRUX_CARRIER + "_alt"));
        shaped(GLOWSTONE_BRICKS.blockItems().core(), 2).pattern("###").pattern("#S#").pattern("###").define('#', Tags.Items.DUSTS_GLOWSTONE).define('S', SOUL_POWDER).unlockedBy(stHasTwo(GLOWSTONE_DUST.getRegistryName().getPath(), ModItemNames.SOUL_POWDER), hasAll(GLOWSTONE_DUST, SOUL_POWDER)).save(consumer);
        shapeless(GLOWSTONE_BRICKS.blockItems().core(), 2).requires(GLOWSTONE, 2).requires(SOUL_POWDER).unlockedBy(stHasTwo(GLOWSTONE.getRegistryName().getPath(), ModItemNames.SOUL_POWDER), hasAll(GLOWSTONE, SOUL_POWDER)).save(consumer, prefix(ModBlockNames.GLOWSTONE_BRICKS + "_from_glowstones"));

        for (BlockGroup<?> group : BlockGroup.getBlockGroups()) {
            group.fillRecipes(consumer);
        }
    }

    protected void specialSave(SpecialRecipeSerializer<?> serializer, ResourceLocation location, Consumer<IFinishedRecipe> consumer) {
        consumer.accept(new IFinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject object) {}

            @Override
            public ResourceLocation getId() {
                return location;
            }

            @Override
            public IRecipeSerializer<?> getType() {
                return serializer;
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return new ResourceLocation(location.getNamespace(), "");
            }
        });
    }

    protected Ingredient soulOf(EntityType<?> type) {
        ItemStack stack = new ItemStack(SOUL_CONTAINER);
        SoulContainerItem.setType(stack, type);
        return new NBTIngredient(stack){};
    }

    private void buildStoneRecipes(Consumer<IFinishedRecipe> consumer) {
        decayedStone(consumer);
        soulStone(consumer);
    }

    private void decayedStone(Consumer<IFinishedRecipe> consumer) {
        makeDecayedStoneRelated(makeSlab(CRACKED_DECAYED_STONE_BRICK_SLAB, CRACKED_DECAYED_STONE_BRICKS), ModBlockNames.CRACKED_DECAYED_STONE_BRICKS, CRACKED_DECAYED_STONE_BRICKS, CRACKED_DECAYED_STONE_BRICK_SLAB, consumer);
        makeDecayedStoneRelated(makeStairs(CRACKED_DECAYED_STONE_BRICK_STAIRS, CRACKED_DECAYED_STONE_BRICKS), ModBlockNames.CRACKED_DECAYED_STONE_BRICKS, CRACKED_DECAYED_STONE_BRICKS, CRACKED_DECAYED_STONE_BRICK_STAIRS, consumer);
        makeDecayedStoneRelated(makeWall(CRACKED_DECAYED_STONE_BRICK_WALL, CRACKED_DECAYED_STONE_BRICKS), ModBlockNames.CRACKED_DECAYED_STONE_BRICKS, CRACKED_DECAYED_STONE_BRICKS, CRACKED_DECAYED_STONE_BRICK_WALL, consumer);
        makeDecayedStoneRelated(makeSlab(DECAYED_STONE_BRICK_SLAB, DECAYED_STONE_BRICKS), ModBlockNames.DECAYED_STONE_BRICKS, DECAYED_STONE_BRICKS, DECAYED_STONE_BRICK_SLAB, consumer);
        makeDecayedStoneRelated(makeStairs(DECAYED_STONE_BRICK_STAIRS, DECAYED_STONE_BRICKS), ModBlockNames.DECAYED_STONE_BRICKS, DECAYED_STONE_BRICKS, DECAYED_STONE_BRICK_STAIRS, consumer);
        makeDecayedStoneRelated(makeWall(DECAYED_STONE_BRICK_WALL, DECAYED_STONE_BRICKS), ModBlockNames.DECAYED_STONE_BRICKS, DECAYED_STONE_BRICKS, DECAYED_STONE_BRICK_WALL, consumer);
        makeDecayedStoneRelated(makeBricks(DECAYED_STONE_BRICKS, DECAYED_STONE), ModBlockNames.DECAYED_STONE, DECAYED_STONE, DECAYED_STONE_BRICKS, consumer);
        makeDecayedStoneRelated(makeSlab(DECAYED_STONE_SLAB, DECAYED_STONE), ModBlockNames.DECAYED_STONE, DECAYED_STONE, DECAYED_STONE_SLAB, consumer);
        makeDecayedStoneRelated(makeStairs(DECAYED_STONE_STAIRS, DECAYED_STONE), ModBlockNames.DECAYED_STONE, DECAYED_STONE, DECAYED_STONE_STAIRS, consumer);
        makeDecayedStoneRelated(makeWall(DECAYED_STONE_WALL, DECAYED_STONE), ModBlockNames.DECAYED_STONE, DECAYED_STONE, DECAYED_STONE_WALL, consumer);
    }

    private void soulStone(Consumer<IFinishedRecipe> consumer) {
        makeSoulStoneRelated(makeChiseled(CHISELED_SOUL_STONE_BRICKS, SOUL_STONE_SLAB), ModBlockNames.SOUL_STONE_SLAB, SOUL_STONE_SLAB, CHISELED_SOUL_STONE_BRICKS, consumer);
        makeSoulStoneRelated(makeSlab(CRACKED_SOUL_STONE_BRICK_SLAB, CRACKED_SOUL_STONE_BRICKS), ModBlockNames.CRACKED_SOUL_STONE_BRICKS, CRACKED_SOUL_STONE_BRICKS, CRACKED_SOUL_STONE_BRICK_SLAB, consumer);
        makeSoulStoneRelated(makeStairs(CRACKED_SOUL_STONE_BRICK_STAIRS, CRACKED_SOUL_STONE_BRICKS), ModBlockNames.CRACKED_SOUL_STONE_BRICKS, CRACKED_SOUL_STONE_BRICKS, CRACKED_SOUL_STONE_BRICK_STAIRS, consumer);
        makeSoulStoneRelated(makeWall(CRACKED_SOUL_STONE_BRICK_WALL, CRACKED_SOUL_STONE_BRICKS), ModBlockNames.CRACKED_SOUL_STONE_BRICKS, CRACKED_SOUL_STONE_BRICKS, CRACKED_SOUL_STONE_BRICK_WALL, consumer);
        makeSoulStoneRelated(makeSlab(SMOOTH_SOUL_STONE_SLAB, SMOOTH_SOUL_STONE), ModBlockNames.SMOOTH_SOUL_STONE, SMOOTH_SOUL_STONE, SMOOTH_SOUL_STONE_SLAB, consumer);
        makeSoulStoneRelated(makeStairs(SMOOTH_SOUL_STONE_STAIRS, SMOOTH_SOUL_STONE), ModBlockNames.SMOOTH_SOUL_STONE, SMOOTH_SOUL_STONE, SMOOTH_SOUL_STONE_STAIRS, consumer);
        makeSoulStoneRelated(makeWall(SMOOTH_SOUL_STONE_WALL, SMOOTH_SOUL_STONE), ModBlockNames.SMOOTH_SOUL_STONE, SMOOTH_SOUL_STONE, SMOOTH_SOUL_STONE_WALL, consumer);
        makeSoulStoneRelated(makeSlab(SOUL_STONE_BRICK_SLAB, SOUL_STONE_BRICKS), ModBlockNames.SOUL_STONE_BRICKS, SOUL_STONE_BRICKS, SOUL_STONE_BRICK_SLAB, consumer);
        makeSoulStoneRelated(makeStairs(SOUL_STONE_BRICK_STAIRS, SOUL_STONE_BRICKS), ModBlockNames.SOUL_STONE_BRICKS, SOUL_STONE_BRICKS, SOUL_STONE_BRICK_STAIRS, consumer);
        makeSoulStoneRelated(makeWall(SOUL_STONE_BRICK_WALL, SOUL_STONE_BRICKS), ModBlockNames.SOUL_STONE_BRICKS, SOUL_STONE_BRICKS, SOUL_STONE_BRICK_WALL, consumer);
        makeSoulStoneRelated(makeBricks(SOUL_STONE_BRICKS, SOUL_STONE), ModBlockNames.SOUL_STONE, SOUL_STONE, SOUL_STONE_BRICKS, consumer);
        makeSoulStoneRelated(makeSlab(SOUL_STONE_SLAB, SOUL_STONE), ModBlockNames.SOUL_STONE, SOUL_STONE, SOUL_STONE_SLAB, consumer);
        makeSoulStoneRelated(makeStairs(SOUL_STONE_STAIRS, SOUL_STONE), ModBlockNames.SOUL_STONE, SOUL_STONE, SOUL_STONE_STAIRS, consumer);
        makeSoulStoneRelated(makeWall(SOUL_STONE_WALL, SOUL_STONE), ModBlockNames.SOUL_STONE, SOUL_STONE, SOUL_STONE_WALL, consumer);
    }

    private void makeSoulStoneRelated(ShapedRecipeBuilder builder, String requiredItemName, BlockItem requiredItem, BlockItem resultItem, Consumer<IFinishedRecipe> consumer) {
        builder.unlockedBy(stHas(requiredItemName), has(requiredItem)).save(consumer);
        soulStonecutting(resultItem, consumer);
    }

    private void makeDecayedStoneRelated(ShapedRecipeBuilder builder, String requiredItemName, BlockItem requiredItem, BlockItem resultItem, Consumer<IFinishedRecipe> consumer) {
        builder.unlockedBy(stHas(requiredItemName), has(requiredItem)).save(consumer);
        decayedStonecutting(resultItem, consumer);
    }

    private void buildSmeltingRecipes(Consumer<IFinishedRecipe> consumer) {
        commonSmelting(CRACKED_DECAYED_STONE_BRICK_SLAB, DECAYED_STONE_BRICK_SLAB, 0.1f, stHas(ModBlockNames.DECAYED_STONE_BRICK_SLAB), has(DECAYED_STONE_BRICK_SLAB), consumer, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_SLAB + "_from_smelting");
        commonSmelting(CRACKED_DECAYED_STONE_BRICK_STAIRS, DECAYED_STONE_BRICK_STAIRS, 0.1f, stHas(ModBlockNames.DECAYED_STONE_BRICK_STAIRS), has(DECAYED_STONE_BRICK_STAIRS), consumer, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_STAIRS + "_from_smelting");
        commonSmelting(CRACKED_DECAYED_STONE_BRICK_WALL, DECAYED_STONE_BRICK_WALL, 0.1f, stHas(ModBlockNames.DECAYED_STONE_BRICK_WALL), has(DECAYED_STONE_BRICK_WALL), consumer, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_WALL + "_from_smelting");
        commonSmelting(CRACKED_DECAYED_STONE_BRICKS, DECAYED_STONE_BRICKS, 0.1f, stHas(ModBlockNames.DECAYED_STONE_BRICKS), has(DECAYED_STONE_BRICKS), consumer);
        soulSmelting(SMOOTH_SOUL_STONE, SOUL_STONE, 0.1f, stHas(ModBlockNames.SOUL_STONE), has(SOUL_STONE), consumer);
        soulSmelting(CRACKED_SOUL_STONE_BRICKS, SOUL_STONE_BRICKS, 0.1f, stHas(ModBlockNames.SOUL_STONE_BRICKS), has(SOUL_STONE_BRICKS), consumer);
        soulSmelting(CRACKED_SOUL_STONE_BRICK_SLAB, SOUL_STONE_BRICK_SLAB, 0.1f, stHas(ModBlockNames.SOUL_STONE_BRICK_SLAB), has(SOUL_STONE_BRICK_SLAB), consumer, ModBlockNames.CRACKED_SOUL_STONE_BRICK_SLAB + "_from_smelting");
        soulSmelting(CRACKED_SOUL_STONE_BRICK_STAIRS, SOUL_STONE_BRICK_STAIRS, 0.1f, stHas(ModBlockNames.SOUL_STONE_BRICK_STAIRS), has(SOUL_STONE_BRICK_STAIRS), consumer, ModBlockNames.CRACKED_SOUL_STONE_BRICK_STAIRS + "_from_smelting");
        soulSmelting(CRACKED_SOUL_STONE_BRICK_WALL, SOUL_STONE_BRICK_WALL, 0.1f, stHas(ModBlockNames.SOUL_STONE_BRICK_WALL), has(SOUL_STONE_BRICK_WALL), consumer, ModBlockNames.CRACKED_SOUL_STONE_BRICK_WALL + "_from_smelting");
    }

    public static String stHas(String name) {
        return "has_" + name;
    }

    public static InventoryChangeTrigger.Instance has(IItemProvider provider) {
        return RecipeProvider.has(provider);
    }

    public static InventoryChangeTrigger.Instance has(ITag<Item> tag) {
        return RecipeProvider.has(tag);
    }

    public static String stHasTwo(String name1, String name2) {
        return "has_" + name1 + "_and_" + name2;
    }

    public static ShapedRecipeBuilder create9(IItemProvider result, IItemProvider ingredient) {
        return shaped(result).pattern("###").pattern("###").pattern("###").define('#', ingredient);
    }

    public static ShapelessRecipeBuilder to9(IItemProvider result, IItemProvider ingredient) {
        return shapeless(result, 9).requires(ingredient);
    }

    public static ShapedRecipeBuilder makeBricks(IItemProvider result, IItemProvider ingredient) {
        return shaped(result, 4).pattern("##").pattern("##").define('#', ingredient);
    }

    public static ShapedRecipeBuilder makeChiseled(IItemProvider result, IItemProvider ingredient) {
        return shaped(result).pattern("#").pattern("#").define('#', ingredient);
    }

    public static ShapedRecipeBuilder makeSlab(IItemProvider result, IItemProvider ingredient) {
        return shaped(result, 6).pattern("###").define('#', ingredient);
    }

    public static ShapedRecipeBuilder makeStairs(IItemProvider result, IItemProvider ingredient) {
        return shaped(result, 4).pattern("#  ").pattern("## ").pattern("###").define('#', ingredient);
    }

    public static ShapedRecipeBuilder makeWall(IItemProvider result, IItemProvider ingredient) {
        return shaped(result, 6).pattern("###").pattern("###").define('#', ingredient);
    }

    public static ShapedRecipeBuilder makeHelmet(IItemProvider result, IItemProvider ingredient) {
        return shaped(result).pattern("XXX").pattern("X X").define('X', ingredient);
    }

    public static ShapedRecipeBuilder makeChestplate(IItemProvider result, IItemProvider ingredient) {
        return shaped(result).pattern("X X").pattern("XXX").pattern("XXX").define('X', ingredient);
    }

    public static ShapedRecipeBuilder makeLeggings(IItemProvider result, IItemProvider ingredient) {
        return shaped(result).pattern("XXX").pattern("X X").pattern("X X").define('X', ingredient);
    }

    public static ShapedRecipeBuilder makeBoots(IItemProvider result, IItemProvider ingredient) {
        return shaped(result).pattern("X X").pattern("X X").define('X', ingredient);
    }

    public static ShapedRecipeBuilder makeAxe(IItemProvider result, IItemProvider material) {
        return makeAxe(result, material, STICK);
    }

    public static ShapedRecipeBuilder makeAxe(IItemProvider result, IItemProvider material, IItemProvider stick) {
        return shaped(result).pattern("XX").pattern("X#").pattern(" #").define('#', stick).define('X', material);
    }

    public static ShapedRecipeBuilder makeHoe(IItemProvider result, IItemProvider material) {
        return makeHoe(result, material, STICK);
    }

    public static ShapedRecipeBuilder makeHoe(IItemProvider result, IItemProvider material, IItemProvider stick) {
        return shaped(result).pattern("XX").pattern(" #").pattern(" #").define('#', stick).define('X', material);
    }

    public static ShapedRecipeBuilder makePickaxe(IItemProvider result, IItemProvider material) {
        return makePickaxe(result, material, STICK);
    }

    public static ShapedRecipeBuilder makePickaxe(IItemProvider result, IItemProvider material, IItemProvider stick) {
        return shaped(result).pattern("XXX").pattern(" # ").pattern(" # ").define('#', stick).define('X', material);
    }

    public static ShapedRecipeBuilder makeShovel(IItemProvider result, IItemProvider material) {
        return makeShovel(result, material, STICK);
    }

    public static ShapedRecipeBuilder makeShovel(IItemProvider result, IItemProvider material, IItemProvider stick) {
        return shaped(result).pattern("X").pattern("#").pattern("#").define('#', stick).define('X', material);
    }

    public static ShapedRecipeBuilder makeSword(IItemProvider result, IItemProvider material) {
        return makeSword(result, material, STICK);
    }

    public static ShapedRecipeBuilder makeSword(IItemProvider result, IItemProvider material, IItemProvider stick) {
        return shaped(result).pattern("X").pattern("X").pattern("#").define('#', stick).define('X', material);
    }

    private static void commonSmelting(IItemProvider result, IItemProvider ingredient, float experience, String condition, ICriterionInstance instance, Consumer<IFinishedRecipe> consumer) {
        smelting(result, ingredient, experience, 200).unlockedBy(condition, instance).save(consumer);
    }

    private static void commonSmelting(IItemProvider result, IItemProvider ingredient, float experience, String condition, ICriterionInstance instance, Consumer<IFinishedRecipe> consumer, String description) {
        smelting(result, ingredient, experience, 200).unlockedBy(condition, instance).save(consumer, prefix(description));
    }

    private static void soulSmelting(IItemProvider result, IItemProvider ingredient, float experience, String condition, ICriterionInstance instance, Consumer<IFinishedRecipe> consumer) {
        smelting(result, ingredient, experience, 400).unlockedBy(condition, instance).save(consumer);
    }

    private static void soulSmelting(IItemProvider result, IItemProvider ingredient, float experience, String condition, ICriterionInstance instance, Consumer<IFinishedRecipe> consumer, String description) {
        smelting(result, ingredient, experience, 400).unlockedBy(condition, instance).save(consumer, prefix(description));
    }

    public static CookingRecipeBuilder smelting(IItemProvider result, IItemProvider ingredient, float experience, int cookingTime) {
        return CookingRecipeBuilder.smelting(Ingredient.of(ingredient.asItem()), result, experience, cookingTime);
    }

    public static CookingRecipeBuilder blasting(IItemProvider result, IItemProvider ingredient, float experience, int cookingTime) {
        return CookingRecipeBuilder.blasting(Ingredient.of(ingredient.asItem()), result, experience, cookingTime);
    }

    public static CookingRecipeBuilder smoking(IItemProvider result, IItemProvider ingredient, float experience, int cookingTime) {
        return CookingRecipeBuilder.cooking(Ingredient.of(ingredient.asItem()), result, experience, cookingTime, IRecipeSerializer.SMOKING_RECIPE);
    }

    public static CookingRecipeBuilder campfire(IItemProvider result, IItemProvider ingredient, float experience, int cookingTime) {
        return CookingRecipeBuilder.cooking(Ingredient.of(ingredient.asItem()), result, experience, cookingTime, IRecipeSerializer.CAMPFIRE_COOKING_RECIPE);
    }

    public static SingleItemRecipeBuilder stonecutting(IItemProvider result, IItemProvider ingredient) {
        return stonecutting(result, ingredient, 1);
    }

    public static SingleItemRecipeBuilder stonecutting(IItemProvider result, IItemProvider ingredient, int ingredientCount) {
        return SingleItemRecipeBuilder.stonecutting(Ingredient.of(ingredient), result, ingredientCount);
    }

    private static void soulStonecutting(IItemProvider result, Consumer<IFinishedRecipe> consumer) {
        @Nullable ResourceLocation resultName = result.asItem().getRegistryName();
        @Nullable ResourceLocation ingredientName = SOUL_STONE.getRegistryName();
        if (resultName == null || ingredientName == null) {
            throw new NullPointerException("Registry name should be non-null");
        }
        stonecutting(result, SOUL_STONE, result.asItem() instanceof BlockItem && ((BlockItem) result.asItem()).getBlock() instanceof SlabBlock ? 2 : 1).unlocks("has_soul_stone", has(SOUL_STONE)).save(consumer, prefix(resultName.getPath() + "_from_" + ingredientName.getPath() + "_stonecutting"));
    }

    private static void decayedStonecutting(IItemProvider result, Consumer<IFinishedRecipe> consumer) {
        @Nullable ResourceLocation resultName = result.asItem().getRegistryName();
        @Nullable ResourceLocation ingredientName = DECAYED_STONE.getRegistryName();
        if (resultName == null || ingredientName == null) {
            throw new NullPointerException("Registry name should be non-null");
        }
        stonecutting(result, DECAYED_STONE, result.asItem() instanceof BlockItem && ((BlockItem) result.asItem()).getBlock() instanceof SlabBlock ? 2 : 1).unlocks("has_decayed_stone", has(DECAYED_STONE)).save(consumer, prefix(resultName.getPath() + "_from_" + ingredientName.getPath() + "_stonecutting"));
    }

    public static InventoryChangeTrigger.Instance hasAll(IItemProvider... providers) {
        return inventoryTrigger(Arrays.stream(providers).map(provider -> ItemPredicate.Builder.item().of(provider).build()).toArray(ItemPredicate[]::new));
    }

    @Override
    public String getName() {
        return super.getName() + ": " + Soullery.MOD_ID;
    }
}
