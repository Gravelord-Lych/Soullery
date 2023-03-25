package lych.soullery.extension.control;

import lych.soullery.Soullery;
import lych.soullery.extension.control.attack.*;
import lych.soullery.extension.control.movement.MovementHandler;
import lych.soullery.extension.control.rotation.DefaultRotationHandler;
import lych.soullery.extension.control.rotation.RotationHandler;
import lych.soullery.extension.highlight.EntityHighlightManager;
import lych.soullery.extension.highlight.HighlighterType;
import lych.soullery.item.MindOperatorItem;
import lych.soullery.network.MovementData;
import lych.soullery.util.DefaultValues;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.SoulEnergies;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.LazyValue;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import static lych.soullery.util.SoulEnergies.getExtractableSEOf;

public abstract class MindOperator<T extends MobEntity> extends Controller<T> {
    public static final int SE_COST = 100;
    public static final int SE_COST_PER_TICK = 2;
    public static final Marker MARKER = MarkerManager.getMarker("MindOperator");
    private static final UUID REACH_DISTANCE_PENALTY_UUID = UUID.fromString("B926A3CB-9FAB-B2F7-0D02-74F921971CFF");
    private static final AttributeModifier REACH_DISTANCE_PENALTY = new AttributeModifier(REACH_DISTANCE_PENALTY_UUID, "Mind operator reach distance penalty", -1, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final LazyValue<Set<Item>> MIND_OPERATORS = new LazyValue<>(() -> ForgeRegistries.ITEMS.getEntries().stream().map(Map.Entry::getValue).filter(item -> item instanceof MindOperatorItem).collect(Collectors.toSet()));
    private static final ITextComponent DEPLETED = makeText("depleted");
    protected final MeleeHandler<? super T> meleeHandler;
    protected final MovementHandler<? super T> movementHandler;
    protected final RightClickHandler<? super T> rightClickHandler;
    protected final RotationHandler<? super T> rotationHandler;
    protected final TargetFinder<? super T> targetFinder;
    @Nullable
    protected final TargetFinder<? super T> alternativeTargetFinder;
    protected final CompoundNBT data;
    protected final Warnings warnings = new Warnings();
    private JumpController specialJumpControl;
    private float rotationDelta;
    private int tier;

    {
        meleeHandler = initMeleeHandler();
        movementHandler = initMovementHandler();
        rightClickHandler = initRightClickHandler();
        rotationHandler = initRotationHandler();
        targetFinder = initTargetFinder();
        alternativeTargetFinder = initAlternativeTargetFinder();
    }

    public MindOperator(ControllerType<T> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
        data = new CompoundNBT();
    }

    public MindOperator(ControllerType<T> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
        setRotationDelta(compoundNBT.getFloat("RotationDelta"));
        data = compoundNBT.getCompound("SpecialData");
        meleeHandler.loadFrom(data);
        movementHandler.loadFrom(data);
        rightClickHandler.loadFrom(data);
        rotationHandler.loadFrom(data);
        targetFinder.loadFrom(data);
        if (alternativeTargetFinder != null) {
            alternativeTargetFinder.loadFrom(data);
        }
    }

    @Nullable
    public static <T extends MobEntity> MindOperator<T> cast(@Nullable Controller<T> controller) {
        return (MindOperator<T>) controller;
    }

    @Override
    public final void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector) {
        if (getPlayer() != null) {
            goalSelector.addGoal(0, DefaultValues.dummyGoal());
            targetSelector.addGoal(0, DefaultValues.dummyGoal());
            PlayerEntity player = getPlayer();
            IPlayerEntityMixin playerM = (IPlayerEntityMixin) player;
            MobEntity operatingMob = playerM.getOperatingMob();
            if (operatingMob == mob) {
                specialJumpControl = new JumpController(mob);
                return;
            }
            if (operatingMob != null) {
                playerM.setOperatingMob(null);
                getSoulManager().removeIf(operatingMob, c -> c instanceof MindOperator);
            }
            EntityUtils.setTarget(mob, null);
            playerM.setOperatingMob(mob);
            specialJumpControl = new JumpController(mob);
            EntityUtils.addPermanentModifierIfAbsent(player, ForgeMod.REACH_DISTANCE.get(), REACH_DISTANCE_PENALTY);
        }
    }

    @Override
    public final boolean overrideBehaviorGoals() {
        return true;
    }

    @Override
    public final boolean overrideTargetGoals() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void stopControlling(MobEntity mob, boolean mobAlive) {
        super.stopControlling(mob, mobAlive);
        PlayerEntity player = getPlayer();
        if (player != null) {
            try {
                removeFrom(player, (T) mob);
            } catch (RuntimeException e) {
                Soullery.LOGGER.error("Remove failure!", e);
                resetAndAddCooldown(player);
            }
        }
    }

    protected void removeFrom(PlayerEntity player, T mob) {
        resetAndAddCooldown(player);
    }

