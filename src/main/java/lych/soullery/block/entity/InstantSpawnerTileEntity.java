package lych.soullery.block.entity;

import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

public class InstantSpawnerTileEntity extends TileEntity implements ITickableTileEntity {
    public static final int SHORT_RANGE = 6;
    public static final int DEFAULT_RANGE = 10;
    public static final int LONG_RANGE = 20;
    public static final int LONGEST_RANGE = 50;
    private static final Marker MARKER = MarkerManager.getMarker("InstantSpawner");
    private int range = DEFAULT_RANGE;
    private int verticalRange = -1;
    private int restrictRadiusMultiplier = 5;
    @Nullable
    private EntityType<?> type;

    public InstantSpawnerTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if (type == null) {
            return;
        }
        if (level != null && !level.isClientSide()) {
            BlockPos pos = getBlockPos();
            PlayerEntity player = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, range, false);
            if (player != null) {
                if (!(specifiedVerticalRange() && Math.abs(pos.getY() - player.getY()) > verticalRange)) {
                    ServerWorld serverLevel = (ServerWorld) level;
                    boolean spawned = spawn(player, serverLevel, pos);
                    if (spawned) {
                        double x = (double) pos.getX() + level.random.nextDouble();
                        double y = (double) pos.getY() + level.random.nextDouble();
                        double z = (double) pos.getZ() + level.random.nextDouble();
                        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0, 0, 0, 0);
                        serverLevel.sendParticles(ParticleTypes.FLAME, x, y, z, 1, 0, 0, 0, 0);
                        level.removeBlock(getBlockPos(), false);
                    }
                }
            }
        }
    }

    private boolean spawn(PlayerEntity player, ServerWorld level, BlockPos pos) {
        Entity entity = type.create(level);
        if (entity == null) {
            return false;
        }
        entity.moveTo(pos, level.getRandom().nextFloat() * 360, 0);
        if (entity instanceof MobEntity) {
            MobEntity mob = (MobEntity) entity;
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), SpawnReason.SPAWNER, null, null);
            mob.restrictTo(getBlockPos(), range * restrictRadiusMultiplier);
        }
        return level.addFreshEntity(entity);
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        CompoundNBT nbt = super.save(compoundNBT);
        if (type != null) {
            nbt.putString("SpawnType", Utils.getRegistryName(type).toString());
        }
        nbt.putInt("Range", range);
        nbt.putInt("VerticalRange", verticalRange);
        nbt.putInt("RestrictRadiusMultiplier", restrictRadiusMultiplier);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        if (compoundNBT.contains("SpawnType")) {
            String typeName = compoundNBT.getString("SpawnType");
            ResourceLocation location;
            try {
                location = new ResourceLocation(typeName);
            } catch (ResourceLocationException e) {
                String message = String.format("Invalid entity id: %s at InstantSpawner %s:[%d,%d,%d]", typeName, getLevel().dimension().location(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                if (ConfigHelper.shouldFailhard()) {
                    throw new IllegalStateException(ConfigHelper.FAILHARD_MESSAGE + message, e);
                }
                Soullery.LOGGER.error(MARKER, message, e);
                return;
            }
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(location);
            if (type == null) {
                Soullery.LOGGER.warn(MARKER, "Invalid SpawnEntityType: {} at InstantSpawner {}:[{},{},{}]", typeName, getLevel().dimension().location(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                return;
            }
            setType(type);
        }
        if (compoundNBT.contains("Range")) {
            setRange(compoundNBT.getInt("Range"));
        }
        if (compoundNBT.contains("VerticalRange")) {
            setVerticalRange(compoundNBT.getInt("VerticalRange"));
        }
        if (compoundNBT.contains("RestrictRadiusMultiplier")) {
            setRestrictRadiusMultiplier(compoundNBT.getInt("RestrictRadiusMultiplier"));
        }
    }

    private boolean specifiedVerticalRange() {
        return verticalRange > 0;
    }

    public void setType(EntityType<?> type) {
        this.type = type;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setVerticalRange(int verticalRange) {
        this.verticalRange = verticalRange;
    }

    public void setRestrictRadiusMultiplier(int restrictRadiusMultiplier) {
        this.restrictRadiusMultiplier = restrictRadiusMultiplier;
    }
}
