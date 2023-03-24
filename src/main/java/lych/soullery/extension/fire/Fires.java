package lych.soullery.extension.fire;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lych.soullery.Soullery;
import lych.soullery.api.event.RegisterFiresEvent;
import lych.soullery.block.ModBlocks;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.mixin.FireBlockAccessor;
import lych.soullery.tag.ModFluidTags;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.FireRenderMaterials;
import lych.soullery.util.ModConstants;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Fires {
    static final Marker FIRE_MARKER = MarkerManager.getMarker("Fires");
    static final Map<Block, Fire> FIRES = new HashMap<>();
    static final BiMap<Integer, Fire> FIRE_IDS = HashBiMap.create();
    static int currentID = 1;
    static final Fire NO_FIRE = Fire.noFire();
    public static final Fire FIRE = createAndRegister(new FireProperties()
            .setBlock(Blocks.FIRE)
            .setSpecialDegree(0)
            .handler(DefaultFireHandler.INSTANCE));
    public static final Fire INFERNO = createAndRegister(new FireProperties()
            .setBlock(ModBlocks.INFERNO)
            .useOverlays(FireRenderMaterials.INFERNO_0, FireRenderMaterials.INFERNO_1)
            .noLava()
            .withDamage(ModConstants.INFERNO_DAMAGE)
            .withPriority(70));
    public static final Fire POISONOUS_FIRE = createAndRegister(new FireProperties()
            .setBlock(ModBlocks.POISONOUS_FIRE)
            .useOverlays(FireRenderMaterials.POISONOUS_FIRE_0, FireRenderMaterials.POISONOUS_FIRE_1)
            .noLava()
            .withDamage(ModConstants.POISONOUS_FIRE_DAMAGE));
    public static final Fire PURE_SOUL_FIRE = createAndRegister(new FireProperties()
            .setBlock(ModBlocks.PURE_SOUL_FIRE)
            .useOverlays(FireRenderMaterials.PURE_SOUL_FIRE_0, FireRenderMaterials.PURE_SOUL_FIRE_1)
            .handler(PureSoulFireHandler.INSTANCE)
            .noLava()
            .withDamage(ModConstants.PURE_SOUL_FIRE_DAMAGE)
            .withPriority(80));
    public static final Fire SOUL_FIRE = createAndRegister(new FireProperties()
            .setBlock(Blocks.SOUL_FIRE)
            .useOverlays(FireRenderMaterials.SOUL_FIRE_0, FireRenderMaterials.SOUL_FIRE_1)
            .handler(SoulFireHandler.INSTANCE)
            .withCustomLava(ModFluidTags.SOUL_LAVA)
            .withDamage(2)
            .withPriority(90));

    static {
        onRegisteringFire();
    }

    static int nextID() {
        return currentID++;
    }

    private Fires() {}

    public static void init() {
        Soullery.LOGGER.info(FIRE_MARKER, "Registering fires...");
    }

    private static void onRegisteringFire() {
        FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterFiresEvent());
    }

    private static Fire createAndRegister(FireProperties properties) {
        Fire fire = Fire.create(properties);
        Fire.register(fire);
        return fire;
    }

    public enum DefaultFireHandler implements Fire.Handler {
        INSTANCE;

        @Override
        public BlockState getState(IBlockReader reader, BlockPos pos, Fire fire) {
            if (!(fire.getBlock() instanceof FireBlock)) {
                throw new IllegalStateException(fire.getBlock().getRegistryName() + " is not compatible with " + getClass().getSimpleName());
            }
            return ((FireBlockAccessor) fire.getBlock()).callGetStateForPlacement(reader, pos);
        }
    }

    public enum PureSoulFireHandler implements ISoulFireHandler {
        INSTANCE;

        private static final UUID SLOWNESS_UUID = UUID.fromString("7F8258C3-D71D-4D84-A3A2-9B54A3BEF6CB");
        private static final AttributeModifier SLOWNESS = new AttributeModifier(SLOWNESS_UUID, "Pure soul fire slowness", -0.1, AttributeModifier.Operation.MULTIPLY_TOTAL);
        private static final UUID WEAKNESS_UUID = UUID.fromString("3D177866-A9B7-D288-9F37-5238006955E7");
        private static final AttributeModifier WEAKNESS = new AttributeModifier(WEAKNESS_UUID, "Pure soul fire weakness", -1, AttributeModifier.Operation.ADDITION);

        @Override
        public void startApplyingTo(Entity entity, Fire newFire, Fire oldFire) {
            if (entity instanceof LivingEntity && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(entity)) {
                LivingEntity living = (LivingEntity) entity;
                if (living.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                    EntityUtils.addTransientModifierIfAbsent(living, Attributes.MOVEMENT_SPEED, SLOWNESS);
                }
                if (living.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                    EntityUtils.addTransientModifierIfAbsent(living, Attributes.ATTACK_DAMAGE, WEAKNESS);
                }
            }
        }

        @Override
        public void stopApplyingTo(Entity entity, Fire oldFire, Fire newFireOrEmpty) {
            if (entity instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) entity;
                if (living.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                    EntityUtils.getAttribute((LivingEntity) entity, Attributes.MOVEMENT_SPEED).removeModifier(SLOWNESS_UUID);
                }
                if (living.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
                    EntityUtils.getAttribute((LivingEntity) entity, Attributes.ATTACK_DAMAGE).removeModifier(WEAKNESS_UUID);
                }
            }
        }
    }

    public enum SoulFireHandler implements ISoulFireHandler {
        INSTANCE;

        @Override
        public boolean canBlockCatchFire(IBlockReader reader, BlockPos firePos, BlockState state, Fire fire) {
            return SoulFireBlock.canSurviveOnBlock(state.getBlock());
        }
    }

    public interface ISoulFireHandler extends Fire.Handler {
        @Override
        default Fire applyTo(Entity entity, Fire fire) {
            return entity instanceof PlayerEntity && ExtraAbility.SOUL_INVULNERABILITY.isOn((PlayerEntity) entity) ? Fires.FIRE : fire;
        }
    }
}
