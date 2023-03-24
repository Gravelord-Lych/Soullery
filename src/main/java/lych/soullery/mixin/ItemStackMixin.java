package lych.soullery.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import lych.soullery.config.ConfigHelper;
import lych.soullery.extension.soulpower.reinforce.Reinforcement;
import lych.soullery.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soullery.util.RomanNumeralGenerator;
import lych.soullery.util.mixin.IItemMixin;
import lych.soullery.util.mixin.IItemStackMixin;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStackMixin {
    @Shadow public abstract Item getItem();

    @Override
    public int getMaxReinforcementCount() {
        return ((IItemMixin) getItem()).getMaxReinforcementCount((ItemStack) (Object) this);
    }

    @Override
    public boolean hasSoulFoil() {
        if (ReinforcementHelper.hasReinforcements((ItemStack) (Object) this)) {
            return true;
        }
        return ((IItemMixin) getItem()).isSoulFoil((ItemStack) (Object) this);
    }

    @Inject(method = "hasFoil", at = @At(value = "HEAD"), cancellable = true)
    private void checkSoulFoilExistence(CallbackInfoReturnable<Boolean> cir) {
        if (hasSoulFoil()) {
            cir.setReturnValue(false);
        }
    }

//  For better display, so require = 0
    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Multimap;entries()Ljava/util/Collection;", remap = false), require = 0)
    private Collection<Map.Entry<Attribute, AttributeModifier>> sortAttributeModifiers(Multimap<Attribute, AttributeModifier> instance) {
//      Do not sort armors because armor do not have attack damage and attack speed.
        if (getItem() instanceof ArmorItem) {
            return instance.entries();
        }
        return Lists.reverse(instance.entries().stream().sorted(this::compare).collect(Collectors.toList()));
    }

    private int compare(Map.Entry<Attribute, AttributeModifier> e1, Map.Entry<Attribute, AttributeModifier> e2) {
        if (e1.getValue().getId() == Item.BASE_ATTACK_DAMAGE_UUID && e2.getValue().getId() == Item.BASE_ATTACK_SPEED_UUID) {
            return 1;
        }
        if (e2.getValue().getId() == Item.BASE_ATTACK_DAMAGE_UUID && e1.getValue().getId() == Item.BASE_ATTACK_SPEED_UUID) {
            return -1;
        }
        if (e1.getValue().getId() == Item.BASE_ATTACK_DAMAGE_UUID || e1.getValue().getId() == Item.BASE_ATTACK_SPEED_UUID) {
            return 1;
        }
        if (e2.getValue().getId() == Item.BASE_ATTACK_DAMAGE_UUID || e2.getValue().getId() == Item.BASE_ATTACK_SPEED_UUID) {
            return -1;
        }
        if (Objects.equals(e1.getValue().getId(), e2.getValue().getId())) {
            return 0;
        }
        String name1 = LanguageMap.getInstance().getOrDefault(e1.getKey().getDescriptionId());
        String name2 = LanguageMap.getInstance().getOrDefault(e2.getKey().getDescriptionId());
        return -name1.compareTo(name2);
    }

    @Inject(method = "getTooltipLines",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;contains(Ljava/lang/String;I)Z", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListNBT;)V")),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void showReinforcements(PlayerEntity player, ITooltipFlag flag, CallbackInfoReturnable<List<ITextComponent>> cir, List<ITextComponent> list) {
        appendReinforcementNames(list, ReinforcementHelper.getReinforcements((ItemStack) (Object) this));
    }

    private void appendReinforcementNames(List<ITextComponent> list, Map<Reinforcement, Integer> reinforcements) {
        if (ReinforcementHelper.hasReinforcements((ItemStack) (Object) this)) {
            for (Map.Entry<Reinforcement, Integer> entry : reinforcements.entrySet()) {
                TextFormatting formatting = entry.getKey().getStyle();
                IFormattableTextComponent component = entry.getKey().getType().getDescription().copy().withStyle(formatting);
                if (entry.getValue() != 1 || entry.getKey().getMaxLevel() != 1) {
                    ITextComponent text;
                    if (ConfigHelper.shouldUseRomanNumeralGenerator()) {
                        text = new StringTextComponent(RomanNumeralGenerator.getRomanNumeral(entry.getValue()));
                    } else {
                        text = new TranslationTextComponent("enchantment.level." + entry.getValue());
                    }
                    component = component.append(" ").append(text);
                }
                list.add(component);
            }
        }
    }
}
