package lych.soullery.entity.monster.boss.enchanter;

import lych.soullery.entity.ModEntities;
import lych.soullery.extension.fire.Fires;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.Utils;
import lych.soullery.util.WorldUtils;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EASTypes {
    private static final Map<ResourceLocation, EASType> MAP = new HashMap<>();
    private static EASType[] BY_ID = new EASType[0];
    public static final EASType OBSIDIAN = create(Blocks.OBSIDIAN, new EASConsumer() {
        @Override
        public void startApplyingTo(EnchantedArmorStandEntity eas) {
            EntityUtils.getAttribute(eas, Attributes.MAX_HEALTH).setBaseValue(50);
            EntityUtils.getAttribute(eas, Attributes.ATTACK_DAMAGE).setBaseValue(3);
            EntityUtils.getAttribute(eas, Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
            eas.setHealth(eas.getMaxHealth());
        }

        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {}

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType WOOD = create(Blocks.OAK_LOG, new EASConsumer() {
        @Override
        public void startApplyingTo(EnchantedArmorStandEntity eas) {
            EntityUtils.getAttribute(eas, Attributes.ARMOR).setBaseValue(8);
            EntityUtils.getAttribute(eas, Attributes.ARMOR_TOUGHNESS).setBaseValue(20);
            defaultAttributesUpgrade(eas);
        }

        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {}

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType SLIME = create(Blocks.SLIME_BLOCK, new EASConsumer() {
        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {
            for (Hand hand : Hand.values()) {
                if (eas.carry(target.getItemInHand(hand))) {
                    target.setItemInHand(hand, ItemStack.EMPTY);
                    break;
                }
            }
        }

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType DISPENSER = create(Blocks.DISPENSER, new EASConsumer() {
        @Override
        public void startApplyingTo(EnchantedArmorStandEntity eas) {
            defaultAttributesUpgrade(eas);
            eas.setRangedAttack(true);
        }

        @Override
        public void stopApplyingTo(EnchantedArmorStandEntity eas) {
            resetDefaultAttributes(eas);
            eas.setRangedAttack(false);
        }

        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {}

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType TNT = create(Blocks.TNT, false, new EASConsumer() {
        @Override
        public void startApplyingTo(EnchantedArmorStandEntity eas) {
            EntityUtils.getAttribute(eas, Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
        }

        @Override
        public void stopApplyingTo(EnchantedArmorStandEntity eas) {
            EntityUtils.getAttribute(eas, Attributes.MOVEMENT_SPEED).setBaseValue(0.24);
        }

        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {}

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}

        @Override
        public void onEASDie(EnchantedArmorStandEntity eas, DamageSource source) {
            if (!eas.level.isClientSide()) {
                WorldUtils.makeFakeExplosionServerside(eas.blockPosition(), (ServerWorld) eas.level);
                eas.level.getEntitiesOfClass(Entity.class, eas.getBoundingBox().inflate(5, 3, 5))
                        .stream()
                        .filter(entity -> !(entity instanceof ItemEntity))
                        .filter(entity -> entity.distanceToSqr(eas) <= 5 * 5)
                        .forEach(entity -> entity.hurt(DamageSource.explosion(eas), 30 / (entity.distanceTo(eas) + 1)));
            }
            eas.remove();
        }
    });
    public static final EASType ICE = create(Blocks.PACKED_ICE, new EASConsumer() {
        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {
            target.addEffect(new EffectInstance(Utils.FROSTED.get(), 20 * 12, 1));
        }

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType MYCELIUM = create(Blocks.MYCELIUM, new EASConsumer() {
        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {
            target.addEffect(new EffectInstance(Effects.POISON, 20 * 8, 0));
        }

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType MAGMA = create(Blocks.MAGMA_BLOCK, new EASConsumer() {
        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {
            target.setSecondsOnFire(5);
            ((IEntityMixin) target).setFireOnSelf(Fires.FIRE);
        }

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType CACTUS = create(Blocks.CACTUS, new EASConsumer() {
        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {}

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {
            if (source.getEntity() != null && !(source.getEntity() instanceof EnchantedArmorStandEntity)) {
                source.getEntity().hurt(DamageSource.thorns(eas), Math.min(10, amount * 0.5f));
            }
        }
    });
    public static final EASType MUSHROOM = create(Blocks.RED_MUSHROOM_BLOCK, new EASConsumer() {
        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {
            target.addEffect(new EffectInstance(Effects.HUNGER, 20 * 20, 1));
        }

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });
    public static final EASType PISTON = create(Blocks.PISTON, new EASConsumer() {
        @Override


        public void startApplyingTo(EnchantedArmorStandEntity eas) {
            EntityUtils.getAttribute(eas, Attributes.ATTACK_KNOCKBACK).setBaseValue(5);
            defaultAttributesUpgrade(eas);
        }

        @Override
        public void onEASAttack(EnchantedArmorStandEntity eas, LivingEntity target) {}

        @Override
        public void onEASHurt(EnchantedArmorStandEntity eas, DamageSource source, float amount) {}
    });

    public static void defaultAttributesUpgrade(EnchantedArmorStandEntity eas) {
        EntityUtils.getAttribute(eas, Attributes.MAX_HEALTH).setBaseValue(25);
        EntityUtils.getAttribute(eas, Attributes.ATTACK_DAMAGE).setBaseValue(3);
        eas.setHealth(eas.getMaxHealth());
    }

    public static void resetDefaultAttributes(EnchantedArmorStandEntity eas) {
        EntityUtils.getAttribute(eas, Attributes.MAX_HEALTH).setBaseValue(EnchantedArmorStandEntity.HEALTH);
        EntityUtils.getAttribute(eas, Attributes.ATTACK_DAMAGE).setBaseValue(EnchantedArmorStandEntity.DAMAGE);
        eas.setHealth(eas.getMaxHealth());
    }

    private static EASType create(Block block, EASConsumer consumer) {
        return create(block, true, consumer);
    }

    private static EASType create(Block block, boolean hasDrop, EASConsumer consumer) {
        return register(new EASType(block, consumer, hasDrop));
    }

    public static EASType register(EASType type) {
        Objects.requireNonNull(type);
        MAP.put(type.getName(), type);
        type.id = BY_ID.length;
        BY_ID = Arrays.copyOf(BY_ID, BY_ID.length + 1);
        BY_ID[BY_ID.length - 1] = type;
        return type;
    }

    @Nullable
    public static EASType get(ResourceLocation name) {
        return MAP.get(name);
    }

    @Nullable
    public static EASType byId(int id) {
        if (id < 0 || id >= BY_ID.length) {
            return null;
        }
        return BY_ID[id];
    }

    @VisibleForTesting
    public static int size() {
        return BY_ID.length;
    }

    public static EnchantedArmorStandEntity summonTyped(EASType type, Entity entity) {
        if (entity.level.isClientSide()) {
            throw new UnsupportedOperationException();
        }
        EnchantedArmorStandEntity eas = ModEntities.ENCHANTED_ARMOR_STAND.create(entity.level);
        eas.setSpecialType(type);
        eas.moveTo(entity.position());
        eas.finalizeSpawn((IServerWorld) entity.level, entity.level.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
        entity.level.addFreshEntity(eas);
        return eas;
    }
}
