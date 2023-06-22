package lych.soullery.util.blg;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.BlockItem;

import java.util.List;

public class WalledBlockItemGroup extends BlockItemGroup {
    private final BlockItem wall;

    public WalledBlockItemGroup(BlockItem core, BlockItem slab, BlockItem stairs, BlockItem wall) {
        super(core, slab, stairs);
        this.wall = wall;
    }

    public BlockItem wall() {
        return wall;
    }

    @Override
    public List<? extends BlockItem> forRegistration() {
        return ImmutableList.<BlockItem>builder().addAll(super.forRegistration()).add(wall).build();
    }
}
