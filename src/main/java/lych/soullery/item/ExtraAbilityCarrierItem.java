package lych.soullery.item;

import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

public class ExtraAbilityCarrierItem extends Item {
    public static final String TAG = Utils.snakeToCamel(ModItemNames.EXTRA_ABILITY_CARRIER) + ".";

    public ExtraAbilityCarrierItem(Properties properties) {
        super(properties);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        @Nullable
        IExtraAbility type = getExa(stack);
        if (type == null) {
            return super.getName(stack);
        }
        return new TranslationTextComponent(getDescriptionId() + ".carried", type.getDisplayName());
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        Rarity rarity = super.getRarity(stack);
        if (getExa(stack) != null) {
            switch (rarity) {
                case COMMON:
                case UNCOMMON:
                    return Rarity.RARE;
                case RARE:
                    return Rarity.EPIC;
                case EPIC:
                    return ModRarities.LEGENDARY;
                default:
                    return rarity == ModRarities.LEGENDARY ? ModRarities.MAX : rarity;
            }
        }
        return rarity;
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return getExa(stack) != null;
    }

    @Nullable
    public static IExtraAbility getExa(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(TAG + "ExtraAbility")) {
            return null;
        }
        ResourceLocation location;
        if (stack.hasTag()) {
            location = new ResourceLocation(stack.getTag().getString(TAG + "ExtraAbility"));
        } else {
            location = null;
        }
        return ExtraAbility.get(location);
    }

    public static void setExa(ItemStack stack, IExtraAbility type) {
        stack.getOrCreateTag().putString(TAG + "ExtraAbility", type.getRegistryName().toString());
    }
}
