package lych.soullery.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class EntityCarrierItem extends CarrierItem<CompoundNBT, MobEntity> {
    public EntityCarrierItem(Properties properties, int size) {
        super(properties, "Entities", size, Constants.NBT.TAG_COMPOUND);
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
//      Redirect the Item Stack to store entities when player has "instabuild" ability
        stack = player.getItemInHand(hand);
        if (entity instanceof MobEntity) {
            if (isFull(stack) || !entity.canChangeDimensions()) {
                return ActionResultType.FAIL;
            }
            addNBT(stack, toNBT((MobEntity) entity));
            entity.remove();
            return ActionResultType.sidedSuccess(player.level.isClientSide());
        }
        return super.interactLivingEntity(stack, player, entity, hand);
    }

    @Nullable
    @Override
    protected MobEntity fromNBT(CompoundNBT nbt, ItemUseContext context) {
        Entity entity = EntityType.loadEntityRecursive(nbt, context.getLevel(), Function.identity());
        return entity instanceof MobEntity ? (MobEntity) entity : null;
    }

    @Override
    protected CompoundNBT toNBT(MobEntity mob, ItemUseContext context) {
        return toNBT(mob);
    }

    private static CompoundNBT toNBT(MobEntity mob) {
        if (mob.isVehicle()) {
            mob.getPassengers().forEach(Entity::stopRiding);
        }
        return mob.serializeNBT();
    }

    @Override
    protected void useOn(MobEntity mob, ItemUseContext context) {
        mob.moveTo(context.getClickLocation());
        context.getLevel().addFreshEntity(mob);
    }
}
