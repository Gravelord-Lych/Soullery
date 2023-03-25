package lych.soullery.util;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.*;
import lych.soullery.api.IRangedAttackGoal;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.functional.FangsEntity;
import lych.soullery.entity.iface.ITieredMob;
import lych.soullery.util.mixin.IBrainMixin;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class  EntityUtils {
    public static final EntityPredicate ALL = EntityPredicate.DEFAULT.allowUnseeable().allowInvulnerable().ignoreInvisibilityTesting().allowSameTeam().allowNonAttackable();
    public static final EntityPredicate ALL_ATTACKABLE = EntityPredicate.DEFAULT.allowUnseeable().ignoreInvisibilityTesting();

    private EntityUtils() {}

    public static Vector3d centerOf(Entity entity) {
        return entity.getBoundingBox().getCenter();
    }

    public static Vector3d eyeOf(Entity entity) {
        return new Vector3d(entity.getX(0.5), entity.getEyeY(), entity.getZ(0.5));
    }

    /**
     * [VanillaCopy]
     */
    public static boolean createFangs(double x, double z, double minY, double maxY, float angle, int warmupDelayTicks, LivingEntity owner, World level, Consumer<? super FangsEntity> consumer) {
        BlockPos blockPos = new BlockPos(x, maxY, z);
        boolean canSpawnFangs = false;
        double dyMax = 0.0D;

        do {
            BlockPos belowPos = blockPos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
                if (!level.isEmptyBlock(blockPos)) {
                    BlockState state = level.getBlockState(blockPos);
                    VoxelShape shape = state.getCollisionShape(level, blockPos);
                    if (!shape.isEmpty()) {
                        dyMax = shape.max(Direction.Axis.Y);
                    }
                }

                canSpawnFangs = true;
                break;
            }

            blockPos = blockPos.below();
        } while (blockPos.getY() >= MathHelper.floor(minY) - 1);

        if (canSpawnFangs) {
            FangsEntity fangs = new FangsEntity(level, x, (double) blockPos.getY() + dyMax, z, angle, warmupDelayTicks, owner);
            consumer.accept(fangs);
            return level.addFreshEntity(fangs);
        }
        return false;
    }

    public static <T> TierChoiceBuilder<T> choiceBuilder() {
        return new TierChoiceBuilder<>();
    }

    public static <T> TierChoiceBuilder<T> choiceBuilder(Int2ObjectMap<T> map) {
        return new TierChoiceBuilder<>(map);
    }

    public static IntTierChoiceBuilder intChoiceBuilder() {
        return new IntTierChoiceBuilder();
    }

    public static IntTierChoiceBuilder intChoiceBuilder(Int2IntMap map) {
        return new IntTierChoiceBuilder(map);
    }

    public static FloatTierChoiceBuilder floatChoiceBuilder() {
        return new FloatTierChoiceBuilder();
    }

    public static FloatTierChoiceBuilder floatChoiceBuilder(Int2FloatMap map) {
        return new FloatTierChoiceBuilder(map);
    }

    public static DoubleTierChoiceBuilder doubleChoiceBuilder() {
        return new DoubleTierChoiceBuilder();
    }

    public static DoubleTierChoiceBuilder doubleChoiceBuilder(Int2DoubleMap map) {
        return new DoubleTierChoiceBuilder(map);
    }

    public static boolean hasArmor(LivingEntity entity) {
        return entity.getArmorCoverPercentage() > 0;
    }

    public static void sharedShieldHitParticle(LivingEntity entity) {
        addParticlesAroundSelf(entity, RedstoneParticles.CYAN, 5);
        addParticlesAroundSelf(entity, ParticleTypes.POOF, 12);
    }

    public static void addParticlesAroundSelf(LivingEntity entity, IParticleData particle, int count) {
        addParticlesAroundSelf(entity, particle, 0.02, count);
    }

    public static void addParticlesAroundSelf(LivingEntity entity, IParticleData particle, double scale, int count) {
        for (int i = 0; i < count; ++i) {
            double xSpeed = entity.getRandom().nextGaussian() * scale;
            double ySpeed = entity.getRandom().nextGaussian() * scale;
            double zSpeed = entity.getRandom().nextGaussian() * scale;
            entity.level.addParticle(particle, entity.getRandomX(1), entity.getRandomY() + 1, entity.getRandomZ(1), xSpeed, ySpeed, zSpeed);
        }
    }

    public static void sharedShieldHitParticleServerside(Entity entity, ServerWorld world) {
        addParticlesAroundSelfServerside(entity, world, RedstoneParticles.CYAN, 5);
        addParticlesAroundSelfServerside(entity, world, ParticleTypes.POOF, 12);
    }

    public static void addParticlesAroundSelfServerside(Entity entity, ServerWorld world, IParticleData particle, int count) {
        addParticlesAroundSelfServerside(entity, world, particle, 0, count);
    }

    public static void addParticlesAroundSelfServerside(Entity entity, ServerWorld world, IParticleData particle, double speed, int count) {
        for (int i = 0; i < count; i++) {
            world.sendParticles(particle, entity.getRandomX(1), entity.getRandomY() + 1, entity.getRandomZ(1), 1, 0, 0, 0, speed);
        }
    }

    public static void spawnAnimServerside(LivingEntity entity, ServerWorld world) {
        for (int i = 0; i < 20; i++) {
            double x = entity.getRandom().nextGaussian() * 0.02D;
            double y = entity.getRandom().nextGaussian() * 0.02D;
            double z = entity.getRandom().nextGaussian() * 0.02D;
            world.sendParticles(ParticleTypes.POOF, entity.getX(1) - x * 10, entity.getRandomY() - y * 10, entity.getRandomZ(1) - z * 10, 1, 0, 0, 0, 0.02);
        }
    }

    public static void directlyAddGoal(GoalSelector selector, PrioritizedGoal goal) {
        ((IGoalSelectorMixin) selector).getAvailableGoals().add(goal);
    }

    public static ModifiableAttributeInstance getAttribute(LivingEntity entity, Attribute attribute) {
        return Objects.requireNonNull(entity.getAttribute(attribute), "If you call this method, you must ensure that your entity has the attribute");
    }

    public static boolean addPermanentModifierIfAbsent(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        if (!getAttribute(entity, attribute).hasModifier(modifier)) {
            getAttribute(entity, attribute).addPermanentModifier(modifier);
            return true;
        }
        return false;
    }

    public static boolean addTransientModifierIfAbsent(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        if (!getAttribute(entity, attribute).hasModifier(modifier)) {
            getAttribute(entity, attribute).addTransientModifier(modifier);
            return true;
        }
        return false;
    }

    public static Comparator<EntityType<?>> comparingEntityType() {
        return Comparator.comparing(type -> Utils.getRegistryName(type).toString());
    }

    public static Entity getLowestVehicle(Entity entity) {
        if (entity.getVehicle() == null) {
            return entity;
        }
        return getLowestVehicle(entity);
    }

    public static Stream<Entity> getAllEntitiesOnTheVehicle(Entity entity) {
        return getLowestVehicle(entity).getSelfAndPassengers();
    }

    public static void spawnAll(Entity entity, World world) {
        getAllEntitiesOnTheVehicle(entity).forEach(world::addFreshEntity);
    }

    public static List<Entity> getSelfAndVehicles(@Nullable Entity entity) {
        if (entity == null) {
            return Collections.emptyList();
        }
        if (!entity.isPassenger()) {
            return Collections.singletonList(entity);
        }
        ImmutableList.Builder<Entity> builder = ImmutableList.builder();
        for (Entity vehicle = entity; vehicle != null; vehicle = vehicle.getVehicle()) {
            builder.add(vehicle);
        }
        return builder.build();
    }

    public static boolean canReach(MobEntity mob, LivingEntity target) {
        Path path = mob.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        } else {
            PathPoint endNode = path.getEndNode();
            if (endNode == null) {
                return false;
            } else {
                int tx = endNode.x - MathHelper.floor(target.getX());
                int tz = endNode.z - MathHelper.floor(target.getZ());
                return tx * tx + tz * tz <= 1.5 * 1.5;
            }
        }
    }

    public static Vector3d bottomOf(Entity entity) {
        return BoundingBoxUtils.bottomOf(entity.getBoundingBox());
    }

    public static boolean shouldApplyEffect(LivingEntity entity, EffectInstance effect, boolean defaultValue) {
        PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(entity, effect);
        MinecraftForge.EVENT_BUS.post(event);
        return defaultValue ? event.getResult() != Event.Result.DENY : event.getResult() == Event.Result.ALLOW;
    }

    public static boolean isThorns(DamageSource source) {
        return source instanceof EntityDamageSource && ((EntityDamageSource) source).isThorns();
    }

    public static DamageSource livingAttack(LivingEntity entity) {
        return entity instanceof PlayerEntity ? DamageSource.playerAttack((PlayerEntity) entity) : DamageSource.mobAttack(entity);
    }

    public static boolean isMelee(DamageSource source) {
        if (source.isProjectile() || !(source instanceof EntityDamageSource)) {
            return false;
        }
        return ("mob".equals(source.getMsgId()) || "player".equals(source.getMsgId())) && !(source instanceof IndirectEntityDamageSource);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Class<T> type, Entity mainEntity, double range) {
        return getEntitiesInRange(type, mainEntity, range, entity -> true);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Class<T> type, Entity mainEntity, double range, Predicate<? super T> predicate) {
        List<T> entities = mainEntity.level.getEntitiesOfClass(type, mainEntity.getBoundingBox().inflate(range), predicate);
        entities.removeIf(e -> e.distanceTo(mainEntity) > range);
        return entities;
    }

    public static void checkGoalInstantiationServerside(Entity entity) {
        Preconditions.checkState(!entity.level.isClientSide(), "Cannot instantiate a goal clientside");
    }

    public static boolean isAlive(@Nullable Entity entity) {
        return entity != null && entity.isAlive();
    }

    public static boolean isDead(@Nullable Entity entity) {
        return entity != null && !entity.isAlive();
    }

    @SuppressWarnings("deprecation")
    public static void killCompletely(Entity entity) {
        entity.kill();
        if (entity.isAlive()) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).setHealth(0);
            }
            entity.remove();
            if (entity.isAlive()) {
                entity.removed = true;
                if (entity.isAlive()) {
                    entity.teleportTo(entity.getX() + 100000, Double.MIN_VALUE, entity.getZ() + 100000);
                }
            }
        }
    }

    public static void spawnItem(World world, BlockPos pos, ItemStack stack) {
        double x = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
        double y = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
        double z = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
        ItemEntity item = new ItemEntity(world, (double) pos.getX() + x, (double) pos.getY() + y, (double) pos.getZ() + z, stack);
        item.setDefaultPickUpDelay();
        world.addFreshEntity(item);
    }

    @Nullable
    public static LivingEntity getTarget(MobEntity mob) {
        return getTargetFromMemory(mob).orElseGet(mob::getTarget);
    }

    private static Optional<LivingEntity> getTargetFromMemory(MobEntity mob) {
        if (hasTargetMemory(mob)) {
            return mob.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        }
        return Optional.empty();
    }

    private static boolean hasTargetMemory(MobEntity mob) {
        return mob.getBrain().checkMemory(MemoryModuleType.ATTACK_TARGET, MemoryModuleStatus.REGISTERED);
    }

    public static void setTarget(MobEntity mob, @Nullable LivingEntity target) {
        if (hasTargetMemory(mob)) {
            if (target != null && (mob instanceof HoglinEntity || mob instanceof ZoglinEntity)) {
                mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, target, 200);
            } else {
                mob.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
            }
        } else {
            mob.setTarget(target);
        }
    }

    public static String nameAndUUIDToString(PlayerEntity player) {
        return String.format("%s (UUID: %s)", player.getDisplayName().getString(), player.getUUID());
    }

    public static ITextComponent getBossNameFor(ITieredMob entity) {
        return getBossNameFor(() -> ((Entity) entity).getDisplayName(), entity::getTier);
    }

    public static ITextComponent getBossNameFor(ITextComponent name, int tier) {
        return getBossNameFor(() -> name, () -> tier);
    }

    public static ITextComponent getBossNameFor(Supplier<ITextComponent> nameGetter, IntSupplier tierGetter) {
        return ConfigHelper.shouldShowBossTier() ? nameGetter.get().copy().append(" (").append("T").append(String.valueOf(tierGetter.getAsInt())).append(")") : nameGetter.get();
    }

    public static boolean canMobGrief(Entity entity) {
        return ForgeEventFactory.getMobGriefingEvent(entity.level, entity);
    }

    public static boolean canMobGrief(LivingEntity entity, BlockPos pos) {
        return ForgeHooks.canEntityDestroy(entity.level, pos, entity);
    }

    public static int removeEffect(LivingEntity entity, Predicate<? super EffectInstance> predicate) {
        List<EffectInstance> effectInstancesToRemove = entity.getActiveEffects().stream().filter(predicate).collect(Collectors.toList());
        List<Effect> effectsToRemove = effectInstancesToRemove.stream().map(EffectInstance::getEffect).collect(Collectors.toList());
        effectsToRemove.forEach(entity::removeEffect);
        if (effectsToRemove.isEmpty()) {
            return 0;
        }
        return effectInstancesToRemove.stream().mapToInt(effect -> effect.getDuration() * (effect.getAmplifier() + 1)).sum();
    }

    public static void clearInvulnerableTime(Entity entity) {
        entity.invulnerableTime = 10;
    }

    public static Optional<PlayerEntity> getPlayerOptional(World world, UUID uuid) {
        return Optional.ofNullable(world.getPlayerByUUID(uuid));
    }

    public static Optional<Entity> getEntityOptional(World world, UUID uuid) {
        return world instanceof ServerWorld ? Optional.ofNullable(((ServerWorld) world).getEntity(uuid)) : Optional.empty();
    }

    public static Optional<Entity> getEntityOptional(World world, UUID uuid, Predicate<? super Entity> predicate) {
        return world instanceof ServerWorld ? Optional.ofNullable(((ServerWorld) world).getEntity(uuid)).filter(predicate) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Optional<T> getEntityOptional(Class<? extends T> cls, World world, UUID uuid) {
        return world instanceof ServerWorld ? Optional.ofNullable(((ServerWorld) world).getEntity(uuid)).filter(entity -> cls.isAssignableFrom(entity.getClass())).map(entity -> (T) entity) : Optional.empty();
    }

    @Nullable
    public static EntityRayTraceResult getEntityRayTraceResult(Entity entity, double reachDistance) {
        return getEntityRayTraceResult(entity, reachDistance, true);
    }

    @Nullable
    public static EntityRayTraceResult getEntityRayTraceResult(Entity entity, double reachDistance, boolean testBlock) {
        Vector3d position = entity.getEyePosition(0);
        Vector3d viewVector = entity.getViewVector(1);
        Vector3d targetPos = position.add(viewVector.scale(reachDistance));
        if (testBlock) {
            BlockRayTraceResult blockRay = entity.level.clip(new RayTraceContext(position, targetPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
            if (blockRay.getType() != RayTraceResult.Type.MISS) {
                return null;
            }
        }
        AxisAlignedBB possibleEntitiesBB = entity.getBoundingBox().expandTowards(viewVector.scale(reachDistance)).inflate(1);
        return ProjectileHelper.getEntityHitResult(entity, position, targetPos, possibleEntitiesBB, entityIn -> !entityIn.isSpectator() && entityIn.isPickable(), reachDistance * reachDistance);
    }

    public static void disableShield(World world, IShieldUser user, @Nullable Random random) {
        user.onShieldExhausted();
        if (user.hasConsumableShield()) {
            user.setSharedShield(null);
            user.onShieldBreak();
            if (random != null && world instanceof ServerWorld) {
                addParticlesAroundSelfServerside((Entity) user, (ServerWorld) world, ParticleTypes.EXPLOSION, 5 + random.nextInt(3));
            }
        }
        if (random != null && user instanceof Entity) {
            ((Entity) user).playSound(ModSoundEvents.ENERGY_SOUND_BREAK.get(), 1, 1);
        }
    }

    public static void normalizeYRot(MobEntity operatingMob) {
        while (operatingMob.yRot >= 360) {
            operatingMob.yRot -= 360;
        }
    }

    public static void normalizeYHeadRot(MobEntity operatingMob) {
        while (operatingMob.yHeadRot >= 360) {
            operatingMob.yHeadRot -= 360;
        }
    }

    public static void normalizeYBodyRot(MobEntity operatingMob) {
        while (operatingMob.yBodyRot >= 360) {
            operatingMob.yBodyRot -= 360;
        }
    }

    public static boolean canSwim(MobEntity mob) {
        if (((IBrainMixin<?>) mob.getBrain()).isValidBrain()) {
            return ((IBrainMixin<?>) mob.getBrain()).canSwim();
        }
        return ((IGoalSelectorMixin) mob.goalSelector).getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof SwimGoal);
    }

    public static boolean canUseCrossbow(MobEntity mob) {
        if (!(mob instanceof ICrossbowUser)) {
            return false;
        }
        return ((IGoalSelectorMixin) mob.goalSelector).getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof SwimGoal);
    }


    public static boolean isWaterMob(MobEntity mob) {
        return mob.getMobType() == CreatureAttribute.WATER || mob instanceof DrownedEntity;
    }

    public static Optional<RangedAttackGoal> findRangedAttackGoal(MobEntity mob) {
        return ((IGoalSelectorMixin) mob.goalSelector).getAvailableGoals().stream().map(PrioritizedGoal::getGoal).filter(goalIn -> goalIn instanceof RangedAttackGoal).findFirst().map(goal -> (RangedAttackGoal) goal);
    }

    public static Optional<Goal> findAnyRangedAttackableGoal(MobEntity mob) {
        return ((IGoalSelectorMixin) mob.goalSelector).getAvailableGoals().stream().map(PrioritizedGoal::getGoal).filter(goalIn -> goalIn instanceof RangedAttackGoal || goalIn instanceof RangedBowAttackGoal<?> || goalIn instanceof RangedCrossbowAttackGoal<?> || goalIn instanceof IRangedAttackGoal).findFirst();
    }

    @FieldsAreNullableByDefault
    public static class TierChoiceBuilder<T> {
        @NotNull
        private final Int2ObjectMap<T> choiceMap;
        private T value;
        private int[] range;

        private TierChoiceBuilder() {
            choiceMap = new Int2ObjectOpenHashMap<>();
        }

        private TierChoiceBuilder(Int2ObjectMap<T> choiceMap) {
            this.choiceMap = choiceMap;
        }

        public TierChoiceBuilder<T> range(int num) {
            return range(num, num);
        }

        /**
         * @param min Inclusive
         * @param max Inclusive
         */
        public TierChoiceBuilder<T> range(int min, int max) {
            Preconditions.checkArgument(min > 0 && max >= min, "Illegal min and max value");
            Preconditions.checkState(value == null, "Please specify the range first");
            this.range = IntStream.rangeClosed(min, max).toArray();
            return this;
        }

        public TierChoiceBuilder<T> value(T value) {
            Objects.requireNonNull(value, "Value should be non-null");
            Preconditions.checkState(range != null, "Please specify the range first");
            this.value = value;
            for (int tier : range) {
                choiceMap.put(tier, this.value);
            }
            this.value = null;
            this.range = null;
            return this;
        }

        public Int2ObjectMap<T> build() {
            Preconditions.checkState(range == null, "You specified a range without a value, please assign a value");
            return Int2ObjectMaps.unmodifiable(new Int2ObjectOpenHashMap<>(choiceMap));
        }
    }

    public static class IntTierChoiceBuilder {
        private final Int2IntMap choiceMap;
        private int[] range;

        private IntTierChoiceBuilder() {
            this(new Int2IntOpenHashMap());
        }

        private IntTierChoiceBuilder(Int2IntMap choiceMap) {
            this.choiceMap = choiceMap;
        }

        public IntTierChoiceBuilder range(int num) {
            return range(num, num);
        }

        /**
         * @param min Inclusive
         * @param max Inclusive
         */
        public IntTierChoiceBuilder range(int min, int max) {
            Preconditions.checkArgument(min > 0 && max >= min, "Illegal min and max value");
            Preconditions.checkState(range == null, "There has already been a range");
            this.range = IntStream.rangeClosed(min, max).toArray();
            return this;
        }

        public IntTierChoiceBuilder value(int value) {
            Preconditions.checkState(range != null, "Please specify the range first");
            for (int tier : range) {
                choiceMap.put(tier, value);
            }
            range = null;
            return this;
        }

        public Int2IntMap build() {
            Preconditions.checkState(range == null, "You specified a range without a value, please assign a value");
            return Int2IntMaps.unmodifiable(new Int2IntOpenHashMap(choiceMap));
        }
    }

    public static class FloatTierChoiceBuilder {
        private final Int2FloatMap choiceMap;
        private int[] range;

        private FloatTierChoiceBuilder() {
            this(new Int2FloatOpenHashMap());
        }

        private FloatTierChoiceBuilder(Int2FloatMap choiceMap) {
            this.choiceMap = choiceMap;
        }

        public FloatTierChoiceBuilder range(int num) {
            return range(num, num);
        }

        /**
         * @param min Inclusive
         * @param max Inclusive
         */
        public FloatTierChoiceBuilder range(int min, int max) {
            Preconditions.checkArgument(min > 0 && max >= min, "Illegal min and max value");
            this.range = IntStream.rangeClosed(min, max).toArray();
            return this;
        }

        public FloatTierChoiceBuilder value(float value) {
            Preconditions.checkState(range != null, "Please specify the range first");
            for (int tier : range) {
                choiceMap.put(tier, value);
            }
            range = null;
            return this;
        }

        public Int2FloatMap build() {
            Preconditions.checkState(range == null, "You specified a range without a value, please assign a value");
            return Int2FloatMaps.unmodifiable(new Int2FloatOpenHashMap(choiceMap));
        }
    }

    public static class DoubleTierChoiceBuilder {
        private final Int2DoubleMap choiceMap;
        private int[] range;

        private DoubleTierChoiceBuilder() {
            this(new Int2DoubleOpenHashMap());
        }

        private DoubleTierChoiceBuilder(Int2DoubleMap choiceMap) {
            this.choiceMap = choiceMap;
        }

        public DoubleTierChoiceBuilder range(int num) {
            return range(num, num);
        }

        /**
         * @param min Inclusive
         * @param max Inclusive
         */
        public DoubleTierChoiceBuilder range(int min, int max) {
            Preconditions.checkArgument(min > 0 && max >= min, "Illegal min and max value");
            this.range = IntStream.rangeClosed(min, max).toArray();
            return this;
        }

        public DoubleTierChoiceBuilder value(double value) {
            Preconditions.checkState(range != null, "Please specify the range first");
            for (int tier : range) {
                choiceMap.put(tier, value);
            }
            range = null;
            return this;
        }

        public Int2DoubleMap build() {
            Preconditions.checkState(range == null, "You specified a range without a value, please assign a value");
            return Int2DoubleMaps.unmodifiable(new Int2DoubleOpenHashMap(choiceMap));
        }
    }
}
