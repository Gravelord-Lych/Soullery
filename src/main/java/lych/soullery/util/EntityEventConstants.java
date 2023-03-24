package lych.soullery.util;

import net.minecraft.entity.Entity;

/**
 * Entity event constants.<br>
 * {@link net.minecraft.world.World#broadcastEntityEvent(Entity, byte)}<br>
 * {@link Entity#handleEntityEvent(byte)}
 */
@SuppressWarnings("unused")
public final class EntityEventConstants {
    public static final byte RABBIT_JUMP = 1;
    public static final byte SPAWNER_DELAY = 1;
    public static final byte ENTITY_DAMAGED_COMMONLY = 2;
    public static final byte THROWABLE_HIT = 3;
    public static final byte ENTITY_DIE = 3;
    public static final byte ENTITY_ATTACK = 4;
    public static final byte TAME_FAILURE = 6;
    public static final byte TAME_SUCCESS = 7;
    public static final byte PLAYER_USED_ITEM = 9;
    public static final byte TNT_MINECART_PRIMED = 10;
    public static final byte IRON_GOLEM_START_OFFERING_FLOWER = 11;
    public static final byte VILLAGER_LOVE = 12;
    public static final byte VILLAGER_BECOME_ANGRY = 13;
    public static final byte VILLAGER_BECOME_HAPPY = 14;
    public static final byte FIREWORK_SET_OFF = 17;
    public static final byte BABY_WILL_BE_SPAWNED = 18;
    public static final byte SQUID_RESET_TENTACLE_MOVEMENT = 19;
    public static final byte MOB_SPAWN = 20;
    public static final byte GUARDIAN_ATTACK = 21;
    public static final byte PERMISSION_LEVEL_0 = 24;
    public static final byte PERMISSION_LEVEL_1 = 25;
    public static final byte PERMISSION_LEVEL_2 = 26;
    public static final byte PERMISSION_LEVEL_3 = 27;
    public static final byte PERMISSION_LEVEL_4 = 28;
    public static final byte SHIELD_BLOCK_DAMAGE = 29;
    public static final byte SHIELD_BREAK = 30; // The event is fired via a shield is disabled.
    public static final byte ARMOR_STAND_HIT = 32;
    public static final byte THORNS_ENCHANTMENT_CAUSE_DAMAGE = 33;
    public static final byte IRON_GOLEM_STOP_OFFERING_FLOWER = 34;
    public static final byte TOTEM_OF_UNDYING_WORK = 35;
    public static final byte DROWN_DAMAGE_ENTITY = 36;
    public static final byte FIRE_DAMAGE_ENTITY = 37;
    public static final byte DOLPHIN_SWIM_TO_TREASURE = 38;
    public static final byte RAVAGER_STUN_TARGET = 39;
    public static final byte OCELOT_DOES_NOT_TRUST_PLAYER = 40;
    public static final byte OCELOT_TRUST_PLAYER = 41;
    public static final byte VILLAGER_FACE_RAID = 42;
    public static final byte PLAYER_JOIN_RAID = 43;
    public static final byte SWEET_BERRY_BUSH_DAMAGE_ENTITY = 44;
    public static final byte FOX_EAT = 45;
    public static final byte ENDER_TELEPORT = 46;
    public static final byte MAINHAND_EQUIPMENT_BREAK = 47;
    public static final byte OFFHAND_EQUIPMENT_BREAK = 48;
    public static final byte HEAD_EQUIPMENT_BREAK = 49;
    public static final byte CHEST_EQUIPMENT_BREAK = 50;
    public static final byte LEGS_EQUIPMENT_BREAK = 51;
    public static final byte FEET_EQUIPMENT_BREAK = 52;
    public static final byte HONEY_BLOCK_SHOW_SLIDE_PARTICLES = 53;
    public static final byte HONEY_BLOCK_SHOW_JUMP_PARTICLES = 54;
    public static final byte SWAP_HAND_ITEMS = 55;
    public static final byte WOLF_CANCEL_SHAKE = 56;

    private EntityEventConstants() {}

    /**
     * Mod entity event constants.
     */
    public static final class Mod {
        private Mod() {}
    }
}
