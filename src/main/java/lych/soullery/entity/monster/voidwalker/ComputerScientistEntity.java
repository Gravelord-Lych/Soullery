package lych.soullery.entity.monster.voidwalker;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.ISharedShieldProvider;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.entity.ai.ComputerOperation;
import lych.soullery.entity.ai.goal.AdvancedVoidwalkerGoals.*;
import lych.soullery.entity.ai.goal.VoidwalkerGoals.VoidwalkerRandomWalkingGoal;
import lych.soullery.extension.shield.SharedShield;
import lych.soullery.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.*;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ComputerScientistEntity extends AbstractVoidLasererEntity<ComputerScientistEntity> implements ISharedShieldProvider {
    private final List<UUID> users = new ArrayList<>();
    private final Object2IntMap<ComputerOperation> abilityCooldowns = new Object2IntOpenHashMap<>();
    @Nullable
    private ISharedShield sharedShield;
    private int shieldCooldown;
    private int shieldLife;
    private int attackIntervalTicks;

    public ComputerScientistEntity(EntityType<? extends ComputerScientistEntity> type, World world) {
        super(type, world);
    }

    @Nullable
    @Override
    public ILaserProvider<? super ComputerScientistEntity> provideLaser() {
        return getOperation();
    }

    @Nullable
    public ComputerOperation getOperation() {
        return ComputerOperation.byId(entityData.get(DATA_LASER_ID));
    }

    public void setOperation(@Nullable ComputerOperation operation) {
        entityData.set(DATA_LASER_ID, operation == null ? -1 : operation.getId());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (shieldCooldown > 0) {
            shieldCooldown--;
        }
        if (shieldLife > 0) {
            shieldLife--;
        } else if (getSharedShield() != null) {
            EntityUtils.disableShield(level, this, null);
            getUsers().forEach(user -> user.setShieldProvider(null));
            users.clear();
        }
        if (getAttackIntervalTicks() > 0) {
            setAttackIntervalTicks(getAttackIntervalTicks() - 1);
        }
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 2)
                .add(Attributes.FOLLOW_RANGE, 19.2);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new ShieldSelfGoal(this));
        goalSelector.addGoal(3, new ShieldOthersGoal(this));
        goalSelector.addGoal(4, new ApplyMutationGoal(this));
        goalSelector.addGoal(5, new ShuffleInventoryGoal(this));
        goalSelector.addGoal(6, new RegularlyAttackGoal(this));
        goalSelector.addGoal(8, new ComputerScientistRetreatGoal(this, 200));
        goalSelector.addGoal(9, new VoidwalkerRandomWalkingGoal(this, 0.8));
        goalSelector.addGoal(10, new LookRandomlyGoal(this));
    }

    @Override
    protected void doStrengthenSelf(VoidwalkerTier tier, VoidwalkerTier oldTier, DifficultyInstance difficulty) {
        switch (tier) {
            case PARAGON:
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(80);
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.26);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(40);
                break;
            case ELITE:
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(55);
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.24);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(28.8);
                break;
            case EXTRAORDINARY:
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(30);
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(24);
                break;
            default:
        }
        setHealth(getMaxHealth());
    }

    @Nullable
    @Override
    public ISharedShield getSharedShield() {
        return sharedShield;
    }

    @Override
    public void setSharedShield(@Nullable ISharedShield sharedShield) {
        this.sharedShield = sharedShield;
        entityData.set(DATA_SHIELDED, sharedShield != null);
    }

    @Override
    public boolean isLowHealth(LivingEntity entity) {
        if (entity == this) {
            return entity.getHealth() < entity.getMaxHealth() * 0.5f;
        }
        return super.isLowHealth(entity);
    }

    @NotNull
    @Override
    public IShieldUser getShieldProvider() {
        return this;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        if (getSharedShield() != null) {
            compoundNBT.put("SharedShield", getSharedShield().save());
        }
        compoundNBT.putInt("ShieldCooldown", shieldCooldown);
        compoundNBT.putInt("ShieldLife", shieldLife);
        ListNBT usersNBT = new ListNBT();
        users.stream().map(NBTUtil::createUUID).forEach(usersNBT::add);
        compoundNBT.put("ShieldUsers", usersNBT);
        ListNBT cooldownsNBT = new ListNBT();
        for (Object2IntMap.Entry<ComputerOperation> entry : abilityCooldowns.object2IntEntrySet()) {
            ComputerOperation operation = entry.getKey();
            int cooldown = entry.getIntValue();
            CompoundNBT entryNBT = new CompoundNBT();
            entryNBT.putInt("OperationId", operation.getId());
            entryNBT.putInt("Cooldown", cooldown);
            cooldownsNBT.add(entryNBT);
        }
        compoundNBT.put("AbilityCooldowns", cooldownsNBT);
        compoundNBT.putInt("AttackIntervalTicks", getAttackIntervalTicks());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("SharedShield")) {
            setSharedShield(new SharedShield(compoundNBT.getCompound("SharedShield")));
        }
        shieldCooldown = compoundNBT.getInt("ShieldCooldown");
        shieldLife = compoundNBT.getInt("ShieldLife");
        if (compoundNBT.contains("ShieldUsers", Constants.NBT.TAG_LIST)) {
            users.clear();
            ListNBT usersNBT = compoundNBT.getList("ShieldUsers", Constants.NBT.TAG_INT_ARRAY);
            usersNBT.stream().map(NBTUtil::loadUUID).forEach(users::add);
        }
        if (compoundNBT.contains("AbilityCooldowns", Constants.NBT.TAG_LIST)) {
            abilityCooldowns.clear();
            ListNBT cooldownsNBT = compoundNBT.getList("AbilityCooldowns", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < cooldownsNBT.size(); i++) {
                CompoundNBT entryNBT = cooldownsNBT.getCompound(i);
                ComputerOperation operation = ComputerOperation.byId(entryNBT.getInt("OperationId"));
                int cooldown = entryNBT.getInt("Cooldown");
                abilityCooldowns.put(operation, cooldown);
            }
        }
        setAttackIntervalTicks(compoundNBT.getInt("AttackIntervalTicks"));
    }

    public ITextComponent formatSRGNameOperation(String srgName, Entity target, boolean attack) {
        if (level.isClientSide()) {
            throw new UnsupportedOperationException();
        }
        int targetId = target.getId();
        if (target instanceof PlayerEntity) {
            targetId = target.getUUID().hashCode();
        }
        targetId += ((ServerWorld) level).getSeed();
        String[] names = srgName.split("_");
        if (names.length != 3) {
            throw new IllegalArgumentException(String.format("%s is certainly not a SRG name", srgName));
        }
        int commandId = Integer.parseInt(names[1]);
        commandId += ((ServerWorld) level).getSeed();
        return formatWithId(Math.abs(commandId), Math.abs(targetId), attack);
    }

    private IFormattableTextComponent formatWithId(int commandId, int targetId, boolean attack) {
        return new StringTextComponent(String.format("<COMMAND ID %03d %s ID %03d>", commandId % 1000, attack ? "ATTACKS" : "DEFENDS", targetId % 1000))
                .withStyle(Style.EMPTY.withColor(Color.fromRgb(getAttackColor())).withBold(true));
    }

    public ITextComponent formatRetreatOperation() {
        return new StringTextComponent("<RETREAT>").withStyle(Style.EMPTY.withColor(Color.fromRgb(getAttackColor())));
    }

    public int getAttackColor() {
        switch (getTier()) {
            case PARAGON:
                return 0x900000;
            case ELITE:
                return 0x800580;
            case EXTRAORDINARY:
                return 0x0A8080;
            default:
                return 0x0E620E;
        }
    }

    public int getShieldCooldown() {
        return shieldCooldown;
    }

    public void setShieldCooldown(int shieldCooldown) {
        this.shieldCooldown = shieldCooldown;
        this.shieldLife = shieldCooldown / 2;
    }

    public void addShieldUser(AbstractVoidwalkerEntity voidwalker) {
        Objects.requireNonNull(voidwalker);
        users.add(voidwalker.getUUID());
    }

    public List<AbstractVoidwalkerEntity> getUsers() {
        if (level.isClientSide()) {
            return Collections.emptyList();
        }
        ServerWorld world = (ServerWorld) level;
        return users.stream()
                .map(world::getEntity)
                .filter(AbstractVoidwalkerEntity.class::isInstance)
                .map(AbstractVoidwalkerEntity.class::cast)
                .collect(Collectors.toList());
    }

    public boolean hasCooldown(ComputerOperation operation) {
        return getCooldown(operation) > 0;
    }

    public int getCooldown(ComputerOperation operation) {
        int cooldown = abilityCooldowns.getInt(operation) - tickCount;
        if (cooldown < 0) {
            abilityCooldowns.removeInt(operation);
            return 0;
        }
        return cooldown;
    }

    public void addCooldown(ComputerOperation operation, int cooldown) {
        if (cooldown <= 0) {
            return;
        }
        abilityCooldowns.put(operation, tickCount + cooldown);
    }

    public int getAttackIntervalTicks() {
        return attackIntervalTicks;
    }

    public void setAttackIntervalTicks(int attackIntervalTicks) {
        this.attackIntervalTicks = attackIntervalTicks;
    }
}
