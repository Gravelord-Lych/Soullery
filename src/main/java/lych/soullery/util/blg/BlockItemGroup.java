package lych.soullery.util.blg;

import com.google.common.collect.ImmutableList;
import lych.soullery.data.ItemModelDataGen;
import net.minecraft.item.BlockItem;
import net.minecraftforge.client.model.generators.ItemModelProvider;

import java.util.List;

public class BlockItemGroup {
    private final BlockItem core;
    private final BlockItem slab;
    private final BlockItem stairs;

    public BlockItemGroup(BlockItem core, BlockItem slab, BlockItem stairs) {
        this.core = core;
        this.slab = slab;
        this.stairs = stairs;
    }

    public BlockItem core() {
        return core;
    }

    public BlockItem slab() {
        return slab;
    }

    public BlockItem stairs() {
        return stairs;
    }

    public List<? extends BlockItem> forRegistration() {
        return ImmutableList.of(core, slab, stairs);
    }

    public void fillModels(ItemModelProvider provider) {
        for (BlockItem item : forRegistration()) {
            ItemModelDataGen.blockItem(provider, item);
        }
    }
}
