package lych.soullery.entity.monster.boss.souldragon;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lych.soullery.client.particle.ModParticles;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.functional.SoulCrystalEntity;
import lych.soullery.entity.monster.IPurifiable;
import lych.soullery.entity.monster.SoulSkeletonEntity;
import lych.soullery.entity.monster.boss.souldragon.phase.Phase;
import lych.soullery.entity.monster.boss.souldragon.phase.PhaseManager;
import lych.soullery.entity.monster.boss.souldragon.phase.PhaseType;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.item.ModItems;
import lych.soullery.network.SoulDragonNetwork;
import lych.soullery.network.SoulDragonNetwork.Message;
import lych.soullery.network.SoulDragonNetwork.MessageType;
import lych.soullery.tag.ModBlockTags;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.world.event.SoulDragonFight;
import lych.soullery.world.event.manager.SoulDragonFightManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.math.Fraction;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SoulDragonEntity extends MobEntity implements IMob, IPurifiable {
    private static final DataParameter<Integer> DATA_HEALTH_STATUS = EntityDataManager.defineId(SoulDragonEntity.class, DataSerializers.INT);
    private static final DataParameter<Integer> DATA_PHASE = EntityDataManager.defineId(SoulDragonEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> DATA_PURIFIED = EntityDataManager.defineId(SoulDragonEntity.class, DataSerializers.BOOLEAN);
    private static final int MAX_HEALTH = 600;
    public static final int POINT_COUNT_INNER = 4;
    public static final int POINT_COUNT_MID = 8;
    public static final int POINT_COUNT_OUTER = 12;
    public static final int POINT_COUNT = POINT_COUNT_INNER + POINT_COUNT_MID + POINT_COUNT_OUTER;
    private static final float OUTER_RANGE = 60;
    private static final float MID_RANGE = 40;
    private static final float INNER_RANGE = 20;
    private static final float EXPLOSION_DAMAGE_RESISTANCE = 0.05f;
    private static final float LOW_HEALTH_ABSOLUTE_DEFENSE = 3;
    private static final EntityPredicate CRYSTAL_DESTROY_TARGETING = new EntityPredicate().range(64);
    public final double[][] positions = new double[64][3];
    private int posPointer = -1;
    private final SoulDragonPartEntity[] subEntities;
    private final SoulDragonPartEntity head;
    private final SoulDragonPartEntity neck;
    private final SoulDragonPartEntity body;
    private final SoulDragonPartEntity tail1;
    private final SoulDragonPartEntity tail2;
    private final SoulDragonPartEntity tail3;
    private final SoulDragonPartEntity wing1;
    private final SoulDragonPartEntity wing2;
    private final PhaseManager phaseManager = new PhaseManager(this);
    private final PathHelper pathHelper = new PathHelper();
    private HealthStatusManager healthManager;
    private BlockPos spawnPos;
    @Nullable
    private SoulDragonFight fight;
    @Nullable
    public SoulCrystalEntity nearestCrystal;
    public int dragonDeathTime;
    public float oFlapTime;
    public float flapTime;
    private int growlTime = 100;
    private int attackStep;
    private boolean inWall;
    private boolean firstSpawnElite = true;
    private int eliteCountRemaining = 16;
    private int eliteToSpawn;
    private int eliteSpawnCooldown;
    public float yRotA;

    public SoulDragonEntity(EntityType<? extends SoulDragonEntity> type, World World) {
        super(type, World);
        head = new SoulDragonPartEntity(this, 1, 1);
        neck = new SoulDragonPartEntity(this, 3, 3);
        body = new SoulDragonPartEntity(this, 5, 3);
        tail1 = new SoulDragonPartEntity(this, 2, 2);
        tail2 = new SoulDragonPartEntity(this, 2, 2);
        tail3 = new SoulDragonPartEntity(this, 2, 2);
        wing1 = new SoulDragonPartEntity(this, 4, 2);
        wing2 = new SoulDragonPartEntity(this, 4, 2);
        subEntities = new SoulDragonPartEntity[]{head, neck, body, tail1, tail2, tail3, wing1, wing2};
        setHealth(this.getMaxHealth());
        noPhysics = true;
        noCulling = true;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, MAX_HEALTH);
    }

    public double[] getLatencyPos(int index, float partialTicks) {
        if (isDeadOrDying()) {
            partialTicks = 0;
        }
        partialTicks = 1 - partialTicks;
        int i = posPointer - index & 63;
        int j = posPointer - index - 1 & 63;
        double[] pos = new double[3];
        double d0 = positions[i][0];
        double d1 = MathHelper.wrapDegrees(positions[j][0] - d0);
        pos[0] = d0 + d1 * partialTicks;
        d0 = positions[i][1];
        d1 = positions[j][1] - d0;
        pos[1] = d0 + d1 * partialTicks;
        pos[2] = MathHelper.lerp(partialTicks, this.positions[i][2], this.positions[j][2]);
        return pos;
    }

    @Override
    public void aiStep() {
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            remove();
            return;
        }
        if (spawnPos == null) {
            spawnPos = blockPosition();
        }
        trySpawnSkeletonElites();
        if (level.isClientSide()) {
            setHealth(this.getHealth());
            if (!isSilent()) {
                float cosFlapTIme = MathHelper.cos(flapTime * ((float) Math.PI * 2F));
                float cosOFlapTime = MathHelper.cos(oFlapTime * ((float) Math.PI * 2F));
                if (cosOFlapTime <= -0.3F && cosFlapTIme >= -0.3F) {
                    level.playLocalSound(getX(), getY(), getZ(), ModSoundEvents.SOUL_DRAGON_FLAP.get(), getSoundSource(), 5, 0.8f + random.nextFloat() * 0.3f, false);
                }
                if (--growlTime < 0) {
                    level.playLocalSound(getX(), getY(), getZ(), ModSoundEvents.SOUL_DRAGON_GROWL.get(), getSoundSource(), 2.5f, 0.8f + random.nextFloat() * 0.3f, false);
                    growlTime = 200 + random.nextInt(200);
                }
            }
        }

        oFlapTime = flapTime;
        if (isDeadOrDying()) {
            float rx = (random.nextFloat() - 0.5f) * 8;
            float ry = (random.nextFloat() - 0.5f) * 4;
            float rz = (random.nextFloat() - 0.5f) * 8;
            level.addParticle(ParticleTypes.EXPLOSION, getX() + rx, getY() + 2 + ry, getZ() + rz, 0, 0, 0);
        } else {
            checkCrystals();
            Vector3d movement = getDeltaMovement();
            float movementLengthInv = 0.2f / (MathHelper.sqrt(getHorizontalDistanceSqr(movement)) * 10 + 1);
            movementLengthInv = movementLengthInv * (float)Math.pow(2.0D, movement.y);
            if (inWall) {
                flapTime += movementLengthInv * 0.5F;
            } else {
                flapTime += movementLengthInv;
            }

            yRot = MathHelper.wrapDegrees(yRot);
            if (isNoAi()) {
                flapTime = 0.5F;
            } else {
                if (posPointer < 0) {
                    for (int i = 0; i < positions.length; i++) {
                        positions[i][0] = yRot;
                        positions[i][1] = getY();
                    }
                }

                if (++posPointer == positions.length) {
                    posPointer = 0;
                }

                positions[posPointer][0] = yRot;
                positions[posPointer][1] = getY();
                if (level.isClientSide()) {
                    if (lerpSteps > 0) {
                        double x = getX() + (lerpX - getX()) / (double) lerpSteps;
                        double y = getY() + (lerpY - getY()) / (double) lerpSteps;
                        double z = getZ() + (lerpZ - getZ()) / (double) lerpSteps;
                        double yRot = MathHelper.wrapDegrees(lerpYRot - this.yRot);
                        this.yRot = (float) (this.yRot + yRot / this.lerpSteps);
                        this.xRot = (float) (this.xRot + (this.lerpXRot - this.xRot) / lerpSteps);
                        lerpSteps--;
                        setPos(x, y, z);
                        setRot(this.yRot, this.xRot);
                    }

                    phaseManager.getCurrentPhase().doClientTick();
                } else {
                    Phase phase = phaseManager.getCurrentPhase();
                    phase.doServerTick();

                    if (phaseManager.getCurrentPhase() != phase) {
                        phase = phaseManager.getCurrentPhase();
                        phase.doServerTick();
                    }

                    Vector3d flyTargetLocation = phase.getFlyTargetLocation();
                    if (flyTargetLocation != null) {
                        double x = flyTargetLocation.x - getX();
                        double y = flyTargetLocation.y - getY();
                        double z = flyTargetLocation.z - getZ();
                        double distanceSqr = x * x + y * y + z * z;
                        float flySpeed = phase.getFlySpeed();
                        double horizontalDistance = MathHelper.sqrt(x * x + z * z);
                        if (horizontalDistance > 0) {
                            y = MathHelper.clamp(y / horizontalDistance, -flySpeed, flySpeed);
                        }

                        setDeltaMovement(getDeltaMovement().add(0, y * 0.01, 0));
                        yRot = MathHelper.wrapDegrees(yRot);
                        double rotMul = MathHelper.clamp(MathHelper.wrapDegrees(180 - MathHelper.atan2(x, z) * (180 / Math.PI) - yRot), -50, 50);
                        Vector3d vectorToMe = flyTargetLocation.subtract(getX(), getY(), getZ()).normalize();
                        Vector3d facedPosition = new Vector3d(MathHelper.sin(yRot * ((float) Math.PI / 180F)), getDeltaMovement().y, -MathHelper.cos(yRot * ((float) Math.PI / 180F))).normalize();
                        float cosAngle = Math.max(((float) facedPosition.dot(vectorToMe) + 0.5f) / 1.5f, 0);
                        yRotA *= 0.8F;
                        yRotA = (float) (yRotA + rotMul * phase.getTurnSpeed());
                        yRot += yRotA * 0.1F;
                        float distanceSqrInv = (float) (2 / (distanceSqr + 1));
                        float movementMul = 0.06F;
                        moveRelative(movementMul * (cosAngle * distanceSqrInv + (1 - distanceSqrInv)), new Vector3d(0, 0, -1));
                        if (inWall) {
                            move(MoverType.SELF, getDeltaMovement().scale(0.8));
                        } else {
                            move(MoverType.SELF, getDeltaMovement());
                        }

                        Vector3d normalizedMovement = getDeltaMovement().normalize();
                        double movementMul1 = 0.8 + 0.15 * (normalizedMovement.dot(facedPosition) + 1) / 2;
                        setDeltaMovement(this.getDeltaMovement().multiply(movementMul1, 0.91, movementMul1));
                    }
                }

                yBodyRot = yRot;
                Vector3d[] positionArray = new Vector3d[subEntities.length];

                for(int i = 0; i < subEntities.length; i++) {
                    positionArray[i] = new Vector3d(subEntities[i].getX(), subEntities[i].getY(), subEntities[i].getZ());
                }

                float rot = (float) (getLatencyPos(5, 1)[1] - getLatencyPos(10, 1)[1]) * 10 * ((float) Math.PI / 180);
                float cosRot = MathHelper.cos(rot);
                float sinRot = MathHelper.sin(rot);
                float yRot = this.yRot * ((float) Math.PI / 180);
                float sinYRot = MathHelper.sin(yRot);
                float cosYRot = MathHelper.cos(yRot);
                tickPart(body, sinYRot * 0.5, 0, -cosYRot * 0.5);
                tickPart(wing1, cosYRot * 4.5, 2, sinYRot * 4.5);
                tickPart(wing2, cosYRot * -4.5, 2, sinYRot * -4.5);
                if (!level.isClientSide() && hurtTime == 0) {
                    knockBack(level.getEntities(this, wing1.getBoundingBox().inflate(4, 2, 4).move(0, -2, 0), EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(SoulDragonEntity::nonSoulSkeleton)));
                    knockBack(level.getEntities(this, wing2.getBoundingBox().inflate(4, 2, 4).move(0, -2, 0), EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(SoulDragonEntity::nonSoulSkeleton)));
                    hurt(level.getEntities(this, head.getBoundingBox().inflate(1), EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(SoulDragonEntity::nonSoulSkeleton)));
                    hurt(level.getEntities(this, neck.getBoundingBox().inflate(1), EntityPredicates.NO_CREATIVE_OR_SPECTATOR.and(SoulDragonEntity::nonSoulSkeleton)));
                }

                float sinYRotA = MathHelper.sin(this.yRot * ((float) Math.PI / 180) - yRotA * 0.01f);
                float cosYRotA = MathHelper.cos(this.yRot * ((float) Math.PI / 180) - yRotA * 0.01f);
                float headYOffset = getHeadYOffset();
                tickPart(head, sinYRotA * 6.5 * cosRot, headYOffset + sinRot * 6.5, -cosYRotA * 6.5 * cosRot);
                tickPart(neck, sinYRotA * 5.5 * cosRot, headYOffset + sinRot * 5.5, -cosYRotA * 5.5 * cosRot);
                double[] posArray5 = getLatencyPos(5, 1.0F);

                for (int i = 0; i < 3; i++) {
                    SoulDragonPartEntity part = null;
                    if (i == 0) {
                        part = tail1;
                    }
                    if (i == 1) {
                        part = tail2;
                    }
                    if (i == 2) {
                        part = tail3;
                    }
                    double[] posArrayForPart = getLatencyPos(12 + i * 2, 1.0F);
                    float ryRot = yRot * ((float) Math.PI / 180F) + rotWrap(posArrayForPart[0] - posArray5[0]) * ((float) Math.PI / 180F);
                    float sinRYRot = MathHelper.sin(ryRot);
                    float cosRYRot = MathHelper.cos(ryRot);
                    float yRotMul = 1.5F;
                    float ryRotMul = (float) (i + 1) * 2;

                    tickPart(part, -(sinYRot * yRotMul + sinRYRot * ryRotMul) * cosRot, posArrayForPart[1] - posArray5[1] - ((ryRotMul + yRotMul) * sinRot) + 1.5, (cosYRot * yRotMul + cosRYRot * ryRotMul) * cosRot);
                }

                if (!level.isClientSide()) {
                    inWall = checkWalls(head.getBoundingBox()) | checkWalls(neck.getBoundingBox()) | checkWalls(body.getBoundingBox());
                    if (fight != null) {
                        fight.updateDragon(this);
                    }
                }

                for (int i = 0; i < subEntities.length; i++) {
                    subEntities[i].xo = positionArray[i].x;
                    subEntities[i].yo = positionArray[i].y;
                    subEntities[i].zo = positionArray[i].z;
                    subEntities[i].xOld = positionArray[i].x;
                    subEntities[i].yOld = positionArray[i].y;
                    subEntities[i].zOld = positionArray[i].z;
                }
            }
        }
    }

    private static boolean nonSoulSkeleton(Entity e) {
        return !(e instanceof SoulSkeletonEntity);
    }

    private void trySpawnSkeletonElites() {
        if (level.isClientSide() || isDeadOrDying()) {
            return;
        }
        boolean maySpawnElite = getHealthStatus() == HealthStatus.LOW && eliteCountRemaining > 0 && eliteToSpawn <= 0;
        if (maySpawnElite && (firstSpawnElite || random.nextInt(Math.max(1, 100 + ((int) getHealth()) * 3 - eliteCountRemaining * 5)) == 0)) {
            if (eliteCountRemaining > 9) {
                eliteToSpawn = random.nextInt(5) + 5;
            } else {
                eliteToSpawn = eliteCountRemaining;
            }
            eliteCountRemaining -= eliteToSpawn;
            firstSpawnElite = false;
        }
        if (eliteSpawnCooldown > 0) {
            eliteSpawnCooldown--;
        }
        if (eliteToSpawn > 0 && eliteSpawnCooldown <= 0 && !inWall()) {
            SoulSkeletonEntity elite = ModEntities.SOUL_SKELETON.create(level);
            if (elite != null) {
                elite.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 20 * 3, 4, false, false, false));
                elite.moveTo(getHead().getX(), getHead().getY(), getHead().getZ(), random.nextFloat() * 360, 0);
                elite.setTarget(level.getNearestPlayer(getX(), getY(), getZ(), 99, true));
                elite.finalizeSpawn((IServerWorld) level, level.getCurrentDifficultyAt(blockPosition()), SpawnReason.MOB_SUMMONED, null, null);
                modifyElite(elite);
                level.addFreshEntity(elite);
                eliteSpawnCooldown = 30;
                eliteToSpawn--;
            }
        }
    }

    private void modifyElite(SoulSkeletonEntity elite) {
        EntityUtils.getAttribute(elite, Attributes.FOLLOW_RANGE).setBaseValue(49);
        EntityUtils.getAttribute(elite, Attributes.MOVEMENT_SPEED).setBaseValue(0.33);
        EntityUtils.getAttribute(elite, Attributes.KNOCKBACK_RESISTANCE).setBaseValue(random.nextDouble() * 0.3 + 0.3);
        EntityUtils.getAttribute(elite, Attributes.ATTACK_DAMAGE).setBaseValue(3);
        elite.setClimbable(true);

        List<Pair<Reinforcement, Integer>> guardianReinforcements = new ArrayList<>(ImmutableList.of(
                Pair.of(Reinforcements.GUARDIAN, 2),
                Pair.of(Reinforcements.GUARDIAN, 1)
        ));
        List<Pair<Reinforcement, Integer>> silverfishReinforcements = new ArrayList<>(ImmutableList.of(
                Pair.of(Reinforcements.SILVERFISH, 2),
                Pair.of(Reinforcements.SILVERFISH, 1)
        ));
        Collections.shuffle(guardianReinforcements, random);
        Collections.shuffle(silverfishReinforcements, random);

        ItemStack helmet = new ItemStack(ModItems.REFINED_SOUL_METAL_HELMET);
        ReinforcementHelper.addReinforcement(helmet, silverfishReinforcements.get(0).getFirst(), guardianReinforcements.get(0).getSecond());
        ItemStack chestplate = new ItemStack(ModItems.REFINED_SOUL_METAL_CHESTPLATE);
        ReinforcementHelper.addReinforcement(chestplate, guardianReinforcements.get(0).getFirst(), guardianReinforcements.get(0).getSecond());
        ItemStack leggings = new ItemStack(ModItems.REFINED_SOUL_METAL_LEGGINGS);
        ReinforcementHelper.addReinforcement(leggings, guardianReinforcements.get(1).getFirst(), guardianReinforcements.get(1).getSecond());
        ItemStack boots = new ItemStack(ModItems.REFINED_SOUL_METAL_BOOTS);
        ReinforcementHelper.addReinforcement(boots, silverfishReinforcements.get(1).getFirst(), guardianReinforcements.get(1).getSecond());

        elite.setItemSlot(EquipmentSlotType.HEAD, helmet);
        elite.setItemSlot(EquipmentSlotType.CHEST, chestplate);
        elite.setItemSlot(EquipmentSlotType.LEGS, leggings);
        elite.setItemSlot(EquipmentSlotType.FEET, boots);

        Reinforcement mainHandWeaponReinforcement;
        int mainHandWeaponReinforcementLevel;
        double randomValue = random.nextDouble();
        if (elite.getMainHandItem().getItem() instanceof BowItem) {
            if (randomValue < 0.8) {
                mainHandWeaponReinforcement = Reinforcements.SKELETON;
            } else {
                mainHandWeaponReinforcement = Reinforcements.BEE;
            }
            mainHandWeaponReinforcementLevel = 1;
        } else {
            if (randomValue < 0.4) {
                mainHandWeaponReinforcement = Reinforcements.ZOMBIE;
                mainHandWeaponReinforcementLevel = 2;
            } else if (randomValue < 0.7) {
                mainHandWeaponReinforcement = Reinforcements.WOLF;
                mainHandWeaponReinforcementLevel = 2;
            } else if (randomValue < 0.9) {
                mainHandWeaponReinforcement = Reinforcements.CAVE_SPIDER;
                mainHandWeaponReinforcementLevel = 1;
            } else {
                mainHandWeaponReinforcement = Reinforcements.SPIDER;
                mainHandWeaponReinforcementLevel = 1;
            }
        }
        ReinforcementHelper.addReinforcement(elite.getMainHandItem(), mainHandWeaponReinforcement, mainHandWeaponReinforcementLevel);

        if (random.nextDouble() < 0.3) {
            if (random.nextBoolean()) {
                elite.addEffect(new EffectInstance(Effects.REGENERATION, Integer.MAX_VALUE, 0, false, false, false));
            } else {
                elite.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false, false));
            }
        } else {
            elite.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, Integer.MAX_VALUE, 0, false, false, false));
        }

        EntityUtils.spawnAnimServerside(elite, (ServerWorld) level);
    }

    private void tickPart(SoulDragonPartEntity part, double x, double y, double z) {
        part.setPos(getX() + x, getY() + y, getZ() + z);
    }

    private float getHeadYOffset() {
        double[] a1 = getLatencyPos(5, 1);
        double[] a2 = getLatencyPos(0, 1);
        return (float) (a1[1] - a2[1]);
    }

    private void checkCrystals() {
        if (getHealthStatus() != HealthStatus.HIGH && getFight() != null && EntityUtils.isAlive(getFight().getSuperCrystal())) {
            if (!level.isClientSide()) {
                updateSuperCrystalServerside();
            }
        }

        if (nearestCrystal != null) {
            if (!nearestCrystal.isAlive()) {
                nearestCrystal = null;
            } else if (tickCount % 10 == 0 && getHealth() < getMaxHealth()) {
                setHealth(getHealth() + 1);
            }
        }

        if (random.nextInt(10) == 0 && getHealthStatus() == HealthStatus.HIGH) {
            List<SoulCrystalEntity> crystalsToCheck = level.getEntitiesOfClass(SoulCrystalEntity.class, getBoundingBox().inflate(32));
            SoulCrystalEntity closest = null;
            double minDistanceSqr = Double.MAX_VALUE;

            for (SoulCrystalEntity crystal : crystalsToCheck) {
                double distanceSqr = crystal.distanceToSqr(this);
                if (distanceSqr < minDistanceSqr) {
                    minDistanceSqr = distanceSqr;
                    closest = crystal;
                }
            }

            nearestCrystal = closest;
        }
    }

    private void updateSuperCrystalServerside() {
        SoulCrystalEntity temp = nearestCrystal;
        nearestCrystal = getFight().getSuperCrystal();
        if (nearestCrystal != null && temp != nearestCrystal) {
            SoulDragonNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), new Message(MessageType.RECOGNIZE_FORTIFIED_CRYSTAL, blockPosition(), getId(), isSilent(), isPurified(), nearestCrystal.getId()));
        }
    }

    public void knockBack(List<? extends Entity> entities) {
        double x = (body.getBoundingBox().minX + body.getBoundingBox().maxX) / 2;
        double z = (body.getBoundingBox().minZ + body.getBoundingBox().maxZ) / 2;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                double tx = entity.getX() - x;
                double tz = entity.getZ() - z;
                double distanceSqr = Math.max(tx * tx + tz * tz, 0.1);
                entity.push(tx / distanceSqr * 4, 0.2, tz / distanceSqr * 4);
                if (((LivingEntity) entity).getLastHurtByMobTimestamp() < entity.tickCount - 2) {
                    entity.hurt(DamageSource.mobAttack(this), 5);
                    doEnchantDamageEffects(this, entity);
                }
            }
        }
    }

    public HealthStatus getHealthStatus() {
        return healthManager.getStatus();
    }

    public void hurt(List<? extends Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                entity.hurt(DamageSource.mobAttack(this), 10);
                doEnchantDamageEffects(this, entity);
            }
        }
    }

    public void doMagicAttackAt(BlockPos pos, World level) {
        AxisAlignedBB bb = new AxisAlignedBB(pos.offset(-1, -1, -1), pos.offset(1, 1, 1));
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bb, entity -> entity != this);
        magicAttack(entities);
    }

    public void magicAttack(List<? extends Entity> entities) {
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                living.hurt(DamageSource.mobAttack(this).bypassArmor().setMagic(), 4);
            }
        }
    }

    private static float rotWrap(double rot) {
        return (float) MathHelper.wrapDegrees(rot);
    }

    @SuppressWarnings("deprecation")
    private boolean checkWalls(AxisAlignedBB bb) {
        int minX = MathHelper.floor(bb.minX);
        int minY = MathHelper.floor(bb.minY);
        int minZ = MathHelper.floor(bb.minZ);
        int maxX = MathHelper.floor(bb.maxX);
        int maxY = MathHelper.floor(bb.maxY);
        int maxZ = MathHelper.floor(bb.maxZ);
        boolean inWall = false;
        boolean destroyed = false;

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();
                    if (!state.isAir(level, pos) && state.getMaterial() != Material.FIRE) {
                        if (ForgeHooks.canEntityDestroy(level, pos, this) && !ModBlockTags.SOUL_DRAGON_IMMUNE.contains(block)) {
                            destroyed = level.removeBlock(pos, false) || destroyed;
                        } else {
                            inWall = true;
                        }
                    }
                }
            }
        }

        if (destroyed) {
            BlockPos rPos = new BlockPos(minX + random.nextInt(maxX - minX + 1), minY + random.nextInt(maxY - minY + 1), minZ + random.nextInt(maxZ - minZ + 1));
            level.levelEvent(Constants.WorldEvents.SPAWN_EXPLOSION_PARTICLE, rPos, 0);
        }

        return inWall;
    }

    public boolean hurt(SoulDragonPartEntity part, DamageSource source, float amount) {
        if (phaseManager.getCurrentPhase().getPhase() == PhaseType.DYING) {
            return false;
        } else {
            amount = phaseManager.getCurrentPhase().onHurt(source, amount);
            if (part != head) {
                amount = amount / 4 + Math.min(amount, 1);
            }
            if (source.isExplosion()) {
                amount *= EXPLOSION_DAMAGE_RESISTANCE;
            }
            if (source.getEntity() == this || source.getEntity() != null && source.getEntity().is(this)) {
                amount = 0;
            }
            if (getHealthStatus() == HealthStatus.LOW) {
                amount -= LOW_HEALTH_ABSOLUTE_DEFENSE;
            }
            if (amount < 0.01f) {
                return false;
            } else {
                if (source.getEntity() instanceof PlayerEntity || source.isExplosion()) {
                    reallyHurt(source, amount);
                    if (isDeadOrDying()) {
                        setHealth(1);
                        phaseManager.setPhase(PhaseType.DYING);
                    }
                }
                return true;
            }
        }
    }

    protected boolean reallyHurt(DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void kill() {
        remove();
        if (fight != null) {
            fight.updateDragon(this);
            fight.setDragonKilled(this);
        }
    }

    @Override
    protected void tickDeath() {
        if (fight != null) {
            fight.updateDragon(this);
        }

        dragonDeathTime++;
        if (dragonDeathTime >= 180 && dragonDeathTime <= 200) {
            float rx = (random.nextFloat() - 0.5f) * 8;
            float ry = (random.nextFloat() - 0.5f) * 4;
            float rz = (random.nextFloat() - 0.5f) * 8;
            level.addParticle(ParticleTypes.EXPLOSION_EMITTER, getX() + rx, getY() + 2 + ry, getZ() + rz, 0, 0, 0);
        }

        boolean loot = level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        int exp = 1000;

        if (!level.isClientSide()) {
            if (dragonDeathTime > 150 && dragonDeathTime % 5 == 0 && loot) {
                dropExperience(MathHelper.floor((float) exp * 0.08f));
            }

            if (dragonDeathTime == 1 && !isSilent()) {
                SoulDragonNetwork.INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), new Message(MessageType.DRAGON_DEATH, blockPosition(), getId(), isSilent(), isPurified()));
            }
        }

        move(MoverType.SELF, new Vector3d(0, 0.1, 0));
        yRot += 20;
        yBodyRot = yRot;

        if (dragonDeathTime == 200 && !level.isClientSide()) {
            if (loot) {
                dropExperience(MathHelper.floor(exp * 0.2f));
            }
            if (fight != null) {
                fight.setDragonKilled(this);
            }
            remove();
        }
    }

    private void dropExperience(int value) {
        while (value > 0) {
            int expValue = ExperienceOrbEntity.getExperienceValue(value);
            value -= expValue;
            level.addFreshEntity(new ExperienceOrbEntity(level, getX(), getY(), getZ(), expValue));
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PHASE, PhaseType.DEFAULT.getId());
        entityData.define(DATA_PURIFIED, false);
        entityData.define(DATA_HEALTH_STATUS, HealthStatus.HIGH.getId());
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld level, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        spawnPos = blockPosition();
        return super.finalizeSpawn(level, difficulty, reason, data, compoundNBT);
    }

    public int getPhaseId() {
        return entityData.get(DATA_PHASE);
    }

    public void setPhaseId(int id) {
        entityData.set(DATA_PHASE, id);
    }

    public <T extends Phase> T getPhase(PhaseType<T> type) {
        return phaseManager.getPhase(type);
    }

    public void setPhase(PhaseType<?> type) {
        phaseManager.setPhase(type);
    }

    public void setRandomAttackPhase(LivingEntity target) {
        phaseManager.setRandomAttackPhase(target);
    }

    @Override
    public void checkDespawn() {}

    @Override
    public boolean addEffect(EffectInstance effect) {
        return false;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public SoulDragonPartEntity[] getParts() {
        return subEntities;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putInt("DragonPhase", phaseManager.getCurrentPhase().getPhase().getId());
        compoundNBT.putInt("AttackStep", getAttackStep());
        compoundNBT.putBoolean("Purified", isPurified());
        if (fight != null) {
            compoundNBT.putInt("Fight", fight.getId());
        }
        compoundNBT.putInt("EliteCountRemaining", eliteCountRemaining);
        compoundNBT.putInt("EliteToSpawn", eliteToSpawn);
        compoundNBT.putBoolean("FirstSpawnElite", firstSpawnElite);
        getHealthManager().saveStatus(compoundNBT);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("DragonPhase")) {
            phaseManager.setPhase(PhaseType.get(compoundNBT.getInt("DragonPhase")));
        }
        setAttackStep(compoundNBT.getInt("AttackStep"));
        if (!level.isClientSide() && compoundNBT.contains("Fight", Constants.NBT.TAG_INT)) {
            setFight(SoulDragonFightManager.get((ServerWorld) level).getFight(compoundNBT.getInt("Fight")));
        }
        setPurified(compoundNBT.getBoolean("Purified"));
        if (compoundNBT.contains("HealthStatus")) {
            getHealthManager().loadStatus(compoundNBT);
        }
        if (compoundNBT.contains("EliteCountRemaining")) {
            eliteCountRemaining = compoundNBT.getInt("EliteCountRemaining");
        }
        if (compoundNBT.contains("EliteToSpawn")) {
            eliteToSpawn = compoundNBT.getInt("EliteToSpawn");
        }
        if (compoundNBT.contains("FirstSpawnElite")) {
            firstSpawnElite = compoundNBT.getBoolean("FirstSpawnElite");
        }
    }

    @Override
    public void setCustomName(@Nullable ITextComponent component) {
        super.setCustomName(component);
        if (getFight() != null && component != null) {
            getFight().getBossInfo().setName(component);
        }
    }

    @Nullable
    public SoulDragonFight getFight() {
        return fight;
    }

    public void setFight(@Nullable SoulDragonFight fight) {
        this.fight = fight;
    }

    private List<SoulCrystalEntity> getCrystals() {
        if (getFight() == null) {
            return Collections.emptyList();
        }
        return getFight().getCrystals();
    }

    public SoulDragonPartEntity getHead() {
        return head;
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadPartYOffset(int part, double[] a1, double[] a2) {
        Phase phase = phaseManager.getCurrentPhase();
        PhaseType<?> type = phase.getPhase();
        double offset;
        if (type != PhaseType.LANDING && type != PhaseType.TAKEOFF) {
            if (part == 6) {
                offset = 0.0D;
            } else {
                offset = a2[1] - a1[1];
            }
        } else {
            BlockPos center = getFightCenter();
            float dis = Math.max(MathHelper.sqrt(center.distSqr(position(), true)) / 4, 1);
            offset = part / dis;
        }
        return (float) offset;
    }

    public BlockPos getFightCenter() {
        if (fight == null) {
            return spawnPos == null ? blockPosition() : spawnPos;
        }
        return level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, fight.getCenter());
    }

    public Vector3d getHeadLookVector(float partialTicks) {
        Phase phase = phaseManager.getCurrentPhase();
        PhaseType<?> type = phase.getPhase();
        Vector3d vector;

        if (type != PhaseType.LANDING && type != PhaseType.TAKEOFF) {
            vector = getViewVector(partialTicks);
        } else {
            BlockPos pos = this.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, getFightCenter());
            float dis = Math.max(MathHelper.sqrt(pos.distSqr(position(), true)) / 4, 1.0F);
            float disInv = 6 / dis;
            float xRotO = xRot;
            float mul = 1.5f;
            xRot = -disInv * mul * 5;
            vector = getViewVector(partialTicks);
            xRot = xRotO;
        }

        return vector;
    }

    HealthStatusManager getHealthManager() {
        if (healthManager == null) {
            healthManager = new HealthStatusManager();
        }
        return healthManager;
    }

    public void onCrystalDestroyed(SoulCrystalEntity crystal, BlockPos pos, DamageSource source) {
        PlayerEntity attacker;

        if (source.getEntity() instanceof PlayerEntity) {
            attacker = (PlayerEntity) source.getEntity();
        } else {
            attacker = level.getNearestPlayer(CRYSTAL_DESTROY_TARGETING, pos.getX(), pos.getY(), pos.getZ());
        }

        if (crystal == nearestCrystal) {
            hurt(head, DamageSource.explosion(attacker), 10 / EXPLOSION_DAMAGE_RESISTANCE);
        }

        phaseManager.getCurrentPhase().onCrystalDestroyed(crystal, pos, source, attacker);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> data) {
        if (DATA_PHASE.equals(data) && level.isClientSide()) {
            phaseManager.setPhase(PhaseType.get(getPhaseId()));
        }
        super.onSyncedDataUpdated(data);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(getHealthManager().handleSetHealth(health));
    }

    @Override
    public SoundCategory getSoundSource() {
        return SoundCategory.HOSTILE;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.SOUL_DRAGON_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_DRAGON_DEATH.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.SOUL_DRAGON_HURT.get();
    }

    private static void initPathPoints(PathPoint[] nodes, World level, BlockPos center) {
        float pi = (float) Math.PI;
        for (int i = 0; i < POINT_COUNT; i++) {
            int h = 5;
            int x;
            int z;
            if (i < POINT_COUNT_OUTER) {
                float mul = (float) (Math.PI / POINT_COUNT_OUTER);
                x = MathHelper.floor(OUTER_RANGE * MathHelper.cos(2 * (-pi + mul * i)));
                z = MathHelper.floor(OUTER_RANGE * MathHelper.sin(2 * (-pi + mul * i)));
            } else if (i < POINT_COUNT_OUTER + POINT_COUNT_MID) {
                int i1 = i - POINT_COUNT_OUTER;
                x = MathHelper.floor(MID_RANGE * MathHelper.cos(2 * (-pi + (pi / POINT_COUNT_MID) * i1)));
                z = MathHelper.floor(MID_RANGE * MathHelper.sin(2 * (-pi + (pi / POINT_COUNT_MID) * i1)));
                h += 10;
            } else {
                int i2 = i - POINT_COUNT_OUTER - POINT_COUNT_MID;
                x = MathHelper.floor(INNER_RANGE * MathHelper.cos(2 * (-pi + (pi / POINT_COUNT_INNER) * i2)));
                z = MathHelper.floor(INNER_RANGE * MathHelper.sin(2 * (-pi + (pi / POINT_COUNT_INNER) * i2)));
            }

            x += center.getX();
            z += center.getZ();

            int y = Math.max(level.getSeaLevel() + 10, level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).getY() + h);
            nodes[i] = new PathPoint(x, y, z);
        }
    }

    private static void initNodeAdjacency(int[] nodeAdjacency) {
//      Outer
        addNearbyNode(nodeAdjacency, 0, 1, 11, 12);
        addNearbyNode(nodeAdjacency, 1, 0, 2, 13);
        addNearbyNode(nodeAdjacency, 2, 1, 3, 13);
        addNearbyNode(nodeAdjacency, 3, 2, 4, 14);
        addNearbyNode(nodeAdjacency, 4, 3, 5, 15);
        addNearbyNode(nodeAdjacency, 5, 4, 6, 15);
        addNearbyNode(nodeAdjacency, 6, 5, 7, 16);
        addNearbyNode(nodeAdjacency, 7, 6, 8, 17);
        addNearbyNode(nodeAdjacency, 8, 7, 9, 17);
        addNearbyNode(nodeAdjacency, 9, 8, 10, 18);
        addNearbyNode(nodeAdjacency, 10, 9, 11, 19);
        addNearbyNode(nodeAdjacency, 11, 0, 10, 19);
//      Mid
        addNearbyNode(nodeAdjacency, 12, 0, 13, 19, 20);
        addNearbyNode(nodeAdjacency, 13, 1, 2, 12, 14, 20, 21);
        addNearbyNode(nodeAdjacency, 14, 3, 13, 15, 21);
        addNearbyNode(nodeAdjacency, 15, 4, 5, 14, 16, 21, 22);
        addNearbyNode(nodeAdjacency, 16, 6, 15, 17, 22);
        addNearbyNode(nodeAdjacency, 17, 7, 8, 16, 18, 22, 23);
        addNearbyNode(nodeAdjacency, 18, 9, 17, 19, 23);
        addNearbyNode(nodeAdjacency, 19, 10, 11, 12, 18, 20, 23);
//      Inner
        addNearbyNode(nodeAdjacency, 20, 12, 13, 19, 21, 22, 23);
        addNearbyNode(nodeAdjacency, 21, 13, 14, 15, 20, 22, 23);
        addNearbyNode(nodeAdjacency, 22, 15, 16, 17, 20, 21, 23);
        addNearbyNode(nodeAdjacency, 23, 17, 18, 19, 20, 21, 22);
    }

    private static void addNearbyNode(int[] nodeAdjacency, int node, int... nodes) {
        if (node < 0 || node >= POINT_COUNT) {
            throw new IllegalArgumentException(String.format("nodeId should be in [0, %d)", POINT_COUNT));
        }
        for (int nearby : nodes) {
            nodeAdjacency[node] |= 1 << nearby;
        }
    }

    private int getStartCheckIndex() {
        if (fight == null || fight.getCrystalsAlive() == 0) {
            return 12;
        }
        return 0;
    }

    private static void setFGHDirectly(PathPoint point, float g, float h) {
        point.g = g;
        point.h = h;
        point.f = g + h;
    }

    public PathHelper getPathHelper() {
        return pathHelper;
    }

    @Nullable
    public Path findPath(int fromId, int toId, @Nullable PathPoint exp) {
        return getPathHelper().findPath(fromId, toId, exp);
    }

    public int findClosestNode() {
        return getPathHelper().findClosestNode();
    }

    public int findClosestNode(double x, double y, double z) {
        return getPathHelper().findClosestNode(x, y, z);
    }

    public boolean inWall() {
        return inWall;
    }

    public int getAttackStep() {
        return attackStep;
    }

    public void setAttackStep(int attackStep) {
        this.attackStep = attackStep;
    }

    @Override
    public boolean isPurified() {
        return entityData.get(DATA_PURIFIED);
    }

    @Override
    public void setPurified(boolean purified) {
        entityData.set(DATA_PURIFIED, purified);
        if (getFight() != null) {
            getFight().updateDragon(this);
        }
    }

    public class PathHelper {
        private final PathPoint[] nodes = new PathPoint[POINT_COUNT];
        private final int[] nodeAdjacency = new int[POINT_COUNT];
        private final PathHeap openSet = new PathHeap();

        public int findClosestNode() {
            if (notInitialized()) {
                initPathPoints(nodes, level, getFightCenter());
                initNodeAdjacency(nodeAdjacency);
            }
            return findClosestNode(getX(), getY(), getZ());
        }

        private boolean notInitialized() {
            return nodes[0] == null;
        }

        public int findClosestNode(double x, double y, double z) {
            float minDistanceSqr = Float.MAX_VALUE;
            int closestIndex = 0;
            PathPoint point = new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));

            for (int i = getStartCheckIndex(); i < POINT_COUNT; ++i) {
                if (nodes[i] != null) {
                    float distanceSqr = nodes[i].distanceToSqr(point);
                    if (distanceSqr < minDistanceSqr) {
                        minDistanceSqr = distanceSqr;
                        closestIndex = i;
                    }
                }
            }

            return closestIndex;
        }

        @Nullable
        public Path findPath(int fromId, int toId, @Nullable PathPoint exp) {
            reset();

            PathPoint from = nodes[fromId];
            PathPoint to = nodes[toId];
            setFGHDirectly(from, 0, from.distanceTo(to));

            openSet.clear();
            openSet.insert(from);

            int startCheckIndex = getStartCheckIndex();
            PathPoint min = from;

            while (!openSet.isEmpty()) {
                PathPoint top = openSet.pop();
                if (top.equals(to)) {
                    if (exp != null) {
                        exp.cameFrom = to;
                        to = exp;
                    }
                    return constructPath(to);
                }
                if (top.distanceTo(to) < min.distanceTo(to)) {
                    min = top;
                }

                top.closed = true;
                int topId = 0;

                for (int i = 0; i < 24; i++) {
                    if (nodes[i] == top) {
                        topId = i;
                        break;
                    }
                }

                for (int i = startCheckIndex; i < 24; i++) {
                    if ((nodeAdjacency[topId] & 1 << i) > 0) {
                        PathPoint pi = nodes[i];
                        if (!pi.closed) {
                            float g = top.g + top.distanceTo(pi);
                            if (!pi.inOpenSet() || g < pi.g) {
                                pi.cameFrom = top;
                                setFGHDirectly(pi, g, pi.distanceTo(to));
                                if (pi.inOpenSet()) {
                                    openSet.changeCost(pi, pi.g + pi.h);
                                } else {
                                    openSet.insert(pi);
                                }
                            }
                        }
                    }
                }
            }

            if (min == from) {
                return null;
            } else {
                LOGGER.debug("Failed to find path from {} to {}", fromId, toId);
                if (exp != null) {
                    exp.cameFrom = min;
                    min = exp;
                }
                return constructPath(min);
            }
        }

        private void reset() {
            for (int i = 0; i < POINT_COUNT; ++i) {
                PathPoint pi = nodes[i];
                pi.closed = false;
                pi.f = 0;
                pi.g = 0;
                pi.h = 0;
                pi.cameFrom = null;
                pi.heapIdx = -1;
            }
        }

        private Path constructPath(PathPoint targetPoint) {
            Queue<PathPoint> queue = new ArrayDeque<>();
            PathPoint father = targetPoint;
            queue.add(targetPoint);

            while (father.cameFrom != null) {
                father = father.cameFrom;
                queue.add(father);
            }

            return new Path(new ArrayList<>(queue), new BlockPos(targetPoint.x, targetPoint.y, targetPoint.z), true);
        }
    }

    public class HealthStatusManager {
        public float handleSetHealth(float health) {
            return getStatus().handleSetHealth(SoulDragonEntity.this, health, (int) getMaxHealth());
        }

        public HealthStatus getStatus() {
            try {
                return HealthStatus.byId(entityData.get(DATA_HEALTH_STATUS));
            } catch (EnumConstantNotFoundException e) {
                if (ConfigHelper.shouldFailhard()) {
                    throw new RuntimeException(ConfigHelper.FAILHARD_MESSAGE + String.format("HealthStatus[%s] not found", e.getId()));
                } else {
                    LOGGER.error("HealthStatus[{}] not found, used default HealthStatus.{}", e.getId(), HealthStatus.HIGH);
                }
                setStatus(HealthStatus.HIGH);
                return HealthStatus.HIGH;
            }
        }

        private void setStatus(HealthStatus status) {
            setStatus(status, true);
        }

        private void setStatus(HealthStatus status, boolean tryBegin) {
            entityData.set(DATA_HEALTH_STATUS, status.getId());
            if (tryBegin) {
                status.begin(SoulDragonEntity.this);
            }
        }

        public void saveStatus(CompoundNBT compoundNBT) {
            compoundNBT.putInt("HealthStatus", getStatus().getId());
        }

        public void loadStatus(CompoundNBT compoundNBT) {
            try {
                setStatus(HealthStatus.byId(compoundNBT.getInt("HealthStatus")), false);
            } catch (EnumConstantNotFoundException e) {
                LOGGER.warn("HealthStatus[{}] not found, reloading...", e.getId());
                setStatus(reloadStatus(), false);
            }
        }

        private HealthStatus reloadStatus() {
            HealthStatus[] values = HealthStatus.values();
            for (HealthStatus s : values) {
                if (getHealth() >= s.getLimit((int) getMaxHealth())) {
                    return s;
                }
            }
            LOGGER.error("HealthStatus cannot be reloaded");
            return HealthStatus.HIGH;
        }
    }

    public enum HealthStatus implements IIdentifiableEnum {
        HIGH(Fraction.TWO_THIRDS) {
            @Override
            public void begin(SoulDragonEntity dragon) {}

            @Override
            public boolean meetsBreakCondition(SoulDragonEntity dragon) {
                if (dragon.getFight() == null) {
                    return true;
                }
                return dragon.getFight().getCrystalsAlive() <= 0;
            }
        },
        MID(Fraction.ONE_THIRD) {
            @Override
            public void begin(SoulDragonEntity dragon) {
                dragon.setPurified(true);
                if (!dragon.level.isClientSide()) {
                    EntityUtils.addParticlesAroundSelfServerside(dragon, (ServerWorld) dragon.level, ModParticles.SOUL_DRAGON_BREATH_PURE, 30);
                }
                if (dragon.getFight() != null) {
                    dragon.getFight().placeSuperCrystal();
                }
            }

            @Override
            public boolean meetsBreakCondition(SoulDragonEntity dragon) {
                if (dragon.getFight() == null) {
                    return true;
                }
                return dragon.getFight().getSuperCrystal() == null;
            }
        },
        LOW(Fraction.ZERO) {
            @Override
            public void begin(SoulDragonEntity dragon) {
                if (dragon.getFight() != null) {
                    dragon.getFight().updateDragon(dragon);
                }
            }

            @Override
            public boolean meetsBreakCondition(SoulDragonEntity dragon) {
                return true;
            }
        };

        private final Fraction fraction;

        HealthStatus(Fraction fraction) {
            this.fraction = fraction;
        }

        public abstract void begin(SoulDragonEntity dragon);

        public abstract boolean meetsBreakCondition(SoulDragonEntity dragon);

        private int getLimit(int maxHealth) {
            return fraction.multiplyBy(Fraction.getFraction(maxHealth, 1)).intValue();
        }

        public float handleSetHealth(SoulDragonEntity dragon, float health, int maxHealth) {
            if (health <= getLimit(maxHealth) && meetsBreakCondition(dragon)) {
                try {
                    dragon.getHealthManager().setStatus(byId(getId() + 1));
                } catch (EnumConstantNotFoundException ignored) {}
                return dragon.getHealthStatus() == LOW ? health : Math.max(health, dragon.getHealthStatus().getLimit(maxHealth) + 1);
            }
            int upperBound;
            try {
                upperBound = byId(getId() - 1).getLimit(maxHealth);
            } catch (EnumConstantNotFoundException e) {
                upperBound = maxHealth;
            }
            return MathHelper.clamp(health, getLimit(maxHealth) + 1, upperBound);
        }

        public static HealthStatus byId(int id) throws EnumConstantNotFoundException {
            return IIdentifiableEnum.byOrdinal(values(), id);
        }
    }
}
