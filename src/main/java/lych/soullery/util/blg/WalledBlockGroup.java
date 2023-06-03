package lych.soullery.util.blg;

import com.google.common.collect.ImmutableList;
import lych.soullery.data.BlockModelDataGen;
import lych.soullery.data.RecipeDataGen;
import lych.soullery.util.Utils;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.TagsProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static lych.soullery.Soullery.prefix;

public abstract class WalledBlockGroup<T extends WalledBlockItemGroup> extends BlockGroup<T> {
    private WallBlock wall;

    public WalledBlockGroup(Block core, Item.Properties blockItemProperties, Function<? super Block, ? extends ResourceLocation> prefixer, boolean autoRegister, String... registryNames) {
        super(core, blockItemProperties, prefixer, autoRegister, registryNames);
    }

    protected abstract boolean isStone();

    public WallBlock wall() {
        return wall;
    }

    @Override
    protected List<? extends Block> forRegistration() {
        this.wall = new WallBlock(Properties.copy(core()));
        return ImmutableList.<Block>builder().addAll(super.forRegistration()).add(wall).build();
    }

    @Override
    public void fillBlockStates(BlockStateProvider provider) {
        super.fillBlockStates(provider);
        provider.wallBlock(wall, modelPrefixer.apply(core()));
    }

    @Override
    public void fillBlockModels(BlockModelProvider provider, Function<? super Block, ? extends String> registryNameExtractor) {
        super.fillBlockModels(provider, registryNameExtractor);
        provider.wallInventory(BlockModelDataGen.wallInventoryToString(wall, registryNameExtractor), modelPrefixer.apply(core()));
    }

    @Override
    public void fillBlockTags(Function<? super ITag.INamedTag<Block>, ? extends TagsProvider.Builder<Block>> tagger) {
        super.fillBlockTags(tagger);
        tagger.apply(BlockTags.WALLS).add(wall);
        if (isStone()) {
            tagger.apply(Tags.Blocks.STONE).add(core());
        }
    }

    @Override
    public boolean fillBlockLootTables(BlockLootTables lt) {
        boolean filled = super.fillBlockLootTables(lt);
        if (filled) {
            lt.dropSelf(wall);
        }
        return filled;
    }

    @Override
    public void fillRecipes(Consumer<IFinishedRecipe> consumer) {
        super.fillRecipes(consumer);
        link(RecipeDataGen::makeWall, wall, core(), consumer);

        if (isStone()) {
            stonecutting(slab(), core(), 2, consumer);
            stonecutting(stairs(), core(), 1, consumer);
            stonecutting(wall, core(), 1, consumer);
        }
    }

    private void stonecutting(Block result, Block ingredient, int count, Consumer<IFinishedRecipe> consumer) {
        RecipeDataGen.stonecutting(result, ingredient, count).unlocks(RecipeDataGen.stHas(blockNames.get(ingredient)), RecipeDataGen.has(ingredient)).save(consumer, prefix(Utils.getRegistryName(result).getPath() + "_from_" + Utils.getRegistryName(ingredient).getPath() + "_stonecutting"));
    }
}
