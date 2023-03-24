package lych.soullery.dispenser;

import lych.soullery.entity.projectile.SoulArrowEntity;
import lych.soullery.item.ModItems;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import static net.minecraft.block.DispenserBlock.registerBehavior;

public final class ModDispenserBehaviors {
    private ModDispenserBehaviors() {}

    public static void registerBehaviors() {
        registerBehavior(ModItems.SOUL_LAVA_BUCKET, new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(IBlockSource source, ItemStack stack) {
                BucketItem bucket = (BucketItem) stack.getItem();
                BlockPos pos = source.getPos().offset(source.getBlockState().getValue(DispenserBlock.FACING).getNormal());
                ServerWorld world = source.getLevel();
                if (bucket.emptyBucket(null, world, pos, null)) {
                    bucket.checkExtraContent(world, stack, pos);
                    return new ItemStack(Items.BUCKET);
                } else {
                    return defaultBehavior.dispense(source, stack);
                }
            }
        });
        registerBehavior(ModItems.SOUL_ARROW, new ProjectileDispenseBehavior() {
            @Override
            protected ProjectileEntity getProjectile(World world, IPosition position, ItemStack stack) {
                SoulArrowEntity arrow = new SoulArrowEntity(world, position.x(), position.y(), position.z());
                arrow.pickup = PickupStatus.ALLOWED;
                return arrow;
            }
        });
    }
}
