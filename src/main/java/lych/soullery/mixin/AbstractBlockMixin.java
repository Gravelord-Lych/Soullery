package lych.soullery.mixin;


import lych.soullery.extension.fire.Fire;
import lych.soullery.util.mixin.IEntityMixin;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @SuppressWarnings("ConstantValue")
    @Inject(method = "entityInside", at = @At("HEAD"))
    private void handleFire(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (((AbstractBlock) (Object) this) instanceof Block) {
            Block thisBlock = (Block) (Object) this;
            if (Fire.isFireBlock(thisBlock)) {
                Fire fire = Fire.byBlock(thisBlock);
                if (fire.canApplyTo(entity)) {
                    ((IEntityMixin) entity).setFireOnSelf(fire);
                }
            }
        }
    }
}
