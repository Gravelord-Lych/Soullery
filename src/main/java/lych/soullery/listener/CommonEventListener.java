package lych.soullery.listener;

import lych.soullery.Soullery;
import lych.soullery.api.capability.IControlledMobData;
import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.api.exa.PlayerBuff;
import lych.soullery.api.shield.ISharedShield;
import lych.soullery.api.shield.ISharedShieldUser;
import lych.soullery.api.shield.IShieldUser;
import lych.soullery.block.IArmoredBlock;
import lych.soullery.block.ModBlockStateProperties;
import lych.soullery.block.ModBlocks;
import lych.soullery.block.SoulMetalBarsBlock;
import lych.soullery.block.plant.SoulifiedBushBlock;
import lych.soullery.capability.ControlledMobDataProvider;
import lych.soullery.config.ConfigHelper;
import lych.soullery.effect.ModEffects;
import lych.soullery.effect.SoulPollutionHandler;
import lych.soullery.entity.ModEntities;
import lych.soullery.entity.iface.*;
import lych.soullery.entity.monster.boss.SkeletonKingEntity;
import lych.soullery.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soullery.entity.projectile.SoulArrowEntity;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.MindOperatorSynchronizer;
import lych.soullery.extension.control.SoulManager;
import lych.soullery.extension.highlight.EntityHighlightManager;
import lych.soullery.extension.skull.ModSkulls;
import lych.soullery.extension.soulpower.buff.PlayerBuffMap;
import lych.soullery.extension.soulpower.reinforce.Reinforcements;
import lych.soullery.extension.soulpower.reinforce.WandererReinforcement;
import lych.soullery.extension.superlink.SuperLinkManager;
import lych.soullery.item.ModItems;
import lych.soullery.item.SoulContainerItem;
import lych.soullery.item.SoulPieceItem;
import lych.soullery.item.VoidwalkerSpawnEggItem;
import lych.soullery.mixin.EntityDamageSourceAccessor;
import lych.soullery.mixin.IndirectEntityDamageSourceAccessor;
import lych.soullery.mixin.MobSpawnInfoAccessor;
import lych.soullery.network.ClickHandlerNetwork;
import lych.soullery.tag.ModBlockTags;
import lych.soullery.util.*;
import lych.soullery.util.mixin.IEntityMixin;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import lych.soullery.world.CommandData;
import lych.soullery.world.event.manager.EventManager;
import lych.soullery.world.event.manager.SoulDragonFightManager;
import lych.soullery.world.event.manager.WorldTickerManager;
import lych.soullery.world.gen.biome.SLBiomes;
import lych.soullery.world.gen.dimension.ModDimensions;
import lych.soullery.world.gen.feature.ModConfiguredFeatures;
import lych.soullery.world.gen.feature.PlateauSpikeFeature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherrackBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

