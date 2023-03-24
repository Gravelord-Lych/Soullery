package lych.soullery.effect;

import lych.soullery.world.gen.dimension.ModDimensions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.MovementInput;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public final class SoulPollutionHandler {
    public static final RangedInteger DURATION_RANGE = RangedInteger.of(100, 300);
    public static final double POLLUTE_PROBABILITY = 0.0005;
    public static final double POLLUTE_PROBABILITY_SPECIAL = 0.04;


    private SoulPollutionHandler() {}

    @OnlyIn(Dist.CLIENT)
    public static void handleInput(PlayerEntity player, Random random, MovementInput input) {
        float sineValue = MathHelper.sin(player.tickCount / 10f);
        float cosineValue = MathHelper.cos(player.tickCount / 10f);
        input.leftImpulse *= 1 + sineValue * (sineValue > 0 ? 0.8f : 0.4f);
        input.forwardImpulse *= 1 + cosineValue * (cosineValue > 0 ? 0.6f : 0.3f);
        if (input.jumping && random.nextInt(4) == 0) {
            input.jumping = false;
        } else if (!input.jumping && random.nextInt(125) == 0) {
            input.jumping = true;
        }
    }

    public static void mayPollute(PlayerEntity player, World world, double probability) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (world.dimension() == ModDimensions.SOUL_LAND && player.getRandom().nextDouble() < probability) {
            player.addEffect(new EffectInstance(ModEffects.SOUL_POLLUTION, DURATION_RANGE.randomValue(player.getRandom())));
        }
    }
}
