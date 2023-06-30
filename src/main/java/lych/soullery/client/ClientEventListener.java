package lych.soullery.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soullery.Soullery;
import lych.soullery.block.ModBlocks;
import lych.soullery.client.gui.screen.ExtraAbilityScreen;
import lych.soullery.client.gui.screen.SEGeneratorScreen;
import lych.soullery.client.gui.screen.SEStorageScreen;
import lych.soullery.client.gui.screen.SoulReinforcementTableScreen;
import lych.soullery.client.render.renderer.ModEntityRenderers;
import lych.soullery.client.render.world.dimension.ModDimensionRenderers;
import lych.soullery.client.shader.ModShaders;
import lych.soullery.effect.ModEffects;
import lych.soullery.effect.SoulPollutionHandler;
import lych.soullery.extension.control.MindOperatorSynchronizer;
import lych.soullery.gui.container.ModContainers;
import lych.soullery.item.EnderLauncherItem;
import lych.soullery.item.ModItems;
import lych.soullery.item.SoulBowItem;
import lych.soullery.network.StaticStatusHandler;
import lych.soullery.util.SoulEnergies;
import lych.soullery.util.mixin.IClientPlayerMixin;
import lych.soullery.util.mixin.IEntityMixin;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public final class ClientEventListener {
    private ClientEventListener() {}

//    public static final KeyBinding MESSAGE_KEY = new KeyBinding("key.message",
//            KeyConflictContext.IN_GAME,
//            KeyModifier.CONTROL,
//            InputMappings.Type.KEYSYM,
//            GLFW.GLFW_KEY_J,
//            "key.category." + SoulCraft.MOD_ID);

    @Mod.EventBusSubscriber(modid = Soullery.MOD_ID, value = Dist.CLIENT)
    public static class ForgeEventListener {
        protected static final ResourceLocation ITEM_VANISHING_LOCATION = Soullery.prefixTex("gui/item_vanishing.png");

        private ForgeEventListener() {}

        @SubscribeEvent
        public static void renderLasers(RenderWorldLastEvent event) {
            //locateTileEntities(Minecraft.getInstance().player, event.getMatrixStack());
            event.getMatrixStack().pushPose();
            LaserRenderingManager.getInstance().render(event.getMatrixStack());
            event.getMatrixStack().popPose();
        }

        private static void locateTileEntities(ClientPlayerEntity player, MatrixStack matrixStack) {
            IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            IVertexBuilder builder = buffer.getBuffer(RenderType.LINES);

            BlockPos playerPos = player.blockPosition();
            int px = playerPos.getX();
            int py = playerPos.getY();
            int pz = playerPos.getZ();
            World world = player.level;

            matrixStack.pushPose();

            Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

            Matrix4f positionMatrix = matrixStack.last().pose();

            BlockPos.Mutable pos = new BlockPos.Mutable();
            for (int dx = -10; dx <= 10; dx++) {
                for (int dy = -10; dy <= 10; dy++) {
                    for (int dz = -10; dz <= 10; dz++) {
                        pos.set(px + dx, py + dy, pz + dz);
                        if (world.getBlockEntity(pos) != null) {
                            blue(builder, positionMatrix, pos);
                        }
                    }
                }
            }

            matrixStack.popPose();

            RenderSystem.disableDepthTest();
            buffer.endBatch(RenderType.LINES);
        }

        public static void blue(IVertexBuilder builder, Matrix4f positionMatrix, BlockPos pos) {
            blueLine(builder, positionMatrix, pos, 0, 0, 0, 1, 0, 0);
            blueLine(builder, positionMatrix, pos, 0, 1, 0, 1, 1, 0);
            blueLine(builder, positionMatrix, pos, 0, 0, 1, 1, 0, 1);
            blueLine(builder, positionMatrix, pos, 0, 1, 1, 1, 1, 1);

            blueLine(builder, positionMatrix, pos, 0, 0, 0, 0, 0, 1);
            blueLine(builder, positionMatrix, pos, 1, 0, 0, 1, 0, 1);
            blueLine(builder, positionMatrix, pos, 0, 1, 0, 0, 1, 1);
            blueLine(builder, positionMatrix, pos, 1, 1, 0, 1, 1, 1);

            blueLine(builder, positionMatrix, pos, 0, 0, 0, 0, 1, 0);
            blueLine(builder, positionMatrix, pos, 1, 0, 0, 1, 1, 0);
            blueLine(builder, positionMatrix, pos, 0, 0, 1, 0, 1, 1);
            blueLine(builder, positionMatrix, pos, 1, 0, 1, 1, 1, 1);
        }

        public static void blueLine(IVertexBuilder builder, Matrix4f positionMatrix, BlockPos pos, float dx1, float dy1, float dz1, float dx2, float dy2, float dz2) {
            builder.vertex(positionMatrix, pos.getX()+dx1, pos.getY()+dy1, pos.getZ()+dz1)
                    .color(0.0f, 0.0f, 1.0f, 1.0f)
                    .endVertex();
            builder.vertex(positionMatrix, pos.getX()+dx2, pos.getY()+dy2, pos.getZ()+dz2)
                    .color(0.0f, 0.0f, 1.0f, 1.0f)
                    .endVertex();
        }

        @SubscribeEvent
        public static void onInputUpdate(InputUpdateEvent event) {
            MovementInput input = event.getMovementInput();
            if (((IEntityMixin) event.getPlayer()).isReversed()) {
                input.leftImpulse = -input.leftImpulse;
                input.forwardImpulse = -input.forwardImpulse;
            }
            ((IPlayerEntityMixin) event.getPlayer()).setStatic(input.leftImpulse == 0 && input.forwardImpulse == 0 && !input.jumping);
            StaticStatusHandler.INSTANCE.sendToServer(((IPlayerEntityMixin) event.getPlayer()).isStatic());
            if (event.getPlayer().hasEffect(ModEffects.SOUL_POLLUTION)) {
                SoulPollutionHandler.handleInput(event.getPlayer(), event.getPlayer().getRandom(), event.getMovementInput());
            }

            MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(event.getPlayer());
            if (operatingMob != null) {
                MindOperatorSynchronizer.handleMovementC(operatingMob, (ClientPlayerEntity) event.getPlayer(), event.getMovementInput());
                MindOperatorSynchronizer.resetPlayerMovement(event.getMovementInput());
            }
        }

        @SubscribeEvent
        public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(player);
            if (operatingMob != null) {
                MindOperatorSynchronizer.setupCamera(operatingMob, player, event);
            } else {
                MindOperatorSynchronizer.resetCamera(event);
            }
        }

        @SubscribeEvent
        public static void onFOVModify(FOVUpdateEvent event) {
            ClientPlayerEntity player = Minecraft.getInstance().player;
            MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(player);
            if (operatingMob != null) {
                MindOperatorSynchronizer.handleFOVModifier(event);
            }
        }

        @SubscribeEvent
        public static <T extends LivingEntity> void onLivingRendered(RenderLivingEvent.Post<T, ? extends EntityModel<? extends T>> event) {
            if (event.getEntity() instanceof MobEntity/* && ((IMobEntityMixin) event.getEntity()).isControlled()*/) {
                IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                IVertexBuilder builder = buffer.getBuffer(ModRenderTypes.laser(6));
                // greenLine(builder, event.getMatrixStack().last().pose(), 0, 0.5f, 0, 0, 6, 0);
            }
        }

        private static void greenLine(IVertexBuilder builder, Matrix4f positionMatrix, float dx1, float dy1, float dz1, float dx2, float dy2, float dz2) {
            builder.vertex(positionMatrix, dx1, dy1, dz1)
                    .color(0.0f, 1.0f, 0.0f, 1.0f)
                    .endVertex();
            builder.vertex(positionMatrix, dx2, dy2, dz2)
                    .color(0.0f, 1.0f, 0.0f, 1.0f)
                    .endVertex();
        }

        @SuppressWarnings("deprecation")
        @SubscribeEvent
        public static void onRenderHotbar(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                if (!((IClientPlayerMixin) player).getItemVanishingSlots().isEmpty()) {
                    RenderSystem.enableRescaleNormal();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.color4f(1, 1, 1, 1);
                    int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                    int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
                    Minecraft.getInstance().getTextureManager().bind(ITEM_VANISHING_LOCATION);
                    IngameGui gui = Minecraft.getInstance().gui;
                    for (int slotIndex : ((IClientPlayerMixin) player).getItemVanishingSlots()) {
                        AbstractGui.blit(event.getMatrixStack(), screenWidth / 2 - 91 - 1 + slotIndex * 20, screenHeight - 22 - 1, gui.getBlitOffset(), 0, player.inventory.selected == slotIndex ? 24 : 0, 24, 22, 48, 24);
                    }
                    RenderSystem.disableRescaleNormal();
                    RenderSystem.disableBlend();
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ModEventListener {
        private ModEventListener() {}

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(ModEventListener::run);
            ModDimensionRenderers.registerDimensionRenderers();
            ModEntityRenderers.registerEntityRenderers();
            ModShaders.registerShaders();
        }

        @SubscribeEvent
        public static void registerItemColors(ColorHandlerEvent.Item event) {
            event.getItemColors().register((stack, tintIndex) -> tintIndex > 0 ? -1 : PotionUtils.getColor(stack),
                    ModItems.HALF_USED_POTION,
                    ModItems.HALF_USED_SPLASH_POTION,
                    ModItems.HALF_USED_LINGERING_POTION);
        }

        private static void run() {
            registerItemModelProperties();
            registerRenderLayers();
            bindScreens();
        }

        private static void registerItemModelProperties() {
            ItemModelsProperties.register(ModItems.SOUL_BOW, SoulBowItem.PULL, (stack, world, entity) -> {
                if (entity == null) {
                    return 0;
                }
                return entity.getUseItem() != stack ? 0 : (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20f;
            });
            ItemModelsProperties.register(ModItems.SOUL_BOW, SoulBowItem.PULLING, (stack, world, entity) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0);
            ItemModelsProperties.register(ModItems.SOUL_ENERGY_GEM, SoulEnergies.SOUL_ENERGY_LEVEL, (stack, world, entity) -> SoulEnergies.getSELevel(stack));
            ItemModelsProperties.register(ModItems.SOUL_ENERGY_GEM_II, SoulEnergies.SOUL_ENERGY_LEVEL, (stack, world, entity) -> SoulEnergies.getSELevel(stack));
            IItemPropertyGetter enderLauncher = (stack, world, entity) -> {
                if (entity instanceof PlayerEntity && Objects.equals(EnderLauncherItem.getUUID(((IPlayerEntityMixin) entity).getEnderLauncher()), EnderLauncherItem.getUUID(stack))) {
                    return 1 - ((IPlayerEntityMixin) entity).getEnderLauncherRemainingTicks() / (float) EnderLauncherItem.ROTATE_TICKS;
                }
                return 0;
            };
            ItemModelsProperties.register(ModItems.ENDER_LAUNCHER, EnderLauncherItem.ROTATION, enderLauncher);
            ItemModelsProperties.register(ModItems.ENDER_LAUNCHER_II, EnderLauncherItem.ROTATION, enderLauncher);
        }

        private static void registerRenderLayers() {
            RenderTypeLookup.setRenderLayer(ModBlocks.BROKEN_REFINED_SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.CHIPPED_REFINED_SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.CHIPPED_SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.DAMAGED_REFINED_SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.DAMAGED_SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.ENCHANTER_SPAWNER, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.INFERNO, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.INSTANT_SPAWNER, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.POISONOUS_FIRE, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.POTTED_SOULIFIED_BUSH, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.PURE_SOUL_FIRE, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.REFINED_SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.SOUL_METAL_BARS, RenderType.cutoutMipped());
            RenderTypeLookup.setRenderLayer(ModBlocks.SOUL_WART, RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.SOULIFIED_BUSH, RenderType.cutout());
        }

        private static void bindScreens() {
            ScreenManager.register(ModContainers.DEPTH_SEGEN, SEGeneratorScreen.Depth::new);
            ScreenManager.register(ModContainers.EXA, ExtraAbilityScreen::new);
            ScreenManager.register(ModContainers.HEAT_SEGEN, SEGeneratorScreen.Heat::new);
            ScreenManager.register(ModContainers.NETHER_SEGEN, SEGeneratorScreen.Nether::new);
            ScreenManager.register(ModContainers.SEGEN, SEGeneratorScreen.Common::new);
            ScreenManager.register(ModContainers.SKY_SEGEN, SEGeneratorScreen.Sky::new);
            ScreenManager.register(ModContainers.SOLAR_SEGEN, SEGeneratorScreen.Solar::new);
            ScreenManager.register(ModContainers.SOUL_ENERGY_STORAGE, SEStorageScreen::new);
            ScreenManager.register(ModContainers.SOUL_REINFORCEMENT_TABLE, SoulReinforcementTableScreen::new);
        }
    }
}
