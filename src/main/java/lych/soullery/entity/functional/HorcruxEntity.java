package lych.soullery.entity.functional;

import lych.soullery.Soullery;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ai.goal.FollowOwnerGoal;
import lych.soullery.entity.ai.goal.wrapper.Goals;
import lych.soullery.entity.iface.IHasPlayerOwner;
import lych.soullery.extension.highlight.EntityHighlightManager;
import lych.soullery.extension.highlight.HighlighterType;
import lych.soullery.item.HorcruxCarrierItem;
import lych.soullery.item.ModItems;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.Utils;
import lych.soullery.world.gen.dimension.ModDimensions;
import lych.soullery.world.teleporter.DirectTeleporter;
import lych.soullery.world.teleporter.SoulLandTeleporter;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HorcruxEntity extends CreatureEntity implements IHasPlayerOwner {
    private static final String FOLLOWING_PLAYER = Soullery.prefixMsg("horcrux", "following_player");
    private static final String WARN_PLAYER = Soullery.prefixMsg("horcrux", "warn_player");
    private static final String POSITION_CHANGED = Soullery.prefixMsg("horcrux", "teleport_back_position_changed");
    private static final String POSITION_CHANGED_AMOUNT = Soullery.prefixMsg("horcrux", "teleport_back_position_changed_amount");
    private static final String INVALID_DIMENSION = Soullery.prefixMsg("horcrux", "invalid_dimension");
    @Nullable
    private GlobalPos home;
    @Nullable
    private UUID ownerUUID;
    private boolean followingPlayer;
    private boolean warningShown;
    private int hurtTicks;

    public HorcruxEntity(EntityType<? extends CreatureEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0, new SwimGoal(this));
        goalSelector.addGoal(3, Goals.of(new FollowOwnerGoal<>(this, 1, 4, 2, 16, true, false)).executeIf(() -> followingPlayer).get());
        goalSelector.addGoal(6, new LookRandomlyGoal(this));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.KNOCKBACK_RESISTANCE, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.24);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.PLAYER_BREATH;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    public void tick() {
        super.tick();
        if (hurtTicks > 0) {
            hurtTicks--;
        }
        if (!level.isClientSide()) {
            EntityHighlightManager.get((ServerWorld) level).highlight(HighlighterType.HORCRUX, this);
            if (tickCount % 10 == 0) {
                if (level.dimension() == ModDimensions.SOUL_LAND && getOwner() != null && !followingPlayer && distanceToSqr(getOwner()) <= 4 * 4) {
                    getOwner().addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 40, 2, false, false, true));
                }
            }
        }
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if (getOwner() == null) {
            setOwner(player);
            return ActionResultType.sidedSuccess(level.isClientSide());
        }

        if (isOwnedBy(player)) {
            if (level.isClientSide()) {
                return ActionResultType.SUCCESS;
            }
            if (player.isShiftKeyDown()) {
                followingPlayer = !followingPlayer;
                if (followingPlayer && !warningShown) {
                    player.sendMessage(new TranslationTextComponent(FOLLOWING_PLAYER).append(new StringTextComponent(Boolean.toString(true)).withStyle(TextFormatting.GREEN)).append(new TranslationTextComponent(WARN_PLAYER).withStyle(TextFormatting.RED)), Util.NIL_UUID);
                    warningShown = true;
                } else {
                    player.sendMessage(new TranslationTextComponent(FOLLOWING_PLAYER).append(new StringTextComponent(Boolean.toString(followingPlayer)).withStyle(followingPlayer ? TextFormatting.GREEN : TextFormatting.RED)), Util.NIL_UUID);
                }
                return ActionResultType.CONSUME;
            }
            if (followingPlayer) {
                return ActionResultType.PASS;
            }
            RegistryKey<World> src = level.dimension();
            if (src != World.OVERWORLD && src != ModDimensions.SOUL_LAND) {
                player.sendMessage(new TranslationTextComponent(INVALID_DIMENSION, getDisplayName()).withStyle(TextFormatting.RED), Util.NIL_UUID);
                return ActionResultType.CONSUME;
            }
            RegistryKey<World> dest = src == World.OVERWORLD ? ModDimensions.SOUL_LAND : World.OVERWORLD;
            ServerWorld level = player.getServer().getLevel(dest);
            BlockPos destPos = Utils.applyIfNonnull(getHome(), h -> h.dimension() == dest ? h.pos() : null);
            GlobalPos home = GlobalPos.of(this.level.dimension(), blockPosition());
            SoulLandTeleporter teleporter = new SoulLandTeleporter(destPos);
            player.changeDimension(level, teleporter);
            if (destPos != null && teleporter.isPositionChanged()) {
                double distance = Math.sqrt(player.distanceToSqr(Vector3d.atBottomCenterOf(destPos)));
                if (distance > 10) {
                    player.sendMessage(new TranslationTextComponent(POSITION_CHANGED).withStyle(TextFormatting.RED), Util.NIL_UUID);
                    player.sendMessage(new TranslationTextComponent(POSITION_CHANGED_AMOUNT, Math.round(distance)), Util.NIL_UUID);
                }
            }
            HorcruxEntity newHorcrux = (HorcruxEntity) changeDimension(level, new DirectTeleporter(player.position()));
            newHorcrux.setHome(home);
            newHorcrux.warningShown = warningShown;
            return ActionResultType.CONSUME;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof PlayerEntity && isOwnedBy((PlayerEntity) source.getEntity())) {
            if (hurtTicks > 0) {
                remove();
                ItemStack stack = new ItemStack(ModItems.HORCRUX_CARRIER);
                if (getOwner() != null) {
                    HorcruxCarrierItem.setOwner(stack, getOwner().getUUID());
                }
                EntityUtils.spawnItem(level, blockPosition(), stack);
                return true;
            }
            hurtTicks = ConfigHelper.getMaxHorcruxHurtTicks();
        }
        markHurt();
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    public GlobalPos getHome() {
        return home;
    }

    public void setHome(@Nullable GlobalPos home) {
        this.home = home;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        if (getHome() != null) {
            compoundNBT.putString("HomeDimension", getHome().dimension().location().toString());
            compoundNBT.put("HomePosition", NBTUtil.writeBlockPos(getHome().pos()));
        }
        saveOwner(compoundNBT);
        compoundNBT.putBoolean("FollowingPlayer", followingPlayer);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("HomeDimension") && compoundNBT.contains("HomePosition")) {
            RegistryKey<World> homeDimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compoundNBT.getString("HomeDimension")));
            setHome(GlobalPos.of(homeDimension, NBTUtil.readBlockPos(compoundNBT.getCompound("HomePosition"))));
        }
        loadOwner(compoundNBT);
        followingPlayer = compoundNBT.getBoolean("FollowingPlayer");
    }

    @Override
    public void kill() {
        remove();
    }
}
