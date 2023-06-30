package lych.soullery.entity.monster.boss.enchanter;

import com.google.common.base.MoreObjects;
import lych.soullery.Soullery;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public final class EASType implements EASConsumer {
    private final ResourceLocation name;
    private final Block representation;
    private final EASConsumer consumer;
    private final boolean hasDrop;
    int id = -1;

    EASType(Block representation, EASConsumer consumer, boolean hasDrop) {
        this(Soullery.prefix(representation.getRegistryName().getPath()), representation, consumer, hasDrop);
    }

    public EASType(ResourceLocation name, Block representation, EASConsumer consumer, boolean hasDrop) {
        this.name = name;
        this.representation = representation;
        this.consumer = consumer;
        this.hasDrop = hasDrop;
    }
/*
/summon soullery:eas -368.37 69.00 187.37 {Brain: {memories: {}}, HurtByTimestamp: 322, Attributes: [{Base: 0.0d, Name: "minecraft:generic.knockback_resistance"}, {Base: 2.0d, Name: "minecraft:generic.attack_damage"}, {Base: 5.0d, Name: "minecraft:generic.max_health"}, {Base: 0.08d, Name: "forge:entity_gravity"}, {Base: 0.24d, Name: "minecraft:generic.movement_speed"}], Invulnerable: 0b, FallFlying: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f, FallDistance: 0.0f, SheepReinforcementTickCount: 350L, CanUpdate: 1b, DeathTime: 0s, ForgeCaps: {"soullery:controlled_mob": {Controllers: [], Timer: {TickCount: 33, TimeLimits: [{Type: "soullery:default_mo", EndTime: 1200, StartTime: 0}]}}, "twilightforest:cap_shield": {permshields: 0, tempshields: 0}}, HandDropChances: [0.085f, 0.085f], PersistenceRequired: 0b, SheepReinforcementLastHurtByTimestamp: 0L, RangedAttack: 0b, Motion: [0.0d, -0.0784000015258789d, 0.0d], Health: 1.0f, LeftHanded: 0b, Air: 300s, OnGround: 1b, Rotation: [46.002808f, 2.2593606f], HandItems: [{}, {}], ArmorDropChances: [0.085f, 0.085f, 0.085f, 0.085f], EntityFireType: "minecraft:air", Fire: -1s, ArmorItems: [{}, {}, {}, {}], CanPickUpLoot: 0b, ItemsCarried: [], HurtTime: 0s}
/summon soullery:eas -368.94 69.00 187.91 {Brain: {memories: {}}, HurtByTimestamp: 0, Attributes: [{Base: 5.0d, Name: "minecraft:generic.max_health"}, {Base: 0.08d, Name: "forge:entity_gravity"}, {Base: 0.24d, Name: "minecraft:generic.movement_speed"}], Invulnerable: 0b, FallFlying: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f, FallDistance: 0.0f, SheepReinforcementTickCount: 135L, CanUpdate: 1b, DeathTime: 0s, ForgeCaps: {"soullery:controlled_mob": {Controllers: [], Timer: {TickCount: 0, TimeLimits: []}}, "twilightforest:cap_shield": {permshields: 0, tempshields: 0}}, HandDropChances: [0.085f, 0.085f], PersistenceRequired: 0b, SheepReinforcementLastHurtByTimestamp: 0L, RangedAttack: 0b, Motion: [0.0d, -0.0784000015258789d, 0.0d], Health: 5.0f, LeftHanded: 0b, Air: 300s, OnGround: 1b, Rotation: [126.3344f, 0.0f], HandItems: [{}, {}], ArmorDropChances: [0.085f, 0.085f, 0.085f, 0.085f], EntityFireType: "minecraft:air", Fire: -1s, ArmorItems: [{}, {}, {}, {}], CanPickUpLoot: 0b, ItemsCarried: [], HurtTime: 0s}
 */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("representation", representation)
                .toString();
    }

    @Override
    public void startApplyingTo(EnchantedArmorStandEntity eas) {
        consumer.startApplyingTo(eas);
    }

    @Override
    public void stopApplyingTo(EnchantedArmorStandEntity eas) {
        consumer.stopApplyingTo(eas);
    }

    @Override
    public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {
        consumer.onEASAttack(eas, target);
    }

    @Override
    public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {
        consumer.onEASHurt(eas, source, amount);
    }

    @Override
    public void onEASDie(EnchantedArmorStandEntity eas, DamageSource source) {
        consumer.onEASDie(eas, source);
    }

    public ResourceLocation getName() {
        return name;
    }

    public Block getRepresentation() {
        return representation;
    }

    public int getId() {
        return id;
    }

    public boolean hasDrop() {
        return hasDrop;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EASType easType = (EASType) o;
        return Objects.equals(name, easType.name) && Objects.equals(representation, easType.representation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, representation);
    }
}
