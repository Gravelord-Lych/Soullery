package lych.soullery.block.entity;

import lych.soullery.entity.ModEntities;
import lych.soullery.entity.monster.boss.enchanter.EnchanterEntity;
import lych.soullery.util.Arena;
import lych.soullery.util.Utils;
import lych.soullery.world.gen.structure.piece.SkyCityPieces;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class EnchanterSpawnerTileEntity extends InstantSpawnerTileEntity {
    private final List<BlockPos> gate = new ArrayList<>();
    private final List<BlockPos> disassemblers = new ArrayList<>();
    private Arena arena = Arena.INFINITY;

    public EnchanterSpawnerTileEntity(TileEntityType<?> type) {
        super(type);
        setType(ModEntities.ENCHANTER);
    }

    @Override
    protected void customizeMob(PlayerEntity player, MobEntity mob, ServerWorld level, BlockPos pos) {
        super.customizeMob(player, mob, level, pos);
        EnchanterEntity enchanter = (EnchanterEntity) mob;
        enchanter.setArena(arena);
        enchanter.addDisassemblers(disassemblers);
        for (BlockPos posIn : gate) {
            level.setBlock(posIn, SkyCityPieces.FENCE, Constants.BlockFlags.DEFAULT);
        }
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void addGateBlock(BlockPos pos) {
        gate.add(pos);
    }

    public void addDisassembler(BlockPos pos) {
        disassemblers.add(pos);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        CompoundNBT nbt = super.save(compoundNBT);
        nbt.put("Arena", getArena().save());
        Utils.saveBlockPosList(compoundNBT, gate, "Gate");
        Utils.saveBlockPosList(compoundNBT, disassemblers, "Disassembler");
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        if (compoundNBT.contains("Arena")) {
            setArena(Arena.load(compoundNBT.getCompound("Arena")));
        }
        Utils.loadBlockPosList(compoundNBT, gate, "Gate");
        Utils.loadBlockPosList(compoundNBT, disassemblers, "Disassembler");
    }
}
