package lych.soullery.block;

import lych.soullery.block.entity.SEStorageTileEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;

public class ModBlockStateProperties {
    public static final BooleanProperty IS_GENERATING_SE = BooleanProperty.create("is_generating_se");
    public static final IntegerProperty SOUL_ENERGY_LEVEL = IntegerProperty.create("soul_energy_level", 0, SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL);
}
