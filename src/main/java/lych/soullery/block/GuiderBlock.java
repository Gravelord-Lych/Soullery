package lych.soullery.block;

import lych.soullery.block.entity.GuiderTileEntity;
import lych.soullery.block.entity.ModTileEntities;

public class GuiderBlock extends SimpleTileEntityBlock {
    public GuiderBlock(Properties properties) {
        super(properties, () -> new GuiderTileEntity(ModTileEntities.GUIDER));
    }
}
