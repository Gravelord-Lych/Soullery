package lych.soullery.item;

import lych.soullery.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

public class SoulPieceItem extends Item {
    public static final String PREFIX = Utils.snakeToCamel(ModItemNames.SOUL_PIECE) + ".";

    public SoulPieceItem(Properties properties) {
        super(properties);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        EntityType<?> type = getType(stack);
        if (type == null) {
            return super.getName(stack);
        }
        return new TranslationTextComponent(getDescriptionId() + ".typed", type.getDescription());
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        Rarity rarity = super.getRarity(stack);
        if (getType(stack) != null) {
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
        return getType(stack) != null;
    }

    @Nullable
    public static EntityType<?> getType(ItemStack stack) {
        if (!stack.hasTag() || !stack.getTag().contains(PREFIX + "EntityType")) {
            return null;
        }
        ResourceLocation location;
        if (stack.hasTag()) {
            location = new ResourceLocation(stack.getTag().getString(PREFIX + "EntityType"));
        } else {
            location = null;
        }
        return location != null && ForgeRegistries.ENTITIES.containsKey(location) ? ForgeRegistries.ENTITIES.getValue(location) : null;
    }

    public static void setType(ItemStack stack, EntityType<?> type) {
        stack.getOrCreateTag().putString(PREFIX + "EntityType", Utils.getRegistryName(type).toString());
    }
}