import static lych.soullery.util.ExtraAbilityConstants.FALL_BUFFER_AMOUNT;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID)
public final class CommonEventListener {
    private CommonEventListener() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide()) {
            EventManager.runEvents();
        }
    }

    @SubscribeEvent
    public static void onGameModeChange(PlayerEvent.PlayerChangeGameModeEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(player);
        if (operatingMob != null) {
            MindOperator<?> operator = SoulManager.remove(operatingMob, MindOperator.class);
            operator.sendGameModeChangeMessage(player);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof SkeletonKingEntity && event.getEntity() instanceof AbstractSkeletonEntity) {
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (((IPlayerEntityMixin) player).hasExtraAbility(ExtraAbility.DRAGON_WIZARD) && event.getSource().isMagic()) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingStartHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof IHasOwner<?>) {
            if (event.getSource() instanceof IndirectEntityDamageSource) {
                IHasOwner<?> entityWithOwner = (IHasOwner<?>) event.getSource().getEntity();
                if (entityWithOwner.getOwner() != null && entityWithOwner.shouldSetIndirectDamageToOwner()) {
                    ((IndirectEntityDamageSourceAccessor) event.getSource()).setOwner(entityWithOwner.getOwner());
                }
            }
            if (event.getSource() instanceof EntityDamageSource) {
                IHasOwner<?> entityWithOwner = (IHasOwner<?>) event.getSource().getEntity();
                if (entityWithOwner.getOwner() != null && entityWithOwner.shouldSetDirectDamageToOwner()) {
                    ((EntityDamageSourceAccessor) event.getSource()).setEntity(entityWithOwner.getOwner());
                }
            }
        }
        if (event.getSource() instanceof IndirectEntityDamageSource && event.getSource().getEntity() != event.getSource().getDirectEntity() && event.getSource().getDirectEntity() instanceof IHasOwner<?>) {
            IHasOwner<?> entityWithOwner = (IHasOwner<?>) event.getSource().getDirectEntity();
            if (entityWithOwner.getOwner() != null && entityWithOwner.shouldSetDirectDamageToOwner()) {
                ((EntityDamageSourceAccessor) event.getSource()).setEntity(entityWithOwner.getOwner());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource() == DamageSource.ON_FIRE) {
            event.setAmount(event.getAmount() * Math.max(1, ((IEntityMixin) event.getEntity()).getFireOnSelf().getFireDamage(event.getEntity(), event.getEntity().level)));
        }
        if (event.getSource().getDirectEntity() instanceof SoulArrowEntity) {
            ((IEntityMixin) event.getEntity()).setFireOnSelf(((IEntityMixin) event.getSource().getDirectEntity()).getFireOnSelf());
        }
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.THORNS_MASTER.isOn((PlayerEntity) event.getEntity())) {
            if (EntityUtils.isMelee(event.getSource()) && !EntityUtils.isThorns(event.getSource()) && event.getSource().getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
                attacker.hurt(DamageSource.thorns(event.getEntity()), ExtraAbilityConstants.THORNS_MASTER_DAMAGE);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingFinallyHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof IDamageMultipliable && !((IDamageMultipliable) event.getSource().getEntity()).multiplyFinalDamage()) {
            event.setAmount(event.getAmount() * ((IDamageMultipliable) event.getSource().getEntity()).getDamageMultiplier());
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!event.getSource().isBypassInvul() && event.getEntity() instanceof SkeletonKingEntity && ((SkeletonKingEntity) event.getEntity()).reachedTier(11)) {
            event.setAmount(Math.min(event.getAmount(), Math.max(SkeletonKingEntity.DAMAGE_THRESHOLD_T11 - ((SkeletonKingEntity) event.getEntity()).getTier() * 2 + 22, SkeletonKingEntity.MIN_MAX_DAMAGE)));
        }
        if (event.getSource().getEntity() instanceof IDamageMultipliable && ((IDamageMultipliable) event.getSource().getEntity()).multiplyFinalDamage()) {
            event.setAmount(event.getAmount() * ((IDamageMultipliable) event.getSource()).getDamageMultiplier());
        }
        if (!event.getSource().isBypassInvul() && event.getEntity() instanceof IHasResistance) {
            event.setAmount(event.getAmount() * (1 - ((IHasResistance) event.getEntity()).getResistance()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingFinallyDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof ITieredMob) {
            ITieredMob entity = (ITieredMob) event.getEntity();
            entity.handleHurt(event.getSource());
        }
        if (event.getEntity() instanceof IShieldUser) {
            @Nullable
            ISharedShield shield = ((IShieldUser) event.getEntity()).getSharedShield();
            if (shield != null && ((IShieldUser) event.getEntity()).isShieldValid()) {
                if (event.getAmount() > 0 && event.getEntity().level instanceof ServerWorld && ((IShieldUser) event.getEntity()).showHitParticles(event.getSource(), event.getAmount())) {
                    EntityUtils.sharedShieldHitParticleServerside(event.getEntity(), (ServerWorld) event.getEntity().level);
                }
                float amount = shield.hurt(event.getSource(), event.getAmount());
                if (amount > 0) {
                    event.setAmount(amount);
                } else {
                    event.setCanceled(true);
                }
                if (shield.getHealth() <= 0) {
                    if (event.getEntity() instanceof ISharedShieldUser) {
                        ISharedShieldUser user = (ISharedShieldUser) event.getEntity();
                        if (user.getShieldProvider() instanceof Entity) {
                            EntityUtils.disableShield(event.getEntityLiving().level, user.getShieldProvider(), event.getEntityLiving().getRandom());
                        }
                    } else {
                        IShieldUser user = (IShieldUser) event.getEntity();
                        EntityUtils.disableShield(event.getEntityLiving().level, user, event.getEntityLiving().getRandom());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPostHurt(PostLivingHurtEvent event) {
        if (event.isSuccessfullyHurt()) {
            Entity attacker = event.getSource().getEntity();
            Entity target = event.getEntity();
            if (attacker instanceof PlayerEntity) {
                SoulPollutionHandler.mayPollute((PlayerEntity) attacker, attacker.level, SoulPollutionHandler.POLLUTE_PROBABILITY_SPECIAL);
            }
            if (target instanceof PlayerEntity) {
                SoulPollutionHandler.mayPollute((PlayerEntity) target, target.level, SoulPollutionHandler.POLLUTE_PROBABILITY_SPECIAL);
            }
        }
    }

    @SubscribeEvent
    public static void onEnderPearlUse(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() == Items.ENDER_PEARL) {
            for (Item item : ModItems.ENDER_LAUNCHER.getTierMap().values()) {
                event.getPlayer().getCooldowns().addCooldown(item, 20);
            }
        }
    }

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getEntity() instanceof AbstractVoidwalkerEntity && event.getSpawnReason() == SpawnReason.SPAWN_EGG) {
            ((AbstractVoidwalkerEntity) event.getEntity()).setTier(VoidwalkerSpawnEggItem.getCurrentTier());
        }
    }

    @SubscribeEvent
    public static void onBonemealApply(BonemealEvent event) {
        if (event.getBlock().is(Blocks.SOUL_SOIL) && canSoulSoilApplyBonemeal(event.getWorld(), event.getPos(), true)) {
            performHyphalify(event.getWorld(), event.getWorld().getRandom(), event.getPos(), ModBlocks.CRIMSON_HYPHAL_SOIL.defaultBlockState(), ModBlocks.WARPED_HYPHAL_SOIL.defaultBlockState());
            event.setResult(Event.Result.ALLOW);
        }
        if (event.getBlock().getBlock() instanceof NetherrackBlock) {
            if (canSoulSoilApplyBonemeal(event.getWorld(), event.getPos(), false)) {
                performHyphalify(event.getWorld(), event.getWorld().getRandom(), event.getPos(), Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.WARPED_NYLIUM.defaultBlockState());
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    private static boolean canSoulSoilApplyBonemeal(IBlockReader reader, BlockPos pos, boolean checkNyliums) {
        if (reader.getBlockState(pos.above()).propagatesSkylightDown(reader, pos)) {
            for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if ((checkNyliums && reader.getBlockState(nearbyPos).is(BlockTags.NYLIUM)) || reader.getBlockState(nearbyPos).is(ModBlockTags.HYPHAL_SOUL_SOIL)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void performHyphalify(World world, Random random, BlockPos pos, BlockState forCrimson, BlockState forWarped) {
        boolean crimson = false;
        boolean warped = false;
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState state = world.getBlockState(nearbyPos);
            if (state.is(Blocks.CRIMSON_NYLIUM) || state.is(ModBlocks.CRIMSON_HYPHAL_SOIL)) {
                crimson = true;
            }
            if (state.is(Blocks.WARPED_NYLIUM) || state.is(ModBlocks.WARPED_HYPHAL_SOIL)) {
                warped = true;
            }
        }
        if (crimson && warped) {
            world.setBlock(pos, random.nextBoolean() ? forCrimson : forWarped, Constants.BlockFlags.DEFAULT);
        } else if (crimson) {
            world.setBlock(pos, forCrimson, Constants.BlockFlags.DEFAULT);
        } else if (warped) {
            world.setBlock(pos, forWarped, Constants.BlockFlags.DEFAULT);
        } else if (ConfigHelper.shouldFailhard()) {
            throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + "Neither nylium nor hyphal soul soil found");
        }
    }

    @SubscribeEvent
    public static void onBurnTimeGet(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModItems.SOULIFIED_BUSH) {
            event.setBurnTime(SoulifiedBushBlock.BURN_TIME);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (MindOperatorSynchronizer.getOperatingMob(event.getPlayer()) != null) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerDig(PlayerEvent.BreakSpeed event) {
        if (ExtraAbility.DESTROYER.isOn(event.getPlayer())) {
            event.setNewSpeed(event.getNewSpeed() * ExtraAbilityConstants.DESTROYER_SPEED_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getEntity() instanceof DamagingProjectileEntity || event.getEntity() instanceof AbstractArrowEntity) {
            World level = event.getEntity().level;
            if (level.isClientSide()) {
                return;
            }
            BlockPos pos = new BlockPos(event.getRayTraceResult().getLocation());
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof SoulMetalBarsBlock && state.getValue(ModBlockStateProperties.DAMAGE_LINKABLE)) {
                SoulMetalBarsBlock block = (SoulMetalBarsBlock) state.getBlock();
                SoulMetalBarsBlock.handleBlockDestroy(level, pos, block, block.getMaxLinkedDamageProjectile(), false);
                SoulMetalBarsBlock.destroyHitProjectile(event.getEntity());
                SoulMetalBarsBlock.addParticles((ServerWorld) level, event.getRayTraceResult().getLocation(), level.getRandom());
            }
        }
    }

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntityLiving().level.dimension() == ModDimensions.SOUL_LAND && event.getSpawnReason() == SpawnReason.NATURAL) {
            if (event.getEntityLiving().getY() > ModConstants.SOUL_LAND_MAX_SPAWNABLE_HEIGHT) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDestroySoulMetalBars(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        if (state.getBlock() instanceof SoulMetalBarsBlock && state.getValue(ModBlockStateProperties.DAMAGE_LINKABLE)) {
            SoulMetalBarsBlock block = (SoulMetalBarsBlock) state.getBlock();
            boolean destroyed = SoulMetalBarsBlock.handleBlockDestroy(event.getWorld(),
                    event.getPos(),
                    block,
                    block.getMaxLinkedDamageDig(),
                    !event.getPlayer().abilities.instabuild && ForgeHooks.canHarvestBlock(state, event.getPlayer(), event.getWorld(), event.getPos()));
            if (event.getWorld() instanceof ServerWorld) {
                SoulMetalBarsBlock.addParticles((ServerWorld) event.getWorld(), Vector3d.atCenterOf(event.getPos()), event.getWorld().getRandom());
            }
            event.setCanceled(!destroyed);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDestroyBlock(LivingDestroyBlockEvent event) {
        if (handleArmoredBlockBreak(event.getEntity().level, event.getPos(), IArmoredBlock::enableForMobs)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDestroyBlock(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        if (handleArmoredBlockBreak(world, event.getPos())) {
            event.setCanceled(true);
        }
        SoulPollutionHandler.mayPollute(event.getPlayer(), world, SoulPollutionHandler.POLLUTE_PROBABILITY_SPECIAL);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> handleArmoredBlockBreak(event.getWorld(), pos, IArmoredBlock::enableForExplosion));
    }

    @SubscribeEvent
    public static void onMobDespawn(LivingSpawnEvent.AllowDespawn event) {
        if (!event.getWorld().isClientSide() && event.getEntityLiving() instanceof MobEntity) {
            MobEntity mob = (MobEntity) event.getEntityLiving();
            if (SoulManager.hasControllers(mob)) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof MobEntity && !entity.level.isClientSide()) {
            event.addCapability(Soullery.prefix("controlled_mob"), new ControlledMobDataProvider<>((MobEntity) entity, (ServerWorld) entity.level));
        }
    }

    private static boolean handleArmoredBlockBreak(World world, BlockPos pos) {
        return handleArmoredBlockBreak(world, pos, b -> true);
    }

    private static boolean handleArmoredBlockBreak(World world, BlockPos pos, Predicate<? super IArmoredBlock> shouldEnable) {
        BlockState old = world.getBlockState(pos);
        Block oldBlock = old.getBlock();
        TileEntity oldBlockEntity = world.getBlockEntity(pos);
        if (oldBlock instanceof IArmoredBlock) {
            IArmoredBlock armoredBlock = (IArmoredBlock) oldBlock;
            if (!shouldEnable.test(armoredBlock)) {
                return false;
            }
            BlockState child = armoredBlock.getChild(world, pos, old, oldBlockEntity);
            if (child != null) {
                world.setBlock(pos, child, Constants.BlockFlags.DEFAULT);
                armoredBlock.restoreFrom(world, pos, old, child, oldBlockEntity);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onEmptyLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player.isSpectator()) {
            return;
        }
        if (MindOperatorSynchronizer.getOperatingMob(player) != null) {
            ClickHandlerNetwork.INSTANCE.sendToServer(ClickHandlerNetwork.Type.LEFT);
        }
    }

    @SubscribeEvent
    public static void onEmptyRightClick(PlayerInteractEvent.RightClickEmpty event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player.isSpectator()) {
            return;
        }
        if (player.getMainHandItem().isEmpty() && ExtraAbility.TELEPORTATION.isOn(player) || MindOperatorSynchronizer.getOperatingMob(player) != null) {
            ClickHandlerNetwork.INSTANCE.sendToServer(ClickHandlerNetwork.Type.RIGHT);
        }
    }


    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isClientSide()) {
            return;
        }
        ServerWorld level = (ServerWorld) event.getWorld();
        if (event.getWorld().getBlockState(event.getPos()).is(Blocks.DRAGON_EGG)) {
            if (event.getWorld().dimension() == ModDimensions.SOUL_LAND && event.getWorld().getBiomeName(event.getPos()).filter(r -> r == SLBiomes.INNERMOST_PLATEAU).isPresent() && checkBase(event, level) && SoulDragonFightManager.get(level).getNearbyEvent(event.getPos(), 100) == null) {
                List<BlockPos> posList = checkSoulDragonFrom(event.getPos().below(), level);
                if (!posList.isEmpty()) {
                    SoulDragonFightManager.tryAddFight(event.getPos(), level, posList);
                } else {
                    SoulDragonFightManager.warnFailedPlayer(event.getPlayer());
                }
                event.setUseBlock(Event.Result.DENY);
            } else if (event.getPlayer().getMainHandItem().getItem() instanceof SoulContainerItem && SoulPieceItem.getType(event.getPlayer().getMainHandItem()) == EntityType.ENDER_DRAGON) {
                event.getPlayer().getMainHandItem().shrink(1);
                event.getWorld().destroyBlock(event.getPos(), false);
                for (int i = 0; i < 2; i++) {
                    EntityUtils.spawnItem(event.getWorld(), event.getPos(), new ItemStack(Items.DRAGON_EGG));
                }
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.sidedSuccess(event.getWorld().isClientSide()));
            }
        }
    }

    private static boolean checkBase(PlayerInteractEvent.RightClickBlock event, ServerWorld level) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                BlockState state = level.getBlockState(event.getPos().offset(x, -1, z));
                if (!state.is(ModBlocks.SOUL_METAL_BLOCK) && !state.is(ModBlocks.REFINED_SOUL_METAL_BLOCK)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static List<BlockPos> checkSoulDragonFrom(BlockPos pos, ServerWorld level) {
        List<BlockPos> posList = PlateauSpikeFeature.findSpikeLocations(level, pos, level.getRandom());
        return posList.stream().allMatch(posIn -> level.getBiomeName(posIn).filter(r -> r == SLBiomes.INNERMOST_PLATEAU).isPresent()) ? posList : Collections.emptyList();
    }

    @SuppressWarnings("deprecation")
    public static void handleEmptyClickServerside(ServerPlayerEntity player, ClickHandlerNetwork.Type type) {
        if (player.isSpectator()) {
            return;
        }
        if (type == ClickHandlerNetwork.Type.LEFT) {
            MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(player);
            if (operatingMob != null) {
                MindOperatorSynchronizer.handleMelee(player, operatingMob);
            }
        }
        if (type == ClickHandlerNetwork.Type.RIGHT) {
            MobEntity operatingMob = MindOperatorSynchronizer.getOperatingMob(player);
            if (operatingMob != null) {
                MindOperatorSynchronizer.handleRightClick(player, operatingMob);
            } else if (player.getMainHandItem().isEmpty() && ExtraAbility.TELEPORTATION.isOn(player)) {
                int cooldown = ((IPlayerEntityMixin) player).getAdditionalCooldowns().getCooldownRemaining(ExtraAbility.TELEPORTATION.getRegistryName());
                if (cooldown == 0) {
                    BlockRayTraceResult ray = (BlockRayTraceResult) player.pick(player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + ExtraAbilityConstants.BASE_TELEPORTATION_RADIUS, 0, false);
                    BlockPos pos = ray.getBlockPos();
                    World world = player.getLevel();
                    if (world.getBlockState(pos).getMaterial().blocksMotion() && world.getBlockState(pos.above()).isAir() && world.getBlockState(pos.above().above()).isAir()) {
                        player.teleportTo(ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
                        ((IPlayerEntityMixin) player).getAdditionalCooldowns().addCooldown(ExtraAbility.TELEPORTATION.getRegistryName(), ExtraAbilityConstants.TELEPORTATION_COOLDOWN);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectFirstlyApply(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.TRANSFORMATION.isOn((PlayerEntity) event.getEntity())) {
            if (event.getPotionEffect().getEffect() == Effects.WITHER) {
                event.getEntityLiving().addEffect(ModEffectUtils.copyAttributes(Effects.REGENERATION, event.getPotionEffect()));
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectApply(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.PURIFICATION.isOn((PlayerEntity) event.getEntity())) {
            boolean harmful = ModEffectUtils.isHarmful(event.getPotionEffect());
            if (harmful && ((PlayerEntity) event.getEntity()).getRandom().nextDouble() < ExtraAbilityConstants.PURIFICATION_PROBABILITY) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<PlayerBuff> buffs = new ArrayList<>();
            for (PlayerBuff playerBuff : PlayerBuffMap.values()) {
                if (PlayerBuffMap.getAbility(playerBuff).orElseThrow(NullPointerException::new).isOn(event.player)) {
                    buffs.add(playerBuff);
                }
            }
            for (PlayerBuff buff : buffs) {
                if (event.side == LogicalSide.SERVER) {
                    buff.serverTick((ServerPlayerEntity) event.player, ((ServerPlayerEntity) event.player).getLevel());
                } else {
                    buff.clientTick((ClientPlayerEntity) event.player, ((ClientPlayerEntity) event.player).clientLevel);
                }
            }
            ((IPlayerEntityMixin) event.player).getAdditionalCooldowns().tick();
            if (event.side == LogicalSide.SERVER) {
                SoulPollutionHandler.mayPollute(event.player, event.player.level, SoulPollutionHandler.POLLUTE_PROBABILITY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntityLiving().level.isClientSide) {
            ((IEntityMixin) event.getEntityLiving()).setReversed(event.getEntityLiving().hasEffect(ModEffects.REVERSION));
            if (event.getEntityLiving() instanceof MobEntity) {
                MobEntity mob = (MobEntity) event.getEntityLiving();
                SoulManager.getData(mob).ifPresent(IControlledMobData::tick);
            }
        }
        if (event.getEntityLiving() instanceof IShieldUser && ((IShieldUser) event.getEntityLiving()).getSharedShield() != null) {
//          Multi-tick is not allowed for shields
            if (!(event.getEntityLiving() instanceof ISharedShieldUser) || (((ISharedShieldUser) event.getEntityLiving()).getShieldProvider() == event.getEntityLiving())) {
                ((IShieldUser) event.getEntityLiving()).getSharedShield().tick();
            }
        }
        if (event.getEntityLiving() instanceof ISpellCastable) {
            ((ISpellCastable) event.getEntityLiving()).renderParticles();
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ITieredMob) {
            ITieredMob entity = (ITieredMob) event.getEntity();
            entity.handleDeath(event.getSource());
        }
        if (!event.getEntity().level.isClientSide()) {
            ServerWorld world = (ServerWorld) event.getEntity().level;
            world.getEntities().filter(entity -> entity instanceof INecromancer<?, ?>).forEach(entity -> {
                INecromancer<?, ?> necromancer = (INecromancer<?, ?>) entity;
                if (entity.distanceToSqr(event.getEntity()) <= necromancer.getReviveDistanceSqr()) {
                    necromancer.addSoulIfPossible(event.getEntity(), world);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        double dropProbability = Reinforcements.getDropProbability(event.getEntityLiving().getType());
        boolean playerKilled = event.isRecentlyHit() && event.getSource().getEntity() instanceof PlayerEntity;
        boolean canLoot = event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        boolean nonDeadPlayer = !(event.getEntityLiving() instanceof PlayerEntity);
        if (playerKilled && canLoot && nonDeadPlayer) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            int level = Reinforcements.WANDERER.getLevel(player.getMainHandItem());
            dropProbability *= (1 + level * WandererReinforcement.PROBABILITY_MULTIPLIER);
            while (event.getEntityLiving().getRandom().nextDouble() < dropProbability) {
                ItemStack piece = new ItemStack(ModItems.SOUL_PIECE);
                SoulPieceItem.setType(piece, event.getEntityLiving().getType());
                event.getEntityLiving().spawnAtLocation(piece);
                dropProbability -= 1;
                if (dropProbability <= 0) {
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCalculatingLootLevel(LootingLevelEvent event) {
        if (event.getDamageSource().getEntity() instanceof PlayerEntity && ExtraAbility.PILLAGER.isOn((PlayerEntity) event.getDamageSource().getEntity())) {
            event.setLootingLevel(event.getLootingLevel() + ExtraAbilityConstants.PILLAGER_LOOTING_LEVEL_BONUS);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoadWithHighPriority(BiomeLoadingEvent event) {
        if (isVanillaRegistryName(event.getName()) && "soul_sand_valley".equals(event.getName().getPath())) {
            event.getSpawns().addMobCharge(ModEntities.SOUL_SKELETON, 0.7, 0.15);
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 20, 5, 5));
        }
        if (isVanillaRegistryName(event.getName()) && "warped_forest".equals(event.getName().getPath())) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_POISONOUS_FIRE);
        }
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (isVanillaRegistryName(event.getName()) && "soul_sand_valley".equals(event.getName().getPath())) {
            ((MobSpawnInfoAccessor) event.getSpawns()).getSpawners().get(EntityClassification.MONSTER).removeIf(spawners -> spawners.type == EntityType.SKELETON);
            ((MobSpawnInfoAccessor) event.getSpawns()).getMobSpawnCosts().remove(EntityType.SKELETON);
        }
    }

    private static boolean isVanillaRegistryName(@Nullable ResourceLocation location) {
        return location != null && "minecraft".equals(location.getNamespace());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity oldPlayer = event.getOriginal();
        PlayerEntity newPlayer = event.getPlayer();
        IPlayerEntityMixin oldPlayerM = (IPlayerEntityMixin) oldPlayer;
        IPlayerEntityMixin newPlayerM = (IPlayerEntityMixin) newPlayer;
        syncData(oldPlayerM, newPlayerM);
    }

    private static void syncData(IPlayerEntityMixin oldPlayerM, IPlayerEntityMixin newPlayerM) {
        Set<IExtraAbility> extraAbilities = oldPlayerM.getExtraAbilities();
        newPlayerM.setExtraAbilities(extraAbilities);
        Map<EntityType<?>, Integer> bossTierMap = oldPlayerM.getBossTierMap();
        CollectionUtils.refill(newPlayerM.getBossTierMap(), bossTierMap);
        AdditionalCooldownTracker tracker = oldPlayerM.getAdditionalCooldowns();
        newPlayerM.getAdditionalCooldowns().reloadFrom(tracker.save());
        for (int i = 0; i < oldPlayerM.getExtraAbilityCarrierInventory().getContainerSize(); i++) {
            newPlayerM.getExtraAbilityCarrierInventory().setItem(i, oldPlayerM.getExtraAbilityCarrierInventory().getItem(i));
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        ServerWorld world = (ServerWorld) event.world;
        if (event.phase == TickEvent.Phase.END) {
            tick(world);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (ExtraAbility.FALLING_BUFFER.isOn(player)) {
                event.setDistance(Math.max(event.getDistance() - FALL_BUFFER_AMOUNT, 0));
            }
        }
    }

    @SubscribeEvent
    public static void onCalculatingLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (event.getLookingEntity() != null) {
            ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.HEAD);
            Item item = stack.getItem();
            EntityType<?> entityType = event.getLookingEntity().getType();
            if (ModSkulls.matches(entityType, item)) {
                event.modifyVisibility(0.5);
            }
            if (event.getEntityLiving() instanceof PlayerEntity && ExtraAbility.IMITATOR.isOn((PlayerEntity) event.getEntityLiving())) {
                event.modifyVisibility(ExtraAbilityConstants.IMITATOR_VISIBILITY_MODIFIER);
            }
        }
    }

    private static void tick(ServerWorld world) {
        CommandData.get(world.getServer()).update(world);
        EntityHighlightManager.get(world).tick();
        SuperLinkManager.get(world).tick(world.getServer());
        WorldTickerManager.get(world).tick();
        SoulDragonFightManager.get(world).tick();
    }
}
