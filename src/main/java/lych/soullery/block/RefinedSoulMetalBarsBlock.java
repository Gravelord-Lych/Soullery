package lych.soullery.block;

import net.minecraft.util.LazyValue;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RefinedSoulMetalBarsBlock extends SoulMetalBarsBlock {
    private static final int MAX_LINKED_DAMAGE_DIG = 12;
    private static final int MAX_LINKED_DAMAGE_PROJECTILE = 6;
    private static final LazyValue<Map<Integer, RefinedSoulMetalBarsBlock>> REFINED_SOUL_METAL_BARS = new LazyValue<>(RefinedSoulMetalBarsBlock::init);

    public RefinedSoulMetalBarsBlock(Properties properties, int health) {
        super(properties, health);
    }

    private static Map<Integer, RefinedSoulMetalBarsBlock> init() {
        return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getClass() == RefinedSoulMetalBarsBlock.class).map(block -> (RefinedSoulMetalBarsBlock) block).collect(Collectors.toMap(SoulMetalBarsBlock::getHealth, Function.identity()));
    }

    @Override
    public int getMaxLinkedDamageDig() {
        return MAX_LINKED_DAMAGE_DIG;
    }

    @Override
    public int getMaxLinkedDamageProjectile() {
        return MAX_LINKED_DAMAGE_PROJECTILE;
    }

    @Override
    public RefinedSoulMetalBarsBlock get(int health) {
        return REFINED_SOUL_METAL_BARS.get().get(health);
    }
}
