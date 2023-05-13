package lych.soullery.mixin;

import lych.soullery.extension.control.MindOperator;
import lych.soullery.extension.control.SoulManager;
import lych.soullery.util.mixin.IGoalSelectorMixin;
import lych.soullery.util.mixin.IMobEntityMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements IMobEntityMixin {
    @Shadow @Final public GoalSelector goalSelector;
    @Shadow @Final public GoalSelector targetSelector;

    private MobEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Shadow protected abstract float getEquipmentDropChance(EquipmentSlotType p_205712_1_);

    @Shadow protected abstract void sendDebugPackets();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void handleGoalSelectors(EntityType<?> type, World world, CallbackInfo ci) {
        ((IGoalSelectorMixin) goalSelector).setMob((MobEntity) (Object) this);
        ((IGoalSelectorMixin) goalSelector).setAlt(new GoalSelector(world.getProfilerSupplier()));
        ((IGoalSelectorMixin) targetSelector).setMob((MobEntity) (Object) this);
        ((IGoalSelectorMixin) targetSelector).setAlt(new GoalSelector(world.getProfilerSupplier()));
    }

    @Inject(method = "serverAiStep",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/MobEntity;customServerAiStep()V")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/profiler/IProfiler;push(Ljava/lang/String;)V", ordinal = 0), cancellable = true)
    private void noControls(CallbackInfo ci) {
        if (isOperated()) {
            sendDebugPackets();
            ci.cancel();
        }
    }

    private boolean isOperated() {
        if (level.isClientSide()) {
            throw new IllegalStateException();
        }
        return SoulManager.getControllers((MobEntity) (Object) this).stream().anyMatch(c -> c instanceof MindOperator);
    }

    @Override
    public float callGetEquipmentDropChance(EquipmentSlotType slotType) {
        return getEquipmentDropChance(slotType);
    }
}
