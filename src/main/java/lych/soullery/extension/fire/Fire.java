package lych.soullery.extension.fire;

import com.google.common.base.MoreObjects;
import com.mojang.datafixers.util.Pair;
import lych.soullery.Soullery;
import lych.soullery.block.IExtendedFireBlock;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IAbstractFireBlockMixin;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Fire {
    public static final Pair<RenderMaterial, RenderMaterial> DEFAULT_FIRE_OVERLAYS = Pair.of(ModelBakery.FIRE_0, ModelBakery.FIRE_1);
    public static final int DEFAULT_PRIORITY = 100;
    private static final Handler DUMMY_HANDLER = new Handler(){};
    @Nullable
    private final Pair<RenderMaterial, RenderMaterial> fireOverlays;
    private final Block fireBlock;
    private final Block[] additionalFireBlocks;
    @Nullable
    private final ITag<Fluid> lavaTag;
    private final Handler handler;
    private final float fireDamage;
    private final int priority;
    private final int specialDegree;

    Fire(Block fireBlock, @Nullable Pair<RenderMaterial, RenderMaterial> fireOverlays, Block[] additionalFireBlocks, @Nullable ITag<Fluid> lavaTag, Handler handler, float fireDamage, int priority, int specialDegree) {
        this.fireBlock = fireBlock;
        this.fireOverlays = fireOverlays;
        this.additionalFireBlocks = additionalFireBlocks;
        this.lavaTag = lavaTag;
        this.handler = handler;
        this.fireDamage = fireDamage;
        this.priority = priority;
        this.specialDegree = specialDegree;
    }

    public static Handler noHandlerNeeded() {
        return DUMMY_HANDLER;
    }

    static Fire createNoFire() {
        Fire noFire = new Fire(Blocks.AIR, null, new Block[]{}, null, noHandlerNeeded(), 0, Integer.MAX_VALUE, 0);
        Fires.FIRES.put(noFire.getBlock(), noFire);
        Fires.FIRE_IDS.put(0, noFire);
        return noFire;
    }

    public static Fire create(FireProperties properties) {
        return new Fire(properties.fireBlock, properties.fireOverlays, properties.additionalFireBlocks, properties.lavaTag, properties.handler, properties.fireDamage, properties.priority, properties.specialDegree);
    }

    public static void register(Fire fire) {
        Fire oldFire = registerFireType(fire.getBlock(), fire);
        if (oldFire == null) {
            return;
        }
        throw new UnsupportedOperationException(String.format("Fire block %s has already bound fire type!", fire.getBlock().getRegistryName()));
    }

    public static Fire autoCreateFireFor(AbstractFireBlock block) {
        return Fire.create(new FireProperties().setBlock(block).withDamage(((IAbstractFireBlockMixin) block).getFireDamage()));
    }

    public static List<Fire> getTrueFires() {
        return Fires.FIRES.values().stream().filter(Fire::isRealFire).sorted(Comparator.comparingInt(Fire::getSpecialDegree).reversed().thenComparing(Fire::getPriority)).collect(Collectors.toList());
    }

    @Nullable
    private static Fire registerFireType(Block block, Fire fire) {
        Fire oldFire = Fires.FIRES.putIfAbsent(block, fire);
        if (oldFire == null) {
            Fires.FIRE_IDS.put(Fires.nextID(), fire);
            Arrays.stream(fire.getAdditionalFireBlocks()).forEach(blockIn -> registerFireType(blockIn, fire));
        }
        return oldFire;
    }

    public static Fire empty() {
        return Fires.NO_FIRE;
    }

    public int getId() {
        return Fires.FIRE_IDS.inverse().get(this);
    }

    public void writeToNBT(CompoundNBT compoundNBT, String name) {
        compoundNBT.putString(name, Utils.getRegistryName(fireBlock, "FireBlock is not found").toString());
    }

    public static Fire fromNBT(CompoundNBT compoundNBT, String name) {
        String blockName = compoundNBT.getString(name);
        ResourceLocation location = new ResourceLocation(blockName);
        if (ForgeRegistries.BLOCKS.containsKey(location)) {
            Block block = ForgeRegistries.BLOCKS.getValue(location);
            if (block != null) {
                return byBlock(block);
            }
        }
        return warnAndUseDefault(String.format("No FireBlock named %s found, used default", location));
    }

    public static boolean isFireBlock(Block block) {
        return Fires.FIRES.entrySet().stream().filter(e -> e.getValue().isRealFire()).anyMatch(e -> e.getKey() == block);
    }

    public static Fire byBlock(Block block) {
        Fire fire = Fires.FIRES.get(block);
        if (fire == null && block instanceof AbstractFireBlock) {
            AbstractFireBlock fireBlock = (AbstractFireBlock) block;
            fire = Fire.autoCreateFireFor(fireBlock);
        }
        if (fire == null) {
            return warnAndUseDefault(String.format("No fire matches FireBlock %s, used default", block.getRegistryName()));
        }
        return fire;
    }

    public static Fire byId(int id) {
        Fire fire = Fires.FIRE_IDS.get(id);
        if (fire == null) {
            return warnAndUseDefault(String.format("No fire with id %d found, used default", id));
        }
        return fire;
    }

    private static Fire warnAndUseDefault(String message) {
        Soullery.LOGGER.warn(Fires.FIRE_MARKER, message);
        return empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fireOverlays", fireOverlays)
                .add("fireBlock", fireBlock)
                .add("additionalFireBlocks", additionalFireBlocks)
                .add("lavaTag", lavaTag)
                .add("handler", handler)
                .add("fireDamage", fireDamage)
                .add("priority", priority)
                .add("specialDegree", specialDegree)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fire fire = (Fire) o;
        return Float.compare(fire.fireDamage, fireDamage) == 0 && getPriority() == fire.getPriority() && getSpecialDegree() == fire.getSpecialDegree() && Objects.equals(getFireOverlays(), fire.getFireOverlays()) && Objects.equals(fireBlock, fire.fireBlock) && Arrays.equals(getAdditionalFireBlocks(), fire.getAdditionalFireBlocks()) && Objects.equals(getLavaTag(), fire.getLavaTag()) && Objects.equals(handler, fire.handler);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getFireOverlays(), fireBlock, getLavaTag(), handler, fireDamage, getPriority(), getSpecialDegree());
        result = 31 * result + Arrays.hashCode(getAdditionalFireBlocks());
        return result;
    }

    public boolean canBlockCatchFire(IBlockReader reader, BlockPos firePos, BlockState state) {
        return handler.canBlockCatchFire(reader, firePos, state, this);
    }

    public BlockState getState(IBlockReader reader, BlockPos pos) {
        return handler.getState(reader, pos, this);
    }

    public Block getBlock() {
        return fireBlock;
    }

    public float getFireDamage(Entity entity, World world) {
        return handler.getFireDamage(entity, world, this);
    }

    public float getDefaultFireDamage() {
        return fireDamage;
    }

    public void entityInsideFire(BlockState fireBlockState, World world, BlockPos pos, Entity entity) {
        handler.entityInsideFire(fireBlockState, world, pos, entity, this);
    }

    public void entityOnFire(Entity entity) {
        handler.entityOnFire(entity, this);
    }

    public boolean isRealFire() {
        return this != Fires.NO_FIRE;
    }

    public int getPriority() {
        return priority;
    }

    public boolean canApplyTo(Entity entity) {
        return !(isRealFire() && immune(entity)) && handler.canApplyTo(entity, this);
    }

    public Fire applyTo(Entity entity) {
        return handler.applyTo(entity, this);
    }

    public void startApplyingTo(Entity entity, Fire oldFire) {
        handler.startApplyingTo(entity, this, oldFire);
    }

    public void stopApplyingTo(Entity entity, Fire newFireOrEmpty) {
        handler.stopApplyingTo(entity, this, newFireOrEmpty);
    }

    public boolean canReplace(Fire oldFire) {
        return canReplace(this, oldFire);
    }

    public static boolean canReplace(Fire fire, Fire oldFire) {
        if (!fire.isRealFire() && oldFire.isRealFire()) {
            return true;
        }
        return fire.getPriority() <= oldFire.getPriority();
    }

    private static boolean immune(Entity entity) {
        return entity.fireImmune() || entity instanceof LivingEntity && ((LivingEntity) entity).hasEffect(Effects.FIRE_RESISTANCE);
    }

    public Pair<RenderMaterial, RenderMaterial> getFireOverlays() {
        return Utils.getOrDefault(fireOverlays, DEFAULT_FIRE_OVERLAYS);
    }

    @Nullable
    public ITag<Fluid> getLavaTag() {
        return lavaTag;
    }

    public Block[] getAdditionalFireBlocks() {
        return additionalFireBlocks;
    }

    public int getSpecialDegree() {
        return specialDegree;
    }

    @SuppressWarnings("unused")
    public interface Handler {
        default boolean canBlockCatchFire(IBlockReader reader, BlockPos firePos, BlockState state, Fire fire) {
            if (fire.getBlock() instanceof IExtendedFireBlock) {
                return ((IExtendedFireBlock) fire.getBlock()).canSurviveOnBlock(state.getBlock());
            }
            if (fire == Fires.FIRE) {
                return true;
            }
            throw new IllegalStateException();
        }

        default BlockState getState(IBlockReader reader, BlockPos pos, Fire fire) {
            return fire.getBlock().defaultBlockState();
        }

        default float getFireDamage(Entity entity, World world, Fire fire) {
            return fire.getDefaultFireDamage();
        }

        default boolean canApplyTo(Entity entity, Fire fire) {
            return true;
        }

        default Fire applyTo(Entity entity, Fire fire) {
            return entity.fireImmune() || entity instanceof LivingEntity && ((LivingEntity) entity).hasEffect(Effects.FIRE_RESISTANCE) ? Fire.empty() : fire;
        }

        default void startApplyingTo(Entity entity, Fire newFire, Fire oldFire) {}

        default void entityInsideFire(BlockState fireBlockState, World world, BlockPos pos, Entity entity, Fire fire) {}

        default void entityOnFire(Entity entity, Fire fire) {}

        default void stopApplyingTo(Entity entity, Fire oldFire, Fire newFireOrEmpty) {}
    }
}
