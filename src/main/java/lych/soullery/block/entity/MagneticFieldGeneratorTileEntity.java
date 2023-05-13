package lych.soullery.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import lych.soullery.entity.iface.ESVMob;
import lych.soullery.util.RedstoneParticles;
import lych.soullery.util.Utils;
import lych.soullery.util.Vectors;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.*;
import java.util.function.Predicate;

public class MagneticFieldGeneratorTileEntity extends TargetableTileEntity {
    private static final double SPEED_MULTIPLIER = 0.5;
    private static final double PROJECTILE_SPEED_MULTIPLIER = 0.85;
    private static final double CENTRIPETAL_SPEED = 0.004;
    private static final int PROJECTILE_LIFE = 200;
    private static final ImmutableList<String> VALID_METALS = ImmutableList.of("iron", "cobalt", "nickel");
    private static final ImmutableMap<Item, Double> ITEM_VALUE = ImmutableMap.<Item, Double>builder()
            .put(Items.IRON_NUGGET, 0.01)
            .put(Items.IRON_INGOT, 0.09)
            .put(Items.IRON_BLOCK, 0.81)
            .build();
    private static final ImmutableList<Pair<Predicate<? super ItemStack>, Double>> ITEM_VALUE_EXTRA = ImmutableList.of(
            Pair.of(MagneticFieldGeneratorTileEntity::isIronTool, 0.5),
            Pair.of(MagneticFieldGeneratorTileEntity::isIronArmor, 0.5),
            Pair.of(MagneticFieldGeneratorTileEntity::isTool, 0.05),
            Pair.of(MagneticFieldGeneratorTileEntity::isArmor, 0.1)
    );

    private final Map<UUID, Integer> projectilesToRemove = new HashMap<>();
    private final int radius = 8;
    private int globalTickCount;

    public MagneticFieldGeneratorTileEntity(TileEntityType<?> type) {
        super(type);
    }

    private static boolean isIronTool(ItemStack stack) {
        return isTool(stack) && stack.getItem() instanceof TieredItem && ((TieredItem) stack.getItem()).getTier() == ItemTier.IRON;
    }

    private static boolean isIronArmor(ItemStack stack) {
        return isArmor(stack) && ((ArmorItem) stack.getItem()).getMaterial() == ArmorMaterial.IRON;
    }

    private static boolean isTool(ItemStack stack) {
        return stack.getItem().isDamageable(stack);
    }

    private static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    protected List<? extends Entity> findTargets(World level) {
        List<Entity> attackable = new ArrayList<>();
        attackable.addAll(level.getEntitiesOfClass(LivingEntity.class, getBB(radius, 4), this::attackable));
        attackable.addAll(level.getEntitiesOfClass(ProjectileEntity.class, getBB(radius * 3 / 2, 6), this::affectable));
        return attackable;
    }

