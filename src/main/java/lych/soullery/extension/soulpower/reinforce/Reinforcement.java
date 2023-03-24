package lych.soullery.extension.soulpower.reinforce;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public abstract class Reinforcement {
    private final LazyValue<EntityType<?>> type;

    public Reinforcement(EntityType<?> type) {
        this(() -> type);
    }

    public Reinforcement(ResourceLocation typeName) {
        this(() -> ForgeRegistries.ENTITIES.containsKey(typeName) ? ForgeRegistries.ENTITIES.getValue(typeName) : null);
    }

    public Reinforcement(Supplier<EntityType<?>> type) {
        this.type = new LazyValue<>(type);
    }

    public boolean isItemSuitable(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return isItemPosSuitable(stack);
    }

    public static boolean checkCompatibility(Set<Reinforcement> reinforcements) {
        for (Reinforcement reinforcement : reinforcements) {
            if (!checkCompatibility(reinforcement, reinforcements)) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkCompatibility(Reinforcement reinforcement, Set<Reinforcement> otherReinforcements) {
        return otherReinforcements.stream().allMatch(otherReinforcement -> checkCompatibility(reinforcement, otherReinforcement));
    }

    public static boolean checkCompatibility(Reinforcement r1, Reinforcement r2) {
        return r1.isCompatibleWith(r2) && r2.isCompatibleWith(r1);
    }

    protected abstract boolean isItemPosSuitable(ItemStack stack);

    protected boolean isCompatibleWith(Reinforcement reinforcement) {
        return true;
    }

    @SuppressWarnings("ConstantConditions")
    public boolean isPresent() {
        if (this instanceof OptionalReinforcement) {
            return get() != null;
        }
        return true;
    }
//
    @SuppressWarnings("ConstantConditions")
    public EntityType<?> getType() {
        EntityType<?> type = get();
        if (type == null) {
            throw new UnsupportedOperationException("Type not loaded");
        }
        return type;
    }

    private EntityType<?> get() {
        return type.get();
    }

    protected boolean hasEvents() {
        return false;
    }

    public int getTotalLevel(Iterable<ItemStack> iterable) {
        return Streams.stream(iterable).mapToInt(this::getLevel).sum();
    }

    public int getLevel(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return ReinforcementHelper.getReinforcementLevel(stack, this);
    }

    public boolean isSpecial() {
        return false;
    }

    public final TextFormatting getStyle() {
        return isSpecial() ? TextFormatting.DARK_PURPLE : TextFormatting.BLUE;
    }

    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reinforcement)) return false;
        Reinforcement that = (Reinforcement) o;
        return Objects.equals(getType(), that.getType());
    }

    public int getCost(int oldLevel, int newLevel) {
        Preconditions.checkArgument(newLevel >= oldLevel, "NewLevel must not be smaller than oldLevel");
        return (oldLevel + newLevel + 1) * (newLevel - oldLevel) / 2;
    }

    public int getUpgradeableNewLevel(int count, int oldLevel) {
        Preconditions.checkArgument(count > 0, "Count must be positive");
        return (int) Math.floor((Math.sqrt(1 + 4 * oldLevel * oldLevel + 4 * oldLevel + 8 * count) - 1) / 2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", getType())
                .toString();
    }
}
