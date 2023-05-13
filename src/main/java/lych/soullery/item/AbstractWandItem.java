package lych.soullery.item;

import lych.soullery.network.WandSoundNetwork;
import lych.soullery.util.SoulEnergies;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractWandItem<T extends AbstractWandItem<T>> extends Item implements IUpgradeableItem {
    private static final Map<Class<?>, Map<Integer, ?>> GLOBAL_TIER_MAP = new HashMap<>();
    private final int energyCost;
    private final int tier;
    private final LazyValue<Map<Integer, T>> tierMap = new LazyValue<>(this::initTierMap);

    public AbstractWandItem(Properties properties, int energyCost, int tier) {
        super(properties);
        this.energyCost = energyCost;
        this.tier = tier;
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, T> initTierMap() {
        if (!GLOBAL_TIER_MAP.containsKey(getClass())) {
            Map<Integer, T> tierMap = ForgeRegistries.ITEMS.getValues().stream().filter(this::isInstance).map(this::cast).collect(Collectors.toMap(AbstractWandItem::getTier, Function.identity()));
            GLOBAL_TIER_MAP.put(getClass(), tierMap);
            return tierMap;
        }
        return (Map<Integer, T>) GLOBAL_TIER_MAP.get(getClass());
    }

    @SuppressWarnings("unchecked")
    private boolean isInstance(@Nullable Item item) {
        if (!(item instanceof AbstractWandItem)) {
            return false;
        }
        try {
            T t = (T) item;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private T cast(Item item) {
        return (T) item;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (level.isClientSide() && shouldSuccessClientSide(player, hand)) {
            return ActionResult.success(player.getItemInHand(hand));
        }
        if (!level.isClientSide()) {
            ActionResultType result = null;
            if (hasEnoughEnergy(player) && ModItems.canDamage(player.getItemInHand(hand))) {
                result = performWandUse((ServerWorld) level, (ServerPlayerEntity) player, hand);
                if (result != null && result.consumesAction()) {
                    SoulEnergies.cost(player, energyCost);
                    ModItems.damage(player, hand);
                    if (getSound() != null) {
                        WandSoundNetwork.INSTANCE.send(PacketDistributor.NEAR.with(() -> PacketDistributor.TargetPoint.p(player.getX(), player.getY(), player.getZ(), 30, player.level.dimension()).get()), getId(this));
                    }
                }
            }
            if (result != null) {
                return new ActionResult<>(result, player.getItemInHand(hand));
            }
        }
        return super.use(level, player, hand);
    }

    protected boolean shouldSuccessClientSide(PlayerEntity player, Hand hand) {
        return hasEnoughEnergy(player) && ModItems.canDamage(player.getItemInHand(hand));
    }

    protected final boolean hasEnoughEnergy(PlayerEntity player) {
        return SoulEnergies.getExtractableSEOf(player) >= energyCost;
    }

    @Nullable
    protected abstract ActionResultType performWandUse(ServerWorld level, ServerPlayerEntity player, Hand hand);

    @Nullable
    public abstract SoundEvent getSound();

    public float getVolume(Random random) {
        return 1;
    }

    public float getPitch(Random random) {
        return 1;
    }

    @Override
    public boolean canUpgrade(ItemStack stack) {
        if (!(stack.getItem() instanceof AbstractWandItem)) {
            return false;
        }
        return getTierMap().containsKey(((AbstractWandItem<?>) stack.getItem()).getTier() + 1);
    }

    @Override
    public ItemStack upgraded(ItemStack old) {
        checkUpgradeable(old);
        T t = getTierMap().get(((AbstractWandItem<?>) old.getItem()).getTier() + 1);
        return new ItemStack(t);
    }

    public Map<Integer, T> getTierMap() {
        return tierMap.get();
    }

    public int getTier() {
        return tier;
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return getTier() > 1;
    }
}
