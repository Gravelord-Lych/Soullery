package lych.soullery.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class SimpleArrowItem extends ArrowItem {
    private final BiFunction<? super World, ? super LivingEntity, ? extends AbstractArrowEntity> creator;

    public SimpleArrowItem(BiFunction<? super World, ? super LivingEntity, ? extends AbstractArrowEntity> creator, Properties properties) {
        super(properties);
        this.creator = creator;
    }

    @Override
    public AbstractArrowEntity createArrow(World world, ItemStack stack, LivingEntity owner) {
        AbstractArrowEntity arrow = creator.apply(world, owner);
        if (arrow instanceof ArrowEntity) {
            ((ArrowEntity) arrow).setEffectsFromItem(stack);
        }
        return arrow;
    }
}
