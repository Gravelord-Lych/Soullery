package lych.soullery.extension.superlink;

import lych.soullery.Soullery;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SuperLinkManager extends WorldSavedData {
    static final Marker SUPER_LINKS = MarkerManager.getMarker("SuperLinks");
    private static final String NAME = "SuperLinkManager";
    private final List<SuperLink> superLinks = new ArrayList<>();

    public SuperLinkManager() {
        super(NAME);
    }

    public void tick(MinecraftServer server) {
        Iterator<SuperLink> iterator = superLinks.iterator();
        while (iterator.hasNext()) {
            SuperLink superLink = iterator.next();
            try {
                superLink.tick(server);
            } catch (InvalidSuperLinkException e) {
                iterator.remove();
            }
        }
    }

    public static SuperLinkManager get(ServerWorld level) {
        ServerWorld overworld = level.getServer().getLevel(World.OVERWORLD);
        Objects.requireNonNull(overworld);
        DimensionSavedDataManager storage = overworld.getDataStorage();
        return storage.computeIfAbsent(SuperLinkManager::new, NAME);
    }

    public boolean addSuperLink(World world, BlockPos from, BlockPos to, boolean allowDifferentDimension, double maxDist, int efficiency) {
        return addSuperLink(world, from, world, to, allowDifferentDimension, maxDist, efficiency);
    }

    public boolean addSuperLink(World world1, BlockPos from, World world2, BlockPos to, boolean allowDifferentDimension, double maxDist, int efficiency) {
        return addSuperLink(GlobalPos.of(world1.dimension(), from), GlobalPos.of(world2.dimension(), to), allowDifferentDimension, maxDist, efficiency);
    }

    public boolean addSuperLink(GlobalPos from, GlobalPos to, boolean allowDifferentDimension, double maxDist, int efficiency) {
        try {
            SuperLink superLink = new SuperLink(from, to, allowDifferentDimension, maxDist, efficiency);
            return add(superLink);
        } catch (InvalidSuperLinkException e) {
            return false;
        }
    }

    public boolean add(SuperLink superLink) {
        return superLinks.add(superLink);
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        superLinks.clear();
        ListNBT listNBT = compoundNBT.getList("SuperLinks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT superLinkNBT = listNBT.getCompound(i);
            try {
                superLinks.add(new SuperLink(superLinkNBT));
            } catch (InvalidSuperLinkException e) {
                Soullery.LOGGER.warn(SuperLinkManager.SUPER_LINKS, "Failed to load a super link", e.getCause());
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        ListNBT listNBT = new ListNBT();
        for (SuperLink superLink : superLinks) {
            listNBT.add(superLink.save());
        }
        compoundNBT.put("SuperLinks", listNBT);
        return compoundNBT;
    }
}
