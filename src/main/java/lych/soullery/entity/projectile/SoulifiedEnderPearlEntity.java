package lych.soullery.entity.projectile;

import lych.soullery.Soullery;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.ModEntityNames;
import lych.soullery.entity.monster.IPurifiable;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class SoulifiedEnderPearlEntity extends EnderPearlEntity implements IPurifiable {
    private static final double REMOVE_DISTANCE_IF_UNSTABLE = 200;
    private static final DataParameter<Boolean> DATA_PURE = EntityDataManager.defineId(SoulifiedEnderPearlEntity.class, DataSerializers.BOOLEAN);
    private Gravity gravity;

    public SoulifiedEnderPearlEntity(EntityType<? extends SoulifiedEnderPearlEntity> type, World world) {
        super(type, world);
    }

    public SoulifiedEnderPearlEntity(World world, LivingEntity owner) {
        this(ModEntities.SOULIFIED_ENDER_PEARL, world, owner);
    }

    public SoulifiedEnderPearlEntity(EntityType<? extends SoulifiedEnderPearlEntity> type, World world, LivingEntity owner) {
        this(type, world);
        setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
        setOwner(owner);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PURE, false);
    }

    @Override
    public void tick() {
        if (gravity.isUnstable() && EntityUtils.isAlive(getOwner()) && farAway()) {
            remove();
        } else {
            super.tick();
        }
    }

    private boolean farAway() {
        return getHorizontalDistanceSqr(getOwner().position().vectorTo(position())) >= REMOVE_DISTANCE_IF_UNSTABLE * REMOVE_DISTANCE_IF_UNSTABLE;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.AIR;
    }

    @Override
    public boolean isPurified() {
        return entityData.get(DATA_PURE);
    }

    @Override
    public void setPurified(boolean purified) {
        entityData.set(DATA_PURE, purified);
    }

    @Override
    protected float getGravity() {
        return gravity.getValue();
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    public enum Gravity implements IIdentifiableEnum {
        NORMAL("normal", 0x17E6C3, 0.03f, false),
        HIGH("high", 0x2EE699, 0.055f, false),
        LOW("low", 0x40FFFF, 0.02f, false),
        NO("zero", 0x59D6FF, 0, true),
        NEGATIVE("negative", 0x80BFFF, -0.03f, true);

        public static final ITextComponent GRAVITY = new TranslationTextComponent(Soullery.prefixMsg("entity", ModEntityNames.SOULIFIED_ENDER_PEARL + ".gravity"));
        private static final ITextComponent SET_GRAVITY = new TranslationTextComponent(Soullery.prefixMsg("entity", ModEntityNames.SOULIFIED_ENDER_PEARL + ".gravity.set"));
        private final String id;
        private final int color;
        private final float value;
        private final boolean unstable;

        Gravity(String id, int color, float value, boolean unstable) {
            this.id = id;
            this.color = color;
            this.value = value;
            this.unstable = unstable;
        }

        public static Gravity byId(int id) {
            try {
                return IIdentifiableEnum.byOrdinal(values(), id);
            } catch (EnumConstantNotFoundException e) {
                return NORMAL;
            }
        }

        public float getValue() {
            return value;
        }

        public boolean isUnstable() {
            return unstable;
        }

        public void sendSetGravityMessage(PlayerEntity player) {
            player.sendMessage(SET_GRAVITY.copy().append(makeText().withStyle(Style.EMPTY.withColor(Color.fromRgb(color)))), Util.NIL_UUID);
        }

        public IFormattableTextComponent makeText() {
            return new TranslationTextComponent(Soullery.prefixMsg("entity", ModEntityNames.SOULIFIED_ENDER_PEARL + ".gravity." + id));
        }

        public Gravity cycle() {
            if (this == NEGATIVE) {
                return NORMAL;
            }
            return byId(getId() + 1);
        }
    }
}
