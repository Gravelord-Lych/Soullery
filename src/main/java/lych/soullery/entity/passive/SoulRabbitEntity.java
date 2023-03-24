package lych.soullery.entity.passive;

import com.google.common.collect.ImmutableList;
import lych.soullery.Soullery;
import lych.soullery.block.plant.SoulWartBlock;
import lych.soullery.config.ConfigHelper;
import lych.soullery.entity.ModEntities;
import lych.soullery.item.ModItems;
import lych.soullery.tag.ModBlockTags;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.util.WeightedRandom;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import lych.soullery.world.gen.biome.ModBiomes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SoulRabbitEntity extends RabbitEntity {
    private boolean initialSet = true;

    public SoulRabbitEntity(EntityType<? extends SoulRabbitEntity> type, World world) {
        super(type, world);
        setSoulRabbitType(Type.DEFAULT);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        clearGoals();
        goalSelector.addGoal(3, new TemptGoal(this, 1, Ingredient.of(ModItems.SOUL_WART), false));
        goalSelector.addGoal(5, new EatSoulWartsGoal(this));
    }

    public static boolean checkSoulRabbitSpawnRules(EntityType<SoulRabbitEntity> type, IWorld world, SpawnReason reason, BlockPos pos, Random random) {
        BlockState state = world.getBlockState(pos.below());
        return state.is(ModBlockTags.SOUL_RABBIT_SPAWNABLE_BLOCKS);
    }

    private void clearGoals() {
        ((IGoalSelectorMixin) goalSelector).getAvailableGoals().removeIf(g -> g.getGoal() instanceof TemptGoal || g.getGoal() instanceof MoveToBlockGoal);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == ModItems.SOUL_WART;
    }

    @Override
    public SoulRabbitEntity getBreedOffspring(ServerWorld world, AgeableEntity entity) {
        SoulRabbitEntity rabbit = ModEntities.SOUL_RABBIT.create(world);
        Type randomType = Type.byId(getRandomRabbitType(world));
        if (random.nextInt(20) != 0) {
            if (entity instanceof SoulRabbitEntity && random.nextBoolean()) {
                randomType = ((SoulRabbitEntity) entity).getSoulRabbitType();
            } else {
                randomType = getSoulRabbitType();
            }
        }
        rabbit.setSoulRabbitType(randomType);
        return rabbit;
    }

    public void setSoulRabbitType(Type type) {
        super.setRabbitType(type.getId());
    }

    public Type getSoulRabbitType() {
        return Type.byId(getRabbitType());
    }

    /**
     * Use {@link SoulRabbitEntity#setSoulRabbitType(Type)}
     */
    @Deprecated
    @Override
    public void setRabbitType(int type) {
//      Vanilla hardcode causes this additional step
        if (type == 0 && initialSet) {
            initialSet = false;
            return;
        }
        initialSet = false;
        super.setRabbitType(Type.byId(type).getId());
    }

    @Override
    protected SoundEvent getJumpSound() {
        return ModSoundEvents.SOUL_RABBIT_JUMP.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.SOUL_RABBIT_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.SOUL_RABBIT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_RABBIT_DEATH.get();
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(sound == SoundEvents.RABBIT_ATTACK ? ModSoundEvents.SOUL_RABBIT_ATTACK.get() : sound, volume, pitch);
    }

    @Override
    protected int getRandomRabbitType(IWorld world) {
        RegistryKey<Biome> biome = world.getBiomeName(blockPosition()).orElse(null);
        if (BiomeDictionary.hasType(biome, ModBiomes.PURE_TYPE)) {
            return Type.BLUE.getId();
        }
        if (BiomeDictionary.hasType(biome, ModBiomes.SOUL_TYPE)) {
            return WeightedRandom.getRandomItem(getRandom(), Type.getSoulTypes()).getId();
        }
        if (BiomeDictionary.hasType(biome, ModBiomes.SOUL_LAND_TYPE) && BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY)) {
            return Type.RED.getId();
        }
        return WeightedRandom.getRandomItem(getRandom(), ImmutableList.copyOf(Type.values())).getId();
    }

    public enum Type implements IIdentifiableEnum, WeightedRandom.Item {
        RED(3969, 5, false, "red.png"),
        CYAN(3970, 100, true, "cyan.png"),
        BLUE(3971, 50, true, "blue.png");

        private static final LazyValue<List<Type>> SOUL_TYPES = new LazyValue<>(() -> Arrays.stream(values()).filter(Type::isSoulType).collect(ImmutableList.toImmutableList()));
        private static final Type DEFAULT = CYAN;
        private final int id;
        private final int weight;
        private final boolean soulType;
        private final ResourceLocation textureLocation;

        Type(int id, int weight, boolean soulType, String textureName) {
            this(id, weight, soulType, Soullery.prefixTex("entity/soul_rabbit/" + textureName));
        }

        Type(int id, int weight, boolean soulType, ResourceLocation textureLocation) {
            this.id = id;
            this.weight = weight;
            this.soulType = soulType;
            this.textureLocation = textureLocation;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public int getWeight() {
            return weight;
        }

        public static Type byId(int id) {
            try {
                return IIdentifiableEnum.byId(values(), id);
            } catch (EnumConstantNotFoundException e) {
                if (ConfigHelper.shouldFailhard()) {
                    throw new RuntimeException(ConfigHelper.FAILHARD_MESSAGE + String.format("Soul Rabbit's type not found. Current id: %d, Available id: %s",
                            e.getId(),
                            Arrays.toString(Arrays.stream(values()).mapToInt(Type::getId).toArray())));
                }
                return DEFAULT;
            }
        }

        public boolean isSoulType() {
            return soulType;
        }

        public static List<Type> getSoulTypes() {
            return SOUL_TYPES.get();
        }

        public ResourceLocation getTextureLocation() {
            return textureLocation;
        }
    }

    public static class EatSoulWartsGoal extends MoveToBlockGoal {
        private final RabbitEntity rabbit;
        private boolean canRaid;

        public EatSoulWartsGoal(RabbitEntity rabbit) {
            super(rabbit, 0.7F, 16);
            this.rabbit = rabbit;
        }

        @Override
        public boolean canUse() {
            if (nextStartTick <= 0) {
                if (!ForgeEventFactory.getMobGriefingEvent(this.rabbit.level, this.rabbit)) {
                    return false;
                }
                canRaid = false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return canRaid && super.canContinueToUse();
        }

        @Override
        public void tick() {
            super.tick();
            rabbit.getLookControl().setLookAt((double) blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5, 10, rabbit.getMaxHeadXRot());
            if (isReachedTarget()) {
                World world = rabbit.level;
                BlockPos pos = blockPos.above();
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (canRaid && block instanceof SoulWartBlock) {
                    int age = state.getValue(SoulWartBlock.AGE);
                    if (age == 0) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
                        world.destroyBlock(pos, true, rabbit);
                    } else {
                        world.setBlock(pos, state.setValue(SoulWartBlock.AGE, age - 1), Constants.BlockFlags.BLOCK_UPDATE);
                        world.levelEvent(2001, pos, Block.getId(state));
                    }
                }
                canRaid = false;
                nextStartTick = 10;
            }
        }

        @Override
        protected boolean isValidTarget(IWorldReader reader, BlockPos pos) {
            if (!canRaid) {
                pos = pos.above();
                BlockState state = reader.getBlockState(pos);
//              Targets max-aged soul wart
                if (state.getBlock() instanceof SoulWartBlock && !state.getBlock().isRandomlyTicking(state)) {
                    canRaid = true;
                    return true;
                }
            }
            return false;
        }
    }
}
