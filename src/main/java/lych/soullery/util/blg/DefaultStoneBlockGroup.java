package lych.soullery.util.blg;

import lych.soullery.data.BlockModelDataGen;
import lych.soullery.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public final class DefaultStoneBlockGroup extends WalledBlockGroup<WalledBlockItemGroup> {
    public DefaultStoneBlockGroup(Block core, Item.Properties blockItemProperties, Function<? super Block, ? extends ResourceLocation> prefixer, boolean autoRegister, String... registryNames) {
        super(core, blockItemProperties, prefixer, autoRegister, registryNames);
    }

    public static DefaultStoneBlockGroup createAndRegister(AbstractBlock.Properties properties, String... registryNames) {
        return createAndRegister(new Block(properties), registryNames);
    }

    public static DefaultStoneBlockGroup createAndRegister(Block core, String... registryNames) {
        return create(core, true, registryNames);
    }

    public static DefaultStoneBlockGroup onlyCreate(Block core,  String... registryNames) {
        return create(core, false, registryNames);
    }

    private static DefaultStoneBlockGroup create(Block core, boolean autoRegister, String... registryNames) {
        return new DefaultStoneBlockGroup(core, ModItems.common(), BlockModelDataGen::prefix, autoRegister, registryNames);
    }

    @Override
    protected boolean isStone() {
        return true;
    }

    @Override
    protected WalledBlockItemGroup initItems(Item.Properties properties) {
        return new WalledBlockItemGroup(new BlockItem(core(), properties), new BlockItem(slab(), properties), new BlockItem(stairs(), properties), new BlockItem(wall(), properties));
    }
}
