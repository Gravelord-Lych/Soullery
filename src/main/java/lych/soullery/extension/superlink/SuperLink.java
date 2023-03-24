package lych.soullery.extension.superlink;

import com.google.common.base.MoreObjects;
import lych.soullery.extension.superlink.InvalidSuperLinkException.Type;
import lych.soullery.util.SoulEnergies;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;

public class SuperLink {
    private final GlobalPos from;
    private final GlobalPos to;
    private final int efficiency;

    public SuperLink(GlobalPos from, GlobalPos to, boolean allowDifferentDimension, double maxDist, int efficiency) throws InvalidSuperLinkException {
        this.from = from;
        this.to = to;
        this.efficiency = efficiency;
        if (!allowDifferentDimension && from.dimension() != to.dimension()) {
            throw new InvalidSuperLinkException(Type.NOT_IN_SAME_DIM);
        }
        if (Double.isFinite(maxDist) && from.dimension() == to.dimension() && from.pos().distSqr(to.pos()) > maxDist * maxDist) {
            throw new InvalidSuperLinkException(Type.FARAWAY);
        }
    }

    public SuperLink(CompoundNBT nbt) throws InvalidSuperLinkException {
        try {
            RegistryKey<World> fd = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("FDimension")));
            RegistryKey<World> td = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("TDimension")));
            from = GlobalPos.of(fd, new BlockPos(nbt.getInt("FX"), nbt.getInt("FY"), nbt.getInt("FZ")));
            to = GlobalPos.of(td, new BlockPos(nbt.getInt("TX"), nbt.getInt("TY"), nbt.getInt("TZ")));
            efficiency = nbt.getInt("Efficiency");
        } catch (Throwable throwable) {
            throw new InvalidSuperLinkException(throwable, Type.INVALID);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void tick(MinecraftServer server) throws InvalidSuperLinkException {
        ServerWorld fw = server.getLevel(from.dimension());
        ServerWorld tw = server.getLevel(to.dimension());
        if (stillValid(fw, tw)) {
            tickTransfer(fw.getBlockEntity(from.pos()), tw.getBlockEntity(to.pos()));
        } else {
            throw new InvalidSuperLinkException(Type.INVALID);
        }
    }

    private void tickTransfer(TileEntity from, TileEntity to) {
        SoulEnergies.transfer(from, to, efficiency);
    }

    private boolean stillValid(ServerWorld fw, ServerWorld tw) {
        TileEntity blockEntity1 = fw.getBlockEntity(from.pos());
        TileEntity blockEntity2 = tw.getBlockEntity(to.pos());
        if (blockEntity1 == null || blockEntity2 == null) {
            return false;
        }
        return SoulEnergies.of(blockEntity1).isPresent() && SoulEnergies.of(blockEntity2).isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperLink superLink = (SuperLink) o;
        return efficiency == superLink.efficiency && Objects.equals(from, superLink.from) && Objects.equals(to, superLink.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, efficiency);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("from", from)
                .add("to", to)
                .add("efficiency", efficiency)
                .toString();
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putString("FDimension", from.dimension().getRegistryName().toString());
        compoundNBT.putInt("FX", from.pos().getX());
        compoundNBT.putInt("FY", from.pos().getY());
        compoundNBT.putInt("FZ", from.pos().getZ());
        compoundNBT.putString("TDimension", to.dimension().getRegistryName().toString());
        compoundNBT.putInt("TX", to.pos().getX());
        compoundNBT.putInt("TY", to.pos().getY());
        compoundNBT.putInt("TZ", to.pos().getZ());
        compoundNBT.putInt("Efficiency", efficiency);
        return compoundNBT;
    }
}