    protected void resetAndAddCooldown(PlayerEntity player) {
        resetAndAddCooldown(player, MindOperatorItem.getCooldownForTier(tier));
    }

    public static void resetAndAddCooldown(PlayerEntity player, int cooldown) {
        MIND_OPERATORS.get().forEach(item -> player.getCooldowns().addCooldown(item, cooldown));
        reset(player);
    }

    public static void reset(PlayerEntity player) {
        ((IPlayerEntityMixin) player).setOperatingMob(null);
        EntityUtils.getAttribute(player, ForgeMod.REACH_DISTANCE.get()).removeModifier(REACH_DISTANCE_PENALTY);
    }

    @Override
    public final int getPriority() {
        return Integer.MIN_VALUE;
    }

    @SuppressWarnings("unchecked")
    public void handleMeleeRaw(MobEntity operatingMob, ServerPlayerEntity player) {
        handleMelee((T) operatingMob, player);
    }

    public void handleMelee(T operatingMob, ServerPlayerEntity player) {
        LivingEntity target = findMeleeTarget(operatingMob, player);
        if (target != null) {
            meleeHandler.handleMeleeAttack(operatingMob, target, player, data);
        }
    }

    @Nullable
    protected LivingEntity findMeleeTarget(T operatingMob, ServerPlayerEntity player) {
        TargetFinder<? super T> finder = Utils.getOrDefault(targetFinder.getPrimary(), targetFinder);
        return finder.findTarget(operatingMob, player, data);
    }

    public void handleMovement(T operatingMob, ServerPlayerEntity player, MovementData movement) {
        movementHandler.handleMovement(operatingMob, player, movement, specialJumpControl, data);
    }

    @SuppressWarnings("unchecked")
    public void handleRightClickRaw(MobEntity operatingMob, ServerPlayerEntity player) {
        handleRightClick((T) operatingMob, player);
    }

    public void handleRightClick(T operatingMob, ServerPlayerEntity player) {
        LivingEntity target = findRightClickTarget(operatingMob, player);
        if (rightClickHandler.needsExactTarget(operatingMob)) {
            if (target != null) {
                rightClickHandler.handleRightClick(operatingMob, target, player, data);
            }
        } else {
            if (target != null) {
                rightClickHandler.handleRightClick(operatingMob, target, player, data);
            } else {
                rightClickHandler.handleRightClick(operatingMob, player, data);
            }
        }
    }

    @Nullable
    protected LivingEntity findRightClickTarget(T operatingMob, ServerPlayerEntity player) {
        return targetFinder.findTarget(operatingMob, player, data);
    }

    public void handleRotation(T operatingMob, ServerPlayerEntity player) {
        rotationHandler.handleRotation(operatingMob, player, rotationDelta, data);
    }

    public void handleRotationOffset(T operatingMob, ServerPlayerEntity player, double amount) {
    }

    @Override
    public boolean tick() {
        super.tick();
        if (getPlayer() != null) {
            getMob().ifPresent(mob -> {
                ServerPlayerEntity player = (ServerPlayerEntity) getPlayer();
                handleRotation(mob, player);

                boolean useAlt = false;
                LivingEntity target = targetFinder.findTarget(mob, player, data);
                if (target == null && alternativeTargetFinder != null) {
                    target = alternativeTargetFinder.findTarget(mob, player, data);
                    useAlt = true;
                }
                if (target != null) {
                    highlightTarget(useAlt, target);
                }
                setAggressiveWhenTargetIsFound(useAlt, mob, target);

                tickHandlers(mob, player);
                warnings.tick(mob, player);
            });
        }
        if (specialJumpControl != null) {
            specialJumpControl.tick();
        }
        if (specialJumpControl == null && getMob().isPresent()) {
            specialJumpControl = new JumpController(getMob().get());
        }
        normalizeRotationDelta();
        ServerPlayerEntity player = (ServerPlayerEntity) getPlayer();
        if (player != null) {
            if (invalid(player)) {
                return false;
            }
            boolean cost = SoulEnergies.cost(player, SE_COST_PER_TICK);
            if (!cost) {
                handleDepleted(player);
            }
            return cost;
        }
        return true;
    }

    protected void highlightTarget(boolean useAlt, LivingEntity target) {
        HighlighterType highlighterType = HighlighterType.MIND_OPERATOR_HELPER;
        if (useAlt) {
            highlighterType = HighlighterType.MIND_OPERATOR_HELPER_ALT;
        }
        EntityHighlightManager.get(level).highlight(highlighterType, target);
    }

    protected void setAggressiveWhenTargetIsFound(boolean useAlt, MobEntity mob, @Nullable LivingEntity target) {
        mob.setAggressive(target != null && !useAlt);
    }

    protected boolean invalid(PlayerEntity player) {
        return !(MIND_OPERATORS.get().contains(player.getMainHandItem().getItem()));
    }

    private void normalizeRotationDelta() {
        while (rotationDelta >= 360) {
            rotationDelta -= 360;
        }
        while (rotationDelta < 0) {
            rotationDelta += 360;
        }
    }

