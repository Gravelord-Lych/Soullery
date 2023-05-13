package lych.soullery.world.event;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import lych.soullery.Soullery;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ModEntityNames;
import lych.soullery.entity.functional.FortifiedSoulCrystalEntity;
import lych.soullery.entity.functional.SoulCrystalEntity;
import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.entity.monster.boss.souldragon.phase.PhaseType;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.PositionCalculators;
import lych.soullery.world.ModTickets;
import lych.soullery.world.event.manager.NotLoadedException;
import lych.soullery.world.gen.config.PlateauSpikeConfig;
import lych.soullery.world.gen.feature.CentralSoulCrystalFeature;
import lych.soullery.world.gen.feature.ModConfiguredFeatures;
import lych.soullery.world.gen.feature.ModFeatures;
import lych.soullery.world.gen.feature.PlateauSpikeFeature;
import lych.soullery.world.gen.feature.PlateauSpikeFeature.Spike;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Unit;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static lych.soullery.Soullery.LOGGER;

public class SoulDragonFight extends AbstractWorldEvent {
    public static final Marker MARKER = MarkerManager.getMarker("SoulDragonFight");
    private static final double ADD_PLAYER_RADIUS = 128;
    private static final int BUILD_INTERVAL = 40;
    private static final int BUILD_DELAY_TICKS = 100;
    private static final int TOTAL_TICKS = BUILD_INTERVAL * PlateauSpikeFeature.COUNT + BUILD_DELAY_TICKS;
    private final ServerBossInfo bossInfo = new ServerBossInfo(new TranslationTextComponent(String.format("entity.%s.%s", Soullery.MOD_ID, ModEntityNames.SOUL_DRAGON)), BossInfo.Color.BLUE, BossInfo.Overlay.NOTCHED_6);
    private final List<Spike> generatedSpikes = new ArrayList<>();
    @Nullable
    private FortifiedSoulCrystalEntity superCrystal;
    private boolean spawnedSuperCrystal;
    @Nullable
    private UUID dragonUUID;
    private Status status;
    private Respawner respawner;
    private int crystalsAlive;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private int ticksSinceLastSetDirty;
    private int victoryTicks;

    public SoulDragonFight(int id, ServerWorld level, BlockPos center, List<Spike> spikes) {
        super(id, level, center);
        this.status = Status.PREPARING;
        this.respawner = new Respawner(spikes);
    }

    public SoulDragonFight(ServerWorld level, CompoundNBT compoundNBT) throws NotLoadedException {
        super(level, compoundNBT);
        if (compoundNBT.contains("DragonUUID")) {
            dragonUUID = compoundNBT.getUUID("DragonUUID");
        }
        try {
            status = IIdentifiableEnum.byId(Status.values(), compoundNBT.getInt("Status"));
        } catch (EnumConstantNotFoundException e) {
            throw new NotLoadedException(String.format("Soul dragon fight status not found: %d", e.getId()));
        }
        if (compoundNBT.contains("Respawner", Constants.NBT.TAG_COMPOUND)) {
            respawner = new Respawner(compoundNBT.getCompound("Respawner"));
        }
        generatedSpikes.addAll(loadSpikes(compoundNBT, "GeneratedSpikes"));
        spawnedSuperCrystal = compoundNBT.getBoolean("SpawnedSuperCrystal");
    }

