package lych.soullery.world;

import lych.soullery.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class CommandData extends WorldSavedData {
    private static final String NAME = "CommandData";
    private boolean allowedCommands;

    public CommandData() {
        this(NAME);
    }

    public CommandData(String name) {
        super(name);
    }

    public static CommandData get(MinecraftServer server) {
        ServerWorld overworld = server.getLevel(World.OVERWORLD);
        if (overworld != null) {
            DimensionSavedDataManager storage = overworld.getDataStorage();
            return storage.computeIfAbsent(CommandData::new, NAME);
        }
        throw new IllegalStateException("Overworld is missing");
    }

    public void update(ServerWorld world) {
        if (allowedCommands) {
            return;
        }
        if (Utils.allowsCommands(world)) {
            allowedCommands = true;
            setDirty();
        }
    }

    public boolean isAllowedCommands() {
        return allowedCommands;
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        allowedCommands = compoundNBT.getBoolean("AllowedCommands");
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        compoundNBT.putBoolean("AllowedCommands", allowedCommands);
        return compoundNBT;
    }
}
