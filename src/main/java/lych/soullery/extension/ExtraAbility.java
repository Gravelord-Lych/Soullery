package lych.soullery.extension;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lych.soullery.api.exa.ExaNames;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.api.exa.MobDebuff;
import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.entity.ModEntities;
import lych.soullery.extension.soulpower.buff.*;
import lych.soullery.extension.soulpower.debuff.FrostResistanceDebuff;
import lych.soullery.extension.soulpower.debuff.MobDebuffMap;
import lych.soullery.extension.soulpower.debuff.MonsterSabotageDebuff;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static lych.soullery.Soullery.prefix;

public final class ExtraAbility implements IExtraAbility {
    private static final int DEFAULT_SCC = 4;
    private static final int HIGH_SCC = 6;
    private static final int MID_SE = 10000;
    private static final int HIGH_SE = 16000;
    private static final int HIGHER_SE = 25000;
    private static final int HIGHEST_SE = 60000;
    public static final Marker MARKER = MarkerManager.getMarker("ExtraAbilities");
    public static final IExtraAbility ARMOR_PIERCER = create(prefix(ExaNames.ARMOR_PIERCER), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility BOW_EXPERT = create(prefix(ExaNames.BOW_EXPERT), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility CHEMIST = create(prefix(ExaNames.CHEMIST), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility CLIMBER = create(prefix(ExaNames.CLIMBER), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility DESTROYER = create(prefix(ExaNames.DESTROYER), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility DRAGON_WIZARD = createSpecial(prefix(ExaNames.DRAGON_WIZARD), HIGH_SCC, HIGHEST_SE);
    public static final IExtraAbility ENHANCED_AUTO_JUMP = create(prefix(ExaNames.ENHANCED_AUTO_JUMP), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility ESCAPER = create(prefix(ExaNames.ESCAPER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility EXPERIENCER = create(prefix(ExaNames.EXPERIENCER), HIGH_SCC, HIGHER_SE);
    public static final IExtraAbility EXPLOSION_MASTER = create(prefix(ExaNames.EXPLOSION_MASTER), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility FALLING_BUFFER = create(prefix(ExaNames.FALLING_BUFFER), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility FANGS_SUMMONER = createSpecial(prefix(ExaNames.FANGS_SUMMONER), HIGH_SCC, HIGHER_SE);
    public static final IExtraAbility FAVORED_TRADER = create(prefix(ExaNames.FAVORED_TRADER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility FIRE_RESISTANCE = createSpecial(prefix(ExaNames.FIRE_RESISTANCE), HIGH_SCC, HIGHER_SE);
    public static final IExtraAbility FIRE_WALKER = create(prefix(ExaNames.FIRE_WALKER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility FLYER = createSpecial(prefix(ExaNames.FLYER), HIGH_SCC, HIGHEST_SE);
    public static final IExtraAbility FROST_RESISTANCE = create(prefix(ExaNames.FROST_RESISTANCE), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility GOLD_PREFERENCE = create(prefix(ExaNames.GOLD_PREFERENCE), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility IMITATOR = create(prefix(ExaNames.IMITATOR), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility INTIMIDATOR = createSpecial(prefix(ExaNames.INTIMIDATOR), HIGH_SCC, HIGHER_SE);
    public static final IExtraAbility INITIAL_ARMOR = create(prefix(ExaNames.INITIAL_ARMOR), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility MONSTER_SABOTAGE = createSpecial(prefix(ExaNames.MONSTER_SABOTAGE), HIGH_SCC, HIGHER_SE);
    public static final IExtraAbility MONSTER_VIEW = create(prefix(ExaNames.MONSTER_VIEW), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility NETHERMAN = create(prefix(ExaNames.NETHERMAN), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility NUTRITIONIST = create(prefix(ExaNames.NUTRITIONIST), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility OVERDRIVE = create(prefix(ExaNames.OVERDRIVE), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility PERMANENT_SLOWDOWN = create(prefix(ExaNames.PERMANENT_SLOWDOWN), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility PILLAGER = create(prefix(ExaNames.PILLAGER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility POISONER = create(prefix(ExaNames.POISONER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility PURIFICATION = create(prefix(ExaNames.PURIFICATION), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility RESTORATION = create(prefix(ExaNames.RESTORATION), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility SLIME_POWER = create(prefix(ExaNames.SLIME_POWER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility SOUL_INVULNERABILITY = create(prefix(ExaNames.SOUL_INVULNERABILITY), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility SPEEDUP = create(prefix(ExaNames.SPEEDUP), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility STATIC_DEFENDER = create(prefix(ExaNames.STATIC_DEFENDER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility SWIMMER = create(prefix(ExaNames.SWIMMER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility TELEPORTATION = create(prefix(ExaNames.TELEPORTATION), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility THORNS_MASTER = create(prefix(ExaNames.THORNS_MASTER), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility TRANSFORMATION = create(prefix(ExaNames.TRANSFORMATION), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility ULTRAREACH = create(prefix(ExaNames.ULTRAREACH), DEFAULT_SCC, MID_SE);
    public static final IExtraAbility WATER_BREATHING = create(prefix(ExaNames.WATER_BREATHING), HIGH_SCC, HIGH_SE);
    public static final IExtraAbility WITHER_REACH = createSpecial(prefix(ExaNames.WITHER_REACH), HIGH_SCC, HIGHEST_SE);

    private static final Map<ResourceLocation, IExtraAbility> ABILITIES = new HashMap<>(64);
    private static final Map<EntityType<?>, IExtraAbility> ENTITY_TO_EXA_MAP = new HashMap<>(64);
    @NotNull
    private final ResourceLocation registryName;
    private final int containerCost;
    private final int energyCost;
    private final boolean special;

    private ExtraAbility(ResourceLocation registryName, int containerCost, int energyCost, boolean special) {
        this.registryName = registryName;
        this.containerCost = containerCost;
        this.energyCost = energyCost;
        this.special = special;
    }

    static {
        register(ARMOR_PIERCER, ArmorPiercerBuff.INSTANCE, EntityType.VINDICATOR);
        register(BOW_EXPERT, EntityType.SKELETON);
        register(CHEMIST, EntityType.WITCH);
        register(CLIMBER, EntityType.SPIDER);
        register(DESTROYER, EntityType.WOLF);
        register(DRAGON_WIZARD, EntityType.ENDER_DRAGON);
        register(ENHANCED_AUTO_JUMP, EntityType.RABBIT);
        register(ESCAPER, EscaperBuff.INSTANCE, EntityType.SQUID);
        register(EXPERIENCER, ExperiencerBuff.INSTANCE, ModEntities.ENCHANTER);
        register(EXPLOSION_MASTER, ExplosionMasterBuff.INSTANCE, EntityType.CREEPER);
        register(FALLING_BUFFER, EntityType.CAT, EntityType.OCELOT, EntityType.CHICKEN, EntityType.GHAST);
        register(FANGS_SUMMONER, EntityType.EVOKER);
        register(FAVORED_TRADER, EntityType.WANDERING_TRADER);
        register(FIRE_RESISTANCE, FireResistanceBuff.INSTANCE, ModEntities.ENERGIZED_BLAZE);
        register(FIRE_WALKER, EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.STRIDER);
        register(FLYER, ModEntities.SOUL_DRAGON);
        register(FROST_RESISTANCE, FrostResistanceBuff.INSTANCE, FrostResistanceDebuff.INSTANCE, EntityType.POLAR_BEAR);
        register(GOLD_PREFERENCE, GoldPreferenceBuff.INSTANCE, EntityType.PIGLIN);
        register(IMITATOR, EntityType.PARROT);
        register(INITIAL_ARMOR, InitialArmorBuff.INSTANCE, EntityType.ZOMBIE, EntityType.SILVERFISH);
        register(INTIMIDATOR, ModEntities.SOUL_SKELETON_KING);
        register(MONSTER_SABOTAGE, MonsterSabotageDebuff.INSTANCE, EntityType.ELDER_GUARDIAN, EntityType.RAVAGER);
        register(MONSTER_VIEW, MonsterViewBuff.INSTANCE, EntityType.BAT, EntityType.PHANTOM);
        register(NETHERMAN, NethermanBuff.INSTANCE, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN);
        register(NUTRITIONIST, EntityType.PIG);
        register(OVERDRIVE, EntityType.HUSK);
        register(PERMANENT_SLOWDOWN, PermanentSlowdownBuff.INSTANCE, EntityType.STRAY);
        register(PILLAGER, EntityType.PILLAGER);
        register(POISONER, PoisonerBuff.INSTANCE, EntityType.BEE, EntityType.CAVE_SPIDER);
        register(PURIFICATION, PurificationBuff.INSTANCE, EntityType.COW, EntityType.MOOSHROOM);
        register(RESTORATION, RestorationBuff.INSTANCE, EntityType.SHEEP);
        register(SLIME_POWER, SlimePowerBuff.INSTANCE, EntityType.SLIME);
        register(SOUL_INVULNERABILITY, ModEntities.SOUL_SKELETON, ModEntities.SOUL_RABBIT, ModEntities.WANDERER);
        register(SPEEDUP, SpeedupBuff.INSTANCE, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE);
        register(STATIC_DEFENDER, StaticDefenderBuff.INSTANCE, EntityType.SHULKER, EntityType.TURTLE);
        register(SWIMMER, SwimmerBuff.INSTANCE, EntityType.DOLPHIN, EntityType.DROWNED);
        register(TELEPORTATION, EntityType.ENDERMAN);
        register(THORNS_MASTER, EntityType.GUARDIAN, EntityType.PUFFERFISH);
        register(TRANSFORMATION, TransformationBuff.INSTANCE, EntityType.WITHER_SKELETON);
        register(ULTRAREACH, UltrareachBuff.INSTANCE, EntityType.FOX);
        register(WATER_BREATHING, WaterBreathingBuff.INSTANCE, EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH);
        register(WITHER_REACH, WitherReachBuff.INSTANCE, EntityType.WITHER);
    }

    @Nullable
    public static IExtraAbility get(@Nullable ResourceLocation registryName) {
        if (registryName == null) {
            return null;
        }
        return ABILITIES.get(registryName);
    }

    public static Optional<IExtraAbility> getOptional(@Nullable ResourceLocation registryName) {
        return Optional.ofNullable(get(registryName));
    }

    @SuppressWarnings("unused")
    @Nullable
    public static IExtraAbility byEntity(EntityType<?> type) {
        return ENTITY_TO_EXA_MAP.get(type);
    }

    @SuppressWarnings("unused")
    @Nullable
    public static IExtraAbility byReinforcement(Reinforcement reinforcement) {
        return ENTITY_TO_EXA_MAP.get(reinforcement.getType());
    }

    public static IExtraAbility createSpecial(ResourceLocation registryName, int containerCost, int energyCost) {
        return create(registryName, containerCost, energyCost, true);
    }

    public static IExtraAbility create(ResourceLocation registryName, int containerCost, int energyCost) {
        return create(registryName, containerCost, energyCost, false);
    }

    public static IExtraAbility create(ResourceLocation registryName, int containerCost, int energyCost, boolean special) {
        if (containerCost > 10) {
            throw new IllegalArgumentException("Too high container cost: " + containerCost);
        }
        return new ExtraAbility(registryName, containerCost, energyCost, special);
    }

    public static void register(IExtraAbility exa) {
        Objects.requireNonNull(exa, "Extra Ability should be non-null");
        if (exa.isDummy()) {
            return;
        }
        Objects.requireNonNull(exa.getRegistryName(), "Registry name should be non-null");
        Preconditions.checkState(ABILITIES.put(exa.getRegistryName(), exa) == null, "Duplicate registry name: " + exa.getRegistryName());
    }

    public static void register(IExtraAbility exa, EntityType<?>... types) {
        register(exa);
        Arrays.stream(types).distinct().forEach(type -> ENTITY_TO_EXA_MAP.put(type, exa));
    }

    public static void register(IExtraAbility exa, PlayerBuff buff, EntityType<?>... types) {
        register(exa, types);
        PlayerBuffMap.bind(exa, buff);
    }

    public static void register(IExtraAbility exa, MobDebuff debuff, EntityType<?>... types) {
        register(exa, types);
        MobDebuffMap.bind(exa, debuff);
    }

    public static void register(IExtraAbility exa, PlayerBuff buff, MobDebuff debuff, EntityType<?>... types) {
        register(exa, types);
        PlayerBuffMap.bind(exa, buff);
        MobDebuffMap.bind(exa, debuff);
    }

    public static ImmutableMap<ResourceLocation, IExtraAbility> getRegisteredExtraAbilities() {
        return ImmutableMap.copyOf(ABILITIES);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public boolean isOn(PlayerEntity player) {
        return ((IPlayerEntityMixin) player).hasExtraAbility(this);
    }

    @Override
    public boolean addTo(PlayerEntity player) {
        boolean added = ((IPlayerEntityMixin) player).addExtraAbility(this);
        if (added) {
            PlayerBuffMap.getBuff(this).ifPresent(buff -> buff.startApplyingTo(player, player.level));
            MobDebuffMap.getDebuff(this).ifPresent(debuff -> debuff.startApplyingTo(player, player.level));
        }
        return added;
    }

    @Override
    public boolean removeFrom(PlayerEntity player) {
        boolean removed = ((IPlayerEntityMixin) player).removeExtraAbility(this);
        if (removed) {
            PlayerBuffMap.getBuff(this).ifPresent(buff -> buff.stopApplyingTo(player, player.level));
            MobDebuffMap.getDebuff(this).ifPresent(debuff -> debuff.stopApplyingTo(player, player.level));
        }
        return removed;
    }

    @Override
    public void reload(@Nullable PlayerEntity oldPlayer, PlayerEntity newPlayer) {
        PlayerBuffMap.getBuff(this).ifPresent(buff -> buff.reload(oldPlayer, newPlayer));
        MobDebuffMap.getDebuff(this).ifPresent(debuff -> debuff.reload(oldPlayer, newPlayer));
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(makeDescriptionId());
    }

    @Override
    public String makeDescriptionId() {
        return Util.makeDescriptionId("exa", getRegistryName());
    }

    @Override
    public int getSoulContainerCost() {
        return containerCost;
    }

    @Override
    public int getSECost() {
        return energyCost;
    }

    @Override
    public boolean isSpecial() {
        return special;
    }

    @Override
    public TextFormatting getStyle() {
        return isSpecial() ? TextFormatting.DARK_PURPLE : TextFormatting.BLUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraAbility that = (ExtraAbility) o;
        return getRegistryName().equals(that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRegistryName());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("registryName", registryName)
                .add("containerCost", containerCost)
                .add("energyCost", energyCost)
                .add("special", special)
                .toString();
    }
}