    @Override
    public void stop() {
        status = Status.STOPPED;
        bossInfo.removeAllPlayers();
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isStopped() {
        return status == Status.STOPPED;
    }

    @Override
    public boolean isOver() {
        return status == Status.VICTORY || status == Status.STOPPED;
    }

    public ServerBossInfo getBossInfo() {
        return bossInfo;
    }

    @Override
    protected void eventTick() {
        super.eventTick();
        if (status == Status.PREPARING) {
            if (respawner == null) {
                throw new NullPointerException("Respawner is null");
            }
            if (respawner.tick()) {
                status = Status.ONGOING;
                respawner = null;
                setDirty();
            }
        } else if (status == Status.ONGOING) {
            if (++ticksSinceLastPlayerScan >= 20) {
                updatePlayers();
                ticksSinceLastPlayerScan = 0;
            }
            if (!bossInfo.getPlayers().isEmpty()) {
                level.getChunkSource().addRegionTicket(ModTickets.SOUL_DRAGON, getCenterChunk(), 9, Unit.INSTANCE);
                tickCrystals();
            } else {
                level.getChunkSource().removeRegionTicket(ModTickets.SOUL_DRAGON, getCenterChunk(), 9, Unit.INSTANCE);
            }
            if (++ticksSinceLastSetDirty >= 40) {
                setDirty();
                ticksSinceLastSetDirty = 0;
            }
            if (spawnedSuperCrystal) {
                if (superCrystal == null || ticksSinceCrystalsScanned == 50) {
                    boolean wasNull = superCrystal == null;
                    checkSuperCrystal();
                    if (superCrystal != null) {
                        if (wasNull) {
                            LOGGER.debug(MARKER, "Found new Fortified Soul Crystal");
                        } else {
                            LOGGER.debug(MARKER, "Found that the previous Fortified Soul Crystal is still alive");
                        }
                    }
                }
                if (EntityUtils.isDead(superCrystal)) {
                    superCrystal = null;
                    LOGGER.debug(MARKER, "Fortified Soul Crystal is destroyed!");
                    setDirty();
                }
            }
        } else {
            victoryTicks++;
            if (victoryTicks > 150) {
                stop();
            }
        }
    }

    @NotNull
    private ChunkPos getCenterChunk() {
        return new ChunkPos(center.getX() >> 4, center.getZ() >> 4);
    }

    private void tickCrystals() {
        boolean loaded = isArenaLoaded();
        if (++ticksSinceCrystalsScanned >= 100 && loaded) {
            updateCrystalCount();
            ticksSinceCrystalsScanned = 0;
        }
    }

    private boolean checkSuperCrystal() {
        if (EntityUtils.isAlive(superCrystal)) {
            return false;
        }
        int startY = center.getY() + CentralSoulCrystalFeature.DISTANCE_TO_GROUND;
        int endY = startY + CentralSoulCrystalFeature.HEIGHT + 4;
        int radius = CentralSoulCrystalFeature.RADIUS + 1;
        AxisAlignedBB bb = new AxisAlignedBB(center.getX() + 0.5 - radius, startY, center.getZ() + 0.5 - radius, center.getX() + 0.5 + radius, endY, center.getZ() + 0.5 + radius);
        List<FortifiedSoulCrystalEntity> crystals = level.getEntitiesOfClass(FortifiedSoulCrystalEntity.class, bb, FortifiedSoulCrystalEntity::isHealable);
        if (!crystals.isEmpty()) {
            boolean same = superCrystal == crystals.get(0);
            superCrystal = crystals.get(0);
            return !same;
        }
        return false;
    }

    private boolean isArenaLoaded() {
        int x = center.getX() >> 4;
        int z = center.getZ() >> 4;

        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                IChunk chunk = level.getChunk(x + i, z + j, ChunkStatus.FULL, false);
                if (!(chunk instanceof Chunk)) {
                    if (chunk == null) {
                        LOGGER.debug("Arena is null chunk, waiting...");
                    } else {
                        LOGGER.debug("Chunk{} is not loaded, waiting...", chunk.getPos());
                    }
                    return false;
                }

                ChunkHolder.LocationType type = ((Chunk) chunk).getFullStatus();
                if (!type.isOrAfter(ChunkHolder.LocationType.TICKING)) {
                    LOGGER.debug("Chunk{} is not fully loaded ({}), waiting...", chunk.getPos(), type);
                    return false;
                }
            }
        }

