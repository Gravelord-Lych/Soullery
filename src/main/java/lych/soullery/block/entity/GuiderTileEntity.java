package lych.soullery.block.entity;

import lych.soullery.entity.functional.SoulCrystalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.Nullable;

public class GuiderTileEntity extends TileEntity {
    private int commander = -1;

    public GuiderTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Nullable
    public SoulCrystalEntity getCommander() {
        if (level == null || commander == -1) {
            return null;
        }
        Entity entity = level.getEntity(commander);
        if (entity instanceof SoulCrystalEntity) {
            return (SoulCrystalEntity) entity;
        }
        return null;
    }

    public void setCommander(SoulCrystalEntity commander) {
        this.commander = commander.getId();
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        super.save(compoundNBT);
        compoundNBT.putInt("Commander", commander);
        return compoundNBT;
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        if (compoundNBT.contains("Commander")) {
            commander = compoundNBT.getInt("Commander");
        }
    }
}
