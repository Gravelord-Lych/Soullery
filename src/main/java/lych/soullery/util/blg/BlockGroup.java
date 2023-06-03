package lych.soullery.util.blg;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lych.soullery.Soullery;
import lych.soullery.data.BlockModelDataGen;
import lych.soullery.data.ModDataGens;
import lych.soullery.data.RecipeDataGen;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.TagsProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTables;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Block groups can register a lot of blocks in a very short time
 */
public abstract class BlockGroup<T extends BlockItemGroup> {
    private static BlockGroup<?>[] blockGroups = new BlockGroup<?>[0];
    protected final Function<? super Block, ? extends ResourceLocation> modelPrefixer;
    protected final Map<Block, String> blockNames;
    private final Block core;
    private final SlabBlock slab;
    private final StairsBlock stairs;
    private final T items;

    public BlockGroup(Block core, Item.Properties blockItemProperties, Function<? super Block, ? extends ResourceLocation> modelPrefixer, boolean autoRegister, String... blockNames) {
        this.core = core;
        this.slab = new SlabBlock(Properties.copy(core));
        this.stairs = new StairsBlock(core::defaultBlockState, Properties.copy(core));
        this.modelPrefixer = modelPrefixer;
        List<? extends Block> blocks = forRegistration();
        checkNames(blocks, blockNames);

        ImmutableMap.Builder<Block, String> builder = ImmutableMap.builder();
        for (int i = 0; i < blocks.size(); i++) {
            builder.put(blocks.get(i), blockNames[i]);
        }
        this.blockNames = builder.build();
        this.items = initItems(blockItemProperties);

        if (autoRegister) {
            blockGroups = Arrays.copyOf(blockGroups, blockGroups.length + 1);
            blockGroups[blockGroups.length - 1] = this;
        }
    }

    public static List<BlockGroup<?>> getBlockGroups() {
        return ImmutableList.copyOf(blockGroups);
    }

    private static void checkNames(List<? extends Block> blocks, String[] names) {
        if (names.length != blocks.size()) {
            int d = names.length - blocks.size();
            if (d < 0) {
                throw new IllegalArgumentException(String.format("Missing %d registry names! (Expected: %d, Provided: %d)", -d, blocks.size(), names.length));
            } else {
                throw new IllegalArgumentException(String.format("Found %d more registry names! (Expected: %d, Provided: %d)", d, blocks.size(), names.length));
            }
        }
    }

    protected abstract T initItems(Item.Properties properties);

    public Block core() {
        return core;
    }

    public SlabBlock slab() {
        return slab;
    }

    public StairsBlock stairs() {
        return stairs;
    }

    public T blockItems() {
        return items;
    }

    protected List<? extends Block> forRegistration() {
        return ImmutableList.of(core, slab, stairs);
    }

    public void fillBlockStates(BlockStateProvider provider) {
        provider.simpleBlock(core);
        provider.slabBlock(slab, modelPrefixer.apply(core), modelPrefixer.apply(core));
        provider.stairsBlock(stairs, modelPrefixer.apply(core));
    }

    public final void fillBlockModels(BlockModelProvider provider) {
        fillBlockModels(provider, ModDataGens::registryNameToString);
    }

    public void fillBlockModels(BlockModelProvider provider, Function<? super Block, ? extends String> registryNameExtractor) {
        BlockModelDataGen.cubeAll(provider, core, registryNameExtractor);
    }

    public void fillBlockTags(Function<? super ITag.INamedTag<Block>, ? extends TagsProvider.Builder<Block>> tagger) {}

    public boolean fillBlockLootTables(BlockLootTables lt) {
        if (core.getLootTable() != LootTables.EMPTY) {
            lt.dropSelf(core);
            lt.dropSelf(slab);
            lt.dropSelf(stairs);
            return true;
        }
        return false;
    }

    public void fillRecipes(Consumer<IFinishedRecipe> consumer) {
        link(RecipeDataGen::makeSlab, slab, core, consumer);
        link(RecipeDataGen::makeStairs, stairs, core, consumer);
    }

    protected void link(BiFunction<? super IItemProvider, ? super IItemProvider, ? extends ShapedRecipeBuilder> recipeCreator, Block result, Block ingredient, Consumer<IFinishedRecipe> consumer) {
        recipeCreator.apply(result, ingredient).unlockedBy(RecipeDataGen.stHas(blockNames.get(result)), RecipeDataGen.has(result)).save(consumer);
    }

    public final <V extends ForgeRegistryEntry<V>> RegistryHelper<V> setRegistry(IForgeRegistry<V> registry) {
        return setRegistry(registry, Soullery::make);
    }

    public <V extends ForgeRegistryEntry<V>> RegistryHelper<V> setRegistry(IForgeRegistry<V> registry, BiFunction<? super V, ? super String, ? extends V> makerFunction) {
        return new RegistryHelper<>(registry, makerFunction);
    }

    @SuppressWarnings("unchecked")
    public final class RegistryHelper<V extends ForgeRegistryEntry<V>> {
        private final IForgeRegistry<V> registry;
        private final BiFunction<? super V, ? super String, ? extends V> makerFunction;

        private RegistryHelper(IForgeRegistry<V> registry, BiFunction<? super V, ? super String, ? extends V> makerFunction) {
            this.registry = registry;
            this.makerFunction = makerFunction;
        }

        public void registerAllBlocks() {
            try {
                for (Map.Entry<Block, String> entry : blockNames.entrySet()) {
                    registry.register(make((V) entry.getKey(), entry.getValue()));
                }
            } catch (ClassCastException e) {
                throw new IllegalStateException("Cannot register blocks with an invalid registry", e);
            }
        }

        public void registerAllBlockItems() {
            try {
                for (BlockItem item : items.forRegistration()) {
                    registry.register(make((V) item, blockNames.get(item.getBlock())));
                }
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Cannot register block items with an invalid registry", e);
            }
        }

        private V make(V entry, String name) {
            return makerFunction.apply(entry, name);
        }
    }
}
