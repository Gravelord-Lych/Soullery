package lych.soullery.world.teleporter;

import lych.soullery.util.TeleporterUtils;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class DirectTeleporter implements ITeleporter {
    @NotNull
    private final Vector3d location;

    public DirectTeleporter(Vector3d location) {
        this.location = Objects.requireNonNull(location);
    }

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
        return TeleporterUtils.createCommonInfo(entity, location);
    }
}