        return true;
    }

    private void updatePlayers() {
        Set<ServerPlayerEntity> validPlayers = Sets.newHashSet();

        for(ServerPlayerEntity player : level.getPlayers(EntityPredicates.ENTITY_STILL_ALIVE.and(EntityPredicates.withinDistance(center.getX(), center.getY(), center.getZ(), ADD_PLAYER_RADIUS)))) {
            bossInfo.addPlayer(player);
            validPlayers.add(player);
        }

        Set<ServerPlayerEntity> oldPlayers = Sets.newHashSet(bossInfo.getPlayers());
        oldPlayers.removeAll(validPlayers);

        for(ServerPlayerEntity player : oldPlayers) {
            bossInfo.removePlayer(player);
        }
    }

    public void placeSuperCrystal() {
        ModConfiguredFeatures.CENTRAL_SOUL_CRYSTAL.place(level, level.getChunkSource().getGenerator(), level.getRandom(), getCenter().above(CentralSoulCrystalFeature.DISTANCE_TO_GROUND));
        superCrystal = CentralSoulCrystalFeature.getCurrentCrystal();
        LOGGER.debug(MARKER, "Fortified Soul Crystal is placed!");
        spawnedSuperCrystal = true;
        setDirty();
    }

    public List<SoulCrystalEntity> getCrystals() {
        List<SoulCrystalEntity> crystals = new ArrayList<>();
        for (Spike spike : generatedSpikes) {
            crystals.addAll(level.getEntitiesOfClass(SoulCrystalEntity.class, spike.getTopBoundingBox(), SoulCrystalEntity::isHealable));
        }
        return crystals;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        if (dragonUUID != null) {
            compoundNBT.putUUID("DragonUUID", dragonUUID);
        }
        compoundNBT.putInt("Status", status.getId());
        if (respawner != null) {
            compoundNBT.put("Respawner", respawner.save());
        }
        ListNBT spikesNBT = new ListNBT();
        for (Spike spike : generatedSpikes) {
            saveSpike(spikesNBT, spike);
        }
        compoundNBT.put("GeneratedSpikes", spikesNBT);
        compoundNBT.putBoolean("SpawnedSuperCrystal", spawnedSuperCrystal);
    }

    public static ITextComponent makeText(String name, Object... args) {
        return new TranslationTextComponent(Soullery.prefixMsg("soul_dragon_fight", name), args);
    }

    private void updateCrystalCount() {
        updateCrystalCount(getCrystals());
    }

    private void updateCrystalCount(List<SoulCrystalEntity> crystals) {
        this.ticksSinceCrystalsScanned = 0;
        this.crystalsAlive = crystals.size();
        LOGGER.debug(MARKER, "Found {} Soul Crystal(s) still alive", crystalsAlive);
    }

    public int getCrystalsAlive() {
        return crystalsAlive;
    }

    public void updateDragon(SoulDragonEntity dragon) {
        checkUUID(dragon);
        bossInfo.setPercent(dragon.getHealth() / dragon.getMaxHealth());
        if (dragon.getHealthStatus() == SoulDragonEntity.HealthStatus.LOW) {
            bossInfo.setColor(BossInfo.Color.RED);
        } else {
            bossInfo.setColor(dragon.isPurified() ? BossInfo.Color.PURPLE : BossInfo.Color.BLUE);
        }
        if (dragon.hasCustomName()) {
            bossInfo.setName(dragon.getDisplayName());
        }
    }

    public void onCrystalDestroyed(SoulCrystalEntity crystal, DamageSource source) {
        List<SoulCrystalEntity> crystals = getCrystals();
        if (crystals.contains(crystal)) {
            Entity entity = level.getEntity(dragonUUID);
            if (entity instanceof SoulDragonEntity) {
                ((SoulDragonEntity) entity).onCrystalDestroyed(crystal, crystal.blockPosition(), source);
            }
        }
    }

    public void setDragonKilled(SoulDragonEntity dragon) {
        checkUUID(dragon);
        bossInfo.setPercent(0);
        bossInfo.setVisible(false);
        status = Status.VICTORY;
        setDirty();
    }

    private void checkUUID(SoulDragonEntity dragon) {
        if (!dragon.getUUID().equals(dragonUUID)) {
            throw new IllegalArgumentException(String.format("Invalid UUID %s, required UUID is %s", dragon.getUUID(), dragonUUID));
        }
    }

    @Nullable
    public FortifiedSoulCrystalEntity getSuperCrystal() {
        return superCrystal;
    }

    public enum Status implements IIdentifiableEnum {
        PREPARING,
        ONGOING,
        VICTORY,
        STOPPED
    }

    public class Respawner {
        private final List<Spike> spikes;
        private SoulCrystalEntity guider;
        private int summonTicks;
        private int finishTicks;

        public Respawner(List<Spike> spikes) {
            this.spikes = spikes;
        }

        public Respawner(CompoundNBT compoundNBT) {
            this.summonTicks = compoundNBT.getInt("SummonTicks");
            this.finishTicks = compoundNBT.getInt("FinishTicks");
            this.spikes = loadSpikes(compoundNBT, "Spikes");
        }

        public boolean tick() {
            if (summonTicks == 0) {
                start();
            }
            summonTicks++;
            bossInfo.setPercent((float) summonTicks / TOTAL_TICKS);
            updateGuider();
            if (guider == null) {
                stop();
                return false;
            }
            if (finishTicks > 0) {
                return tickLast();
            }
            if (summonTicks % BUILD_INTERVAL == 0) {
                if (spikes.isEmpty()) {
                    finishTicks = summonTicks;
                    setDirty();
                    return false;
                }
                Spike spike = spikes.get(0);
                spikes.remove(0);
                int baseHeight = PositionCalculators.heightmap(spike.getCenterX(), spike.getCenterZ(), level) + PlateauSpikeFeature.Y_OFFSET;
                ModFeatures.PLATEAU_SPIKE.configured(new PlateauSpikeConfig(center, ImmutableList.of(spike))).place(level, level.getChunkSource().getGenerator(), level.getRandom(), center);
                generatedSpikes.add(spike.generate(baseHeight, baseHeight + spike.getHeight()));
                setDirty();
            }
            return false;
        }

        private void start() {
            guider = new SoulCrystalEntity(level, center.getX() + 0.5, center.getY(), center.getZ() + 0.5);
            guider.setInvulnerable(true);
            level.addFreshEntity(guider);
            guider.setBeamTarget(aboveCenter60());
            setDirty();
        }

        private void updateGuider() {
            List<SoulCrystalEntity> guiders = level.getEntitiesOfClass(SoulCrystalEntity.class, new AxisAlignedBB(center).inflate(1));
            if (guiders.isEmpty()) {
                guider = null;
            } else {
                guiders.sort(Comparator.comparingDouble(c -> c.distanceToSqr(Vector3d.atBottomCenterOf(center))));
                guider = guiders.get(0);
                guiders.forEach(g -> {
                    if (g != guider) {
                        g.setBeamTarget(null);
                    } else if (!g.getBeamTarget().equals(aboveCenter60())) {
                        g.setBeamTarget(aboveCenter60());
                    }
                });
            }
        }

        private boolean tickLast() {
            int postTicks = summonTicks - finishTicks;
            getCrystals().forEach(crystal -> crystal.setBeamTarget(aboveCenter60()));
            if (postTicks > BUILD_DELAY_TICKS) {
                spawnSoulDragon();
                return true;
            }
            return false;
        }

        private BlockPos aboveCenter60() {
            return center.above(60);
        }

        private void spawnSoulDragon() {
            SoulDragonEntity dragon = ModEntities.SOUL_DRAGON.create(level);
            dragon.setFight(SoulDragonFight.this);
            dragon.setPhase(PhaseType.DEFAULT);
            dragon.moveTo(center.above(3), 0, 0);
            dragon.finalizeSpawn(level, level.getCurrentDifficultyAt(center), SpawnReason.EVENT, null, null);
            dragonUUID = dragon.getUUID();
            level.addFreshEntity(dragon);
            getCrystals().forEach(crystal -> crystal.setBeamTarget(null));
            if (EntityUtils.isAlive(guider)) {
                level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, guider.getX(), guider.getY(), guider.getZ(), 1, 0, 0, 0, 0);
                guider.remove();
            }
        }

        public CompoundNBT save() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putInt("SummonTicks", summonTicks);
            compoundNBT.putInt("FinishTicks", finishTicks);
            ListNBT spikesNBT = new ListNBT();
            for (Spike spike : spikes) {
                saveSpike(spikesNBT, spike);
            }
            compoundNBT.put("Spikes", spikesNBT);
            return compoundNBT;
        }
    }

    private static List<Spike> loadSpikes(CompoundNBT compoundNBT, String name) {
        List<Spike> spikes = new LinkedList<>();
        ListNBT spikesNBT = compoundNBT.getList(name, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < spikesNBT.size(); i++) {
            loadSpike(spikes, spikesNBT, i);
        }
        return spikes;
    }

    private static void loadSpike(List<Spike> spikes, ListNBT spikesNBT, int i) {
        CompoundNBT spikeNBT = spikesNBT.getCompound(i);
        int centerX = spikeNBT.getInt("CenterX");
        int centerZ = spikeNBT.getInt("CenterZ");
        int radius = spikeNBT.getInt("Radius");
        int height = spikeNBT.getInt("Height");
        Spike spike = new Spike(centerX, centerZ, radius, height);
        if (spikeNBT.contains("BaseHeight") && spikeNBT.contains("BBMinY")) {
            int baseHeight = spikeNBT.getInt("BaseHeight");
            double minY = spikeNBT.getDouble("BBMinY");
            spike = spike.generate(baseHeight, minY);
        }
        spikes.add(spike);
    }

    private static void saveSpike(ListNBT spikesNBT, Spike spike) {
        CompoundNBT spikeNBT = new CompoundNBT();
        spikeNBT.putInt("CenterX", spike.getCenterX());
        spikeNBT.putInt("CenterZ", spike.getCenterZ());
        spikeNBT.putInt("Height", spike.getHeight());
        spikeNBT.putInt("Radius", spike.getRadius());
        if (spike.isGenerated()) {
            spikeNBT.putInt("BaseHeight", spike.getBaseHeight());
            spikeNBT.putDouble("BBMinY", spike.getTopBoundingBox().minY);
        }
        spikesNBT.add(spikeNBT);
    }
}
