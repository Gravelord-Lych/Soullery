package lych.soullery.mixin;

import lych.soullery.extension.ExtraAbility;
import lych.soullery.util.ExtraAbilityConstants;
import lych.soullery.util.mixin.IFoodStatsMixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FoodStats.class)
public abstract class FoodStatsMixin implements IFoodStatsMixin {
    @Nullable
    private PlayerEntity player;

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 18))
    private int modifyRegenFoodLevelThreshold(int constant, PlayerEntity player) {
        if (ExtraAbility.OVERDRIVE.isOn(player)) {
            return ExtraAbilityConstants.OVERDRIVE_FOOD_LEVEL_REQUIREMENT;
        }
        return constant;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 80, ordinal = 0))
    private int modifyRegenInterval(int constant, PlayerEntity player) {
        if (ExtraAbility.OVERDRIVE.isOn(player)) {
            return ExtraAbilityConstants.OVERDRIVE_REGEN_INTERVAL;
        }
        return constant;
    }

    @ModifyVariable(method = "eat(IF)V", at = @At("HEAD"), argsOnly = true)
    private int modifyNutrition(int nutrition) {
        if (getPlayer() != null && ExtraAbility.NUTRITIONIST.isOn(getPlayer())) {
            return (int) (nutrition * ExtraAbilityConstants.NUTRITIONIST_NUTRITION_AND_SATURATION_MODIFIER);
        }
        return nutrition;
    }

    @ModifyVariable(method = "eat(IF)V", at = @At("HEAD"), argsOnly = true)
    private float modifySaturationModifier(float saturationModifier) {
        if (getPlayer() != null && ExtraAbility.NUTRITIONIST.isOn(getPlayer())) {
            return saturationModifier * ExtraAbilityConstants.NUTRITIONIST_NUTRITION_AND_SATURATION_MODIFIER;
        }
        return saturationModifier;
    }

    @Nullable
    @Override
    public PlayerEntity getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(PlayerEntity player) {
        this.player = player;
    }
}
