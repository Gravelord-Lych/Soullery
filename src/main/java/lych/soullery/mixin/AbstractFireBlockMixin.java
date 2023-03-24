package lych.soullery.mixin;

import lych.soullery.extension.fire.Fire;
import lych.soullery.util.mixin.IAbstractFireBlockMixin;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin implements IAbstractFireBlockMixin {
    @Shadow
    @Final
    private float fireDamage;

    @Override
    public float getFireDamage() {
        return fireDamage;
    }

    @Redirect(method = "entityInside", at = @At(value = "FIELD", target = "Lnet/minecraft/block/AbstractFireBlock;fireDamage:F"))
    private float hurtEntityInside(AbstractFireBlock instance, BlockState state, World world, BlockPos pos, Entity entity) {
        return ((IAbstractFireBlockMixin) instance).getApplicableFire(entity).getFireDamage(entity, world);
    }

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hurt(Lnet/minecraft/util/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void handleEntityInside(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (!entity.fireImmune()) {
            getApplicableFire(entity).entityInsideFire(state, world, pos, entity);
        }
    }

    @Inject(method = "getState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockReader;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"), cancellable = true)
    private static void addMoreFireBlocks(IBlockReader reader, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = reader.getBlockState(pos.below());
        for (Fire fire : Fire.getTrueFires()) {
            if (fire.canBlockCatchFire(reader, pos, state)) {
                cir.setReturnValue(fire.getState(reader, pos));
                break;
            }
        }
    }
}
