package lych.soullery.extension.soulpower.reinforce;

import lych.soullery.Soullery;
import lych.soullery.util.DefaultValues;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import lych.soullery.util.Utils;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public final class ReinforcementHelper {
    public static final String TAG = "SoulCraft.Reinforcements";
    public static final String TYPE = "SoulCraft.ReinforcementType";
    public static final String LVL = "SoulCraft.ReinforcementLevel";

    private ReinforcementHelper() {}

    public static ListNBT serializeNBT(Map<Reinforcement, Integer> reinforcements) {
        ListNBT listNBT = new ListNBT();
        for (Entry<Reinforcement, Integer> entry : reinforcements.entrySet()) {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString(TYPE, getReinforcementName(entry.getKey()));
            compoundNBT.putInt(LVL, entry.getValue());
            listNBT.add(compoundNBT);
        }
        return listNBT;
    }

    public static Map<Reinforcement, Integer> deserializeNBT(ListNBT listNBT) {
        Map<Reinforcement, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT compoundNBT = listNBT.getCompound(i);
            String name = compoundNBT.getString(TYPE);
            ResourceLocation location = ResourceLocation.tryParse(name);
            if (location == null) {
                Soullery.LOGGER.warn(Reinforcements.REINFORCEMENT, "{} is not a valid ResourceLocation, skipped", name);
                continue;
            }
            EntityType<?> type = ForgeRegistries.ENTITIES.getValue(location);
            Reinforcement reinforcement = Utils.applyIfNonnull(type, Reinforcements::get);
            if (reinforcement == null) {
                Soullery.LOGGER.warn(Reinforcements.REINFORCEMENT, "Reinforcement {} has unknown EntityType, skipped", location.getPath());
                continue;
            }
            int level = compoundNBT.getInt(LVL);
            map.put(reinforcement, level);
        }
        return map;
    }

    private static boolean removeReinforcement(ListNBT listNBT, Reinforcement reinforcement) {
        if (listNBT.isEmpty()) {
            return false;
        }
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT compoundNBT = listNBT.getCompound(i);
            if (Objects.equals(compoundNBT.getString(TYPE), getReinforcementName(reinforcement))) {
                listNBT.remove(i);
                return true;
            }
        }
        return false;
    }

    private static void addReinforcementDirectly(ListNBT listNBT, Reinforcement reinforcement, int level) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putString(TYPE, getReinforcementName(reinforcement));
        compoundNBT.putInt(LVL, level);
        listNBT.add(compoundNBT);
    }

    private static int getReinforcementLevel(ListNBT listNBT, Reinforcement reinforcement) {
        if (listNBT.isEmpty()) {
            return 0;
        }
        if (listNBT.getElementType() != NBT.TAG_COMPOUND) {
            throw new IllegalArgumentException("listNBT must contain CompoundNBT");
        }
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT compoundNBT = listNBT.getCompound(i);
            String name = compoundNBT.getString(TYPE);
            if (Objects.equals(name, getReinforcementName(reinforcement))) {
                return compoundNBT.getInt(LVL);
            }
        }
        return 0;
    }

    private static String getReinforcementName(Reinforcement reinforcement) {
        return Utils.getRegistryName(reinforcement.getType()).toString();
    }

    public static boolean hasReinforcements(ItemStack stack) {
        return !getReinforcementTags(stack).isEmpty();
    }

    public static void putReinforcements(ItemStack stack, Map<Reinforcement, Integer> map) {
        putReinforcements(stack, map, false);
    }

    public static void putReinforcements(ItemStack stack, Map<Reinforcement, Integer> map, boolean forcePut) {
        if (map.isEmpty()) {
            if (stack.hasTag() && hasReinforcements(stack)) {
                stack.removeTagKey(TAG);
            }
            return;
        }
        if (!forcePut) {
            if (map.size() > maxCountOf(stack)) {
                throw new IllegalArgumentException("Too many reinforcements, expected <= " + maxCountOf(stack));
            }
            if (!Reinforcement.checkCompatibility(map.keySet())) {
                throw new IllegalArgumentException("Given reinforcements are incompatible with each other");
            }
            map.entrySet().removeIf(e -> !e.getKey().isItemSuitable(stack));
        }
        stack.getOrCreateTag().put(TAG, serializeNBT(map));
    }

    public static Map<Reinforcement, Integer> getReinforcements(ItemStack stack) {
        return deserializeNBT(getReinforcementTags(stack));
    }

    public static ApplicationStatus addReinforcement(ItemStack stack, Reinforcement reinforcement, int level) {
        return addReinforcement(stack, reinforcement, level, false);
    }

    public static ApplicationStatus addOrUpgradeReinforcement(ItemStack stack, Reinforcement reinforcement, int newLevel) {
        return addReinforcement(stack, reinforcement, newLevel, true);
    }

    private static ApplicationStatus addReinforcement(ItemStack stack, Reinforcement reinforcement, int level, boolean allowsUpgrade) {
        if (!reinforcement.isItemSuitable(stack)) {
            return ApplicationStatus.UNSUPPORTED;
        }
        if (isReinforced(stack)) {
            return ApplicationStatus.REINFORCED;
        }
        if (level > reinforcement.getMaxLevel()) {
            return ApplicationStatus.LEVEL_TOO_HIGH;
        }
        if (isIncompatible(stack, reinforcement)) {
            return ApplicationStatus.INCOMPATIBLE;
        }
        if (containsReinforcement(stack, reinforcement)) {
            if (!allowsUpgrade) {
                return ApplicationStatus.DUPLICATE;
            }
            removeReinforcement(stack, reinforcement);
        }
        if (!stack.hasTag() || !stack.getTag().contains(TAG, NBT.TAG_LIST)) {
            stack.getOrCreateTag().put(TAG, new ListNBT());
        }
        addReinforcementDirectly(getReinforcementTags(stack), reinforcement, level);
        return ApplicationStatus.OK;
    }

    public static boolean isReinforced(ItemStack stack) {
        return getReinforcementCount(stack) >= maxCountOf(stack);
    }

    public static boolean isIncompatible(ItemStack stack, Reinforcement reinforcement) {
        return !Reinforcement.checkCompatibility(reinforcement, getReinforcements(stack).keySet());
    }

    public static boolean removeReinforcement(ItemStack stack, Reinforcement reinforcement) {
        return removeReinforcement(getReinforcementTags(stack), reinforcement);
    }

    public static boolean containsReinforcement(ItemStack stack, Reinforcement reinforcement) {
        return getReinforcementLevel(getReinforcementTags(stack), reinforcement) > 0;
    }

    public static int getReinforcementLevel(ItemStack stack, Reinforcement reinforcement) {
        return getReinforcementLevel(getReinforcementTags(stack), reinforcement);
    }

    public static ListNBT getReinforcementTags(ItemStack stack) {
        if (!stack.hasTag()) {
            return new ListNBT();
        }
        return stack.getTag().getList(TAG, NBT.TAG_COMPOUND);
    }

    public static int getReinforcementCount(ItemStack stack) {
        return getReinforcementTags(stack).size();
    }

    private static int maxCountOf(ItemStack stack) {
        return ((IItemStackMixin) (Object) stack).getMaxReinforcementCount();
    }

    public enum ApplicationStatus implements IIdentifiableEnum {
        OK(null),
        DUPLICATE(new TranslationTextComponent(Soullery.prefixMsg("reinforcement", "status.duplicate"))),
        INCOMPATIBLE(new TranslationTextComponent(Soullery.prefixMsg("reinforcement", "status.incompatible"))),
        LEVEL_TOO_HIGH(new TranslationTextComponent(Soullery.prefixMsg("reinforcement", "status.level_too_high"))),
        REINFORCED(new TranslationTextComponent(Soullery.prefixMsg("reinforcement", "status.reinforced"))),
        UNSUPPORTED(new TranslationTextComponent(Soullery.prefixMsg("reinforcement", "status.unsupported")));

        @Nullable
        private final ITextComponent text;

        ApplicationStatus(@Nullable ITextComponent text) {
            this.text = text;
        }

        @Nullable
        public static ApplicationStatus byId(int id) {
            try {
                return IIdentifiableEnum.byOrdinal(values(), id);
            } catch (EnumConstantNotFoundException e) {
                return null;
            }
        }

        public ITextComponent getErrorText() {
            return Utils.getOrDefault(text, DefaultValues.dummyTextComponent()).copy().withStyle(TextFormatting.RED);
        }

        public boolean isOk() {
            return this == OK;
        }
    }
}
