package lych.soullery.util.data;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;

import java.util.Objects;
import java.util.OptionalLong;

public class DimensionTypeBuilder implements IDataBuilder {
    private final String modid;
    private final String name;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private OptionalLong fixedTime = OptionalLong.empty();
    private boolean hasSkylight = true;
    private boolean hasCeiling;
    private boolean ultraWarm;
    private boolean natural;
    private double coordinateScale = 1;
    private boolean createDragonFight;
    private boolean piglinSafe;
    private boolean bedWorks;
    private boolean respawnAnchorWorks;
    private boolean hasRaids;
    private int logicalHeight;
    private IBiomeMagnifier biomeZoomer = FuzzedBiomeMagnifier.INSTANCE;
    private ResourceLocation infiniburn;
    private ResourceLocation effectsLocation;
    private float ambientLight;

    public DimensionTypeBuilder(String modid, String name) {
        this.modid = modid;
        this.name = name;
    }

    public DimensionTypeBuilder fixedTime(long fixedTime) {
        return fixedTime(OptionalLong.of(fixedTime));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DimensionTypeBuilder fixedTime(OptionalLong fixedTime) {
        this.fixedTime = fixedTime;
        return this;
    }

    public DimensionTypeBuilder noSkylight() {
        this.hasSkylight = false;
        return this;
    }

    public DimensionTypeBuilder hasCeiling() {
        this.hasCeiling = true;
        return this;
    }

    public DimensionTypeBuilder ultraWarm() {
        this.ultraWarm = true;
        return this;
    }

    public DimensionTypeBuilder natural() {
        this.natural = true;
        return this;
    }

    public DimensionTypeBuilder coordinateScale(double coordinateScale) {
        this.coordinateScale = coordinateScale;
        return this;
    }

    @Deprecated
    public DimensionTypeBuilder createDragonFight(boolean createDragonFight) {
        this.createDragonFight = createDragonFight;
        return this;
    }

    public DimensionTypeBuilder piglinSafe() {
        this.piglinSafe = true;
        return this;
    }

    public DimensionTypeBuilder bedWorks() {
        this.bedWorks = true;
        return this;
    }

    public DimensionTypeBuilder respawnAnchorWorks() {
        this.respawnAnchorWorks = true;
        return this;
    }

    public DimensionTypeBuilder hasRaids() {
        this.hasRaids = true;
        return this;
    }

    public DimensionTypeBuilder logicalHeight(int logicalHeight) {
        this.logicalHeight = logicalHeight;
        return this;
    }

    @Deprecated
    public DimensionTypeBuilder biomeZoomer(IBiomeMagnifier biomeZoomer) {
        this.biomeZoomer = biomeZoomer;
        return this;
    }

    public DimensionTypeBuilder infiniburn(ResourceLocation infiniburn) {
        this.infiniburn = infiniburn;
        return this;
    }

    public DimensionTypeBuilder effectsLocation(ResourceLocation effectsLocation) {
        this.effectsLocation = effectsLocation;
        return this;
    }

    public DimensionTypeBuilder ambientLight(float ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    @Deprecated
    public DimensionType build() {
        return new DimensionType(fixedTime, hasSkylight, hasCeiling, ultraWarm, natural, coordinateScale, createDragonFight, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, biomeZoomer, infiniburn, effectsLocation, ambientLight) {};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DimensionTypeBuilder that = (DimensionTypeBuilder) o;
        return Objects.equals(modid, that.modid) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modid, name);
    }

    @Override
    public String getNamespace() {
        return modid;
    }

    @Override
    public String getPath() {
        return name;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        fixedTime.ifPresent(time -> root.addProperty("fixed_time", time));
        root.addProperty("has_skylight", hasSkylight);
        root.addProperty("has_ceiling", hasCeiling);
        root.addProperty("ultrawarm", ultraWarm);
        root.addProperty("natural", natural);
        root.addProperty("coordinate_scale", coordinateScale);
//        root.addProperty("create_dragon_fight", createDragonFight);
        root.addProperty("piglin_safe", piglinSafe);
        root.addProperty("bed_works", bedWorks);
        root.addProperty("respawn_anchor_works", respawnAnchorWorks);
        root.addProperty("has_raids", hasRaids);
        root.addProperty("logical_height", logicalHeight);
        root.addProperty("infiniburn", infiniburn.toString());
        root.addProperty("effects", effectsLocation.toString());
        root.addProperty("ambient_light", ambientLight);
        return root;
    }
}