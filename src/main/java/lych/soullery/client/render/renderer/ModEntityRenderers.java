package lych.soullery.client.render.renderer;

import lych.soullery.Soullery;
import lych.soullery.entity.monster.IPurifiable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static lych.soullery.entity.ModEntities.*;
import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@OnlyIn(Dist.CLIENT)
public final class ModEntityRenderers {
    private static final ResourceLocation DRAGON_FIREBALL_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_fireball.png");
    private static final ResourceLocation SOULBALL_LOCATION = Soullery.prefixTex("entity/souldragon/soulball.png");
    private static final ResourceLocation SOULBALL_PURE_LOCATION = Soullery.prefixTex("entity/souldragon/soulball_pure.png");

    private ModEntityRenderers() {}

    public static void registerEntityRenderers() {
        registerEntityRenderingHandler(CLONED_SKELETON_KING, SkeletonKingRenderer::new);
        registerEntityRenderingHandler(COMPUTER_SCIENTIST, ComputerScientistRenderer::new);
        registerEntityRenderingHandler(DARK_EVOKER, DarkEvokerRenderer::new);
        registerEntityRenderingHandler(DROPPING_MORTAR_SHELL, ModEntityRenderers::createRenderForMortarShells);
        registerEntityRenderingHandler(ENERGIZED_BLAZE, EnergizedBlazeRenderer::new);
        registerEntityRenderingHandler(ENGINEER, EngineerRenderer::new);
        registerEntityRenderingHandler(ETHE_ARMORER, EtheArmorerRenderer::new);
        registerEntityRenderingHandler(ETHEREAL_ARROW, EtherealArrowRenderer::new);
        registerEntityRenderingHandler(FANGS, FangsRenderer::new);
        registerEntityRenderingHandler(FANGS_SUMMONER, TippedArrowRenderer::new);
        registerEntityRenderingHandler(FORTIFIED_SOUL_CRYSTAL, FortifiedSoulCrystalRenderer::new);
        registerEntityRenderingHandler(GIANT_X, GiantXRenderer::new);
        registerEntityRenderingHandler(GRAVITATIONAL_DRAGON_FIREBALL, SimpleTexturedRenderer.single(DRAGON_FIREBALL_LOCATION));
        registerEntityRenderingHandler(HORCRUX, HorcruxRenderer::new);
        registerEntityRenderingHandler(ILLUSORY_HORSE, IllusoryHorseRenderer::new);
        registerEntityRenderingHandler(META8, Meta08Renderer::new);
        registerEntityRenderingHandler(PURSUER, PursuerRenderer::new);
        registerEntityRenderingHandler(REDSTONE_BOMB, NoTextureRenderer::new);
        registerEntityRenderingHandler(REDSTONE_MORTAR, RedstoneTurretRenderer.Mortar::new);
        registerEntityRenderingHandler(REDSTONE_TURRET, RedstoneTurretRenderer.Common::new);
        registerEntityRenderingHandler(RISING_MORTAR_SHELL, ModEntityRenderers::createRenderForMortarShells);
        registerEntityRenderingHandler(ROBOT, RobotRenderer::new);
        registerEntityRenderingHandler(SKELETON_FOLLOWER, SkeletonRenderer::new);
        registerEntityRenderingHandler(SKELETON_KING, SkeletonKingRenderer::new);
        registerEntityRenderingHandler(SOULBALL, SimpleTexturedRenderer.fixedRenderType(IPurifiable.select(SOULBALL_PURE_LOCATION, SOULBALL_LOCATION), RenderType::entityCutoutNoCull));
        registerEntityRenderingHandler(SOUL_ARROW, SoulArrowRenderer::new);
        registerEntityRenderingHandler(SOUL_BOLT, SoulBoltRenderer::new);
        registerEntityRenderingHandler(SOUL_CONTROLLER, SoulControllerRenderer::new);
        registerEntityRenderingHandler(SOUL_CRYSTAL, SoulCrystalRenderer::new);
        registerEntityRenderingHandler(SOUL_DRAGON, SoulDragonRenderer::new);
        registerEntityRenderingHandler(SOUL_RABBIT, SoulRabbitRenderer::new);
        registerEntityRenderingHandler(SOUL_SKELETON, SoulSkeletonRenderer::new);
        registerEntityRenderingHandler(SOUL_SKELETON_KING, SoulSkeletonKingRenderer::new);
        registerEntityRenderingHandler(SUB_ZOMBIE, ZombieRenderer::new);
        registerEntityRenderingHandler(VOID_ALCHEMIST, VoidAlchemistRenderer::new);
        registerEntityRenderingHandler(VOID_ARCHER, VoidArcherRenderer::new);
        registerEntityRenderingHandler(VOID_DEFENDER, VoidDefenderRenderer::new);
        registerEntityRenderingHandler(VOIDWALKER, VoidwalkerRenderer::new);
        registerEntityRenderingHandler(WANDERER, WandererRenderer::new);
    }

    private static <T extends Entity & IRendersAsItem> EntityRenderer<T> createRenderForMortarShells(EntityRendererManager manager) {
        return new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer(), 1.2f, true);
    }
}