    private boolean attackable(LivingEntity entity) {
        if (ESVMob.isESVMob(entity)) {
            return false;
        }
        if (EntityPredicates.NO_SPECTATORS.test(entity)) {
            if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative() && ((PlayerEntity) entity).abilities.flying) {
                return false;
            }
            return entity.attackable() && horizontalDistanceToSqr(entity) <= radius * radius;
        }
        return false;
    }

    private boolean affectable(ProjectileEntity projectile) {
        return ESVMob.nonESVMob(projectile.getOwner()) && horizontalDistanceToSqr(projectile) <= radius * radius;
    }

    @Override
    public void tick() {
        globalTickCount++;
        super.tick();
    }

    @Override
    protected void applyTarget(Entity target, boolean clientside) {
        if (target instanceof ProjectileEntity) {
            Integer timestamp = projectilesToRemove.putIfAbsent(target.getUUID(), globalTickCount + PROJECTILE_LIFE);
            if (timestamp != null && timestamp < globalTickCount) {
                target.remove();
                projectilesToRemove.remove(target.getUUID());
                if (clientside) {
                    for (int i = 0; i < 8; i++) {
                        double dx = level.random.nextGaussian() * 0.02D;
                        double dy = level.random.nextGaussian() * 0.02D;
                        double dz = level.random.nextGaussian() * 0.02D;
                        level.addParticle(ParticleTypes.POOF, target.getRandomX(1), target.getRandomY(), target.getRandomZ(1), dx, dy, dz);
                    }
                }
            }
        }
        double metalValue = Math.min(5, calcMetalValue(target));
        Vector3d vector = target.position().vectorTo(position());
        vector = new Vector3d(vector.x, 0, vector.z);
        double speedMul = target instanceof ProjectileEntity ? PROJECTILE_SPEED_MULTIPLIER : SPEED_MULTIPLIER;
        if (target instanceof PlayerEntity) {
            target.setDeltaMovement(target.getDeltaMovement().add(Vectors.rotate90(vector, Vector3d.ZERO, true).normalize().scale(speedMul).scale(metalValue * metalValue).scale(MathHelper.fastInvSqrt(vector.lengthSqr()))));
        } else {
            target.setDeltaMovement(target.getDeltaMovement().scale(0.5).add(Vectors.rotate90(vector, Vector3d.ZERO, true).normalize().scale(speedMul).scale(metalValue * metalValue).scale(MathHelper.fastInvSqrt(vector.lengthSqr()))));
        }
        target.push((getX() - target.getX()) * CENTRIPETAL_SPEED, 0, (getZ() - target.getZ()) * CENTRIPETAL_SPEED);

        if (clientside) {
            Random random = target instanceof LivingEntity ? ((LivingEntity) target).getRandom() : level.random;
            randomMagneticParticle(random, RedstoneParticles.LIGHT_BLUE, target);
            randomMagneticParticle(random, RedstoneParticles.RED, target);
        }
    }

    private void randomMagneticParticle(Random random, RedstoneParticleData particle, Entity target) {
        if (random.nextInt(100) == 0) {
            double dx = random.nextGaussian() * 0.02;
            double dy = random.nextGaussian() * 0.02;
            double dz = random.nextGaussian() * 0.02;
            level.addParticle(particle, target.getRandomX(1), target.getRandomY(), target.getRandomZ(1), dx, dy, dz);
        }
    }

    private static double calcMetalValue(Entity target) {
        double metalValue = 1;

        if (target instanceof PlayerEntity) {
            PlayerInventory inventory = ((PlayerEntity) target).inventory;
            metalValue += inventory.items.stream().mapToDouble(MagneticFieldGeneratorTileEntity::getItemMetalValue).sum();
        }

        String path = Utils.getRegistryName(target.getType()).getPath();
        if (VALID_METALS.stream().anyMatch(path::contains)) {
            metalValue *= 3;
        }

        return Math.log(metalValue) + 1;
    }

    private static double getItemMetalValue(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        double metalValue = ITEM_VALUE.getOrDefault(stack.getItem(), 0.0);
        for (Pair<Predicate<? super ItemStack>, Double> pair : ITEM_VALUE_EXTRA) {
            if (pair.getFirst().test(stack)) {
                metalValue += pair.getSecond();
                break;
            }
        }
        return metalValue * stack.getCount();
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        CompoundNBT saved = super.save(compoundNBT);
        saved.putInt("GlobalTickCount", globalTickCount);
        ListNBT projectilesToRemoveNBT = new ListNBT();
        for (Map.Entry<UUID, Integer> entry : projectilesToRemove.entrySet()) {
            CompoundNBT entryNBT = new CompoundNBT();
            entryNBT.putUUID("UUID", entry.getKey());
            entryNBT.putInt("Timestamp", entry.getValue());
            projectilesToRemoveNBT.add(entryNBT);
        }
        saved.put("ProjectilesToRemove", projectilesToRemoveNBT);
        return saved;
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        super.load(state, compoundNBT);
        globalTickCount = compoundNBT.getInt("GlobalTickCount");
        if (compoundNBT.contains("ProjectilesToRemove", Constants.NBT.TAG_LIST)) {
            projectilesToRemove.clear();
            ListNBT projectilesToRemoveNBT = compoundNBT.getList("ProjectilesToRemove", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < projectilesToRemoveNBT.size(); i++) {
                CompoundNBT entryNBT = projectilesToRemoveNBT.getCompound(i);
                UUID uuid = entryNBT.getUUID("UUID");
                int timestamp = entryNBT.getInt("Timestamp");
                projectilesToRemove.put(uuid, timestamp);
            }
        }
    }
}