    private void tickHandlers(T mob, ServerPlayerEntity player) {
        meleeHandler.tick(mob, player, data);
        movementHandler.tick(mob, player, data);
        rightClickHandler.tick(mob, player, data);
        rotationHandler.tick(mob, player, data);
        targetFinder.tick(mob, player, data);
        if (alternativeTargetFinder != null) {
            alternativeTargetFinder.tick(mob, player, data);
        }
    }

    protected abstract MeleeHandler<? super T> initMeleeHandler();

    protected abstract MovementHandler<? super T> initMovementHandler();

    protected RightClickHandler<? super T> initRightClickHandler() {
        return NoRightClickHandler.INSTANCE;
    }

    protected RotationHandler<? super T> initRotationHandler() {
        return DefaultRotationHandler.INSTANCE;
    }

    protected TargetFinder<? super T> initTargetFinder() {
        return RangedTargetFinder.DEFAULT;
    }

    @Nullable
    protected TargetFinder<? super T> initAlternativeTargetFinder() {
        return null;
    }

    public float getRotationDelta() {
        return rotationDelta;
    }

    public void setRotationDelta(float rotationDelta) {
        this.rotationDelta = rotationDelta;
    }

    @Override
    public CompoundNBT save() {
        CompoundNBT compoundNBT = super.save();
        compoundNBT.putFloat("RotationDelta", getRotationDelta());
        compoundNBT.put("SpecialData", data);
        meleeHandler.saveTo(data);
        movementHandler.saveTo(data);
        rightClickHandler.saveTo(data);
        rotationHandler.saveTo(data);
        targetFinder.saveTo(data);
        if (alternativeTargetFinder != null) {
            alternativeTargetFinder.saveTo(data);
        }
        return compoundNBT;
    }

    private List<Warning> getDefaultWarnings() {
        return new ArrayList<>(Arrays.asList(
                Warning.of((mob, player) -> getExtractableSEOf(player) <= 10000, makeText("lowSE.10000")),
                Warning.of((mob, player) -> getExtractableSEOf(player) <= 2000, makeText("lowSE.2000")),
                Warning.of((mob, player) -> getExtractableSEOf(player) <= 500, makeText("lowSE.500")).setSevere(true),
                Warning.of((mob, player) -> getTimeRemaining(mob, player) <= 600, makeText("lackTime.600")),
                Warning.of((mob, player) -> getTimeRemaining(mob, player) <= 100, makeText("lackTime.100")).setSevere(true)
        ));
    }

    private static TranslationTextComponent makeText(String name, Object... args) {
        return new TranslationTextComponent(Soullery.prefixMsg("controlling", name), args);
    }

    private int getTimeRemaining(MobEntity mob, ServerPlayerEntity player) {
        return SoulManager.get(player.getLevel()).getTimes().timeRemaining(mob, getType());
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    @Override
    protected void handleDeath(T mob, ServerPlayerEntity player) {
        player.sendMessage(makeText("death", mob.getDisplayName()).withStyle(TextFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
    }

    @Override
    protected void handleDepleted(ServerPlayerEntity player) {
        player.sendMessage(DEPLETED.copy().withStyle(TextFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
    }

    public class Warnings {
        private final List<Warning> warnings;
        private boolean firstTick = true;

        public Warnings() {
            this(getDefaultWarnings());
        }

        public Warnings(List<Warning> warnings) {
            this.warnings = warnings;
        }

        public void tick(MobEntity mob, ServerPlayerEntity player) {
            for (Iterator<Warning> iterator = warnings.iterator(); iterator.hasNext(); ) {
                Warning warning = iterator.next();
                if (warning.getCondition().test(mob, player)) {
                    if (!firstTick) {
                        player.sendMessage(warning.getMessage().copy().withStyle(warning.isSevere() ? TextFormatting.GOLD : TextFormatting.BLUE), ChatType.GAME_INFO, Util.NIL_UUID);
                    }
                    iterator.remove();
                }
            }
            firstTick = false;
        }
    }

    public static class Warning {
        private final BiPredicate<? super MobEntity, ? super ServerPlayerEntity> condition;
        private final ITextComponent message;
        private final boolean severe;

        private Warning(BiPredicate<? super MobEntity, ? super ServerPlayerEntity> condition, ITextComponent message, boolean severe) {
            this.condition = condition;
            this.message = message;
            this.severe = severe;
        }

        public static Warning of(BiPredicate<? super MobEntity, ? super ServerPlayerEntity> condition, ITextComponent message) {
            return new Warning(condition, message, false);
        }

        public Warning setSevere(boolean severe) {
            return new Warning(condition, message, severe);
        }

        public BiPredicate<? super MobEntity, ? super ServerPlayerEntity> getCondition() {
            return condition;
        }

        public ITextComponent getMessage() {
            return message;
        }

        public boolean isSevere() {
            return severe;
        }
    }
}
