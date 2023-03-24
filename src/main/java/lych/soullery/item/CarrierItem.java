package lych.soullery.item;

import lych.soullery.Soullery;
import lych.soullery.util.DequeNBTOperator;
import lych.soullery.util.EnumConstantNotFoundException;
import lych.soullery.util.IIdentifiableEnum;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.SoftOverride;

import java.util.List;

public abstract class CarrierItem<T extends INBT, E> extends Item implements IModeChangeable {
    protected static final String CHANGE_MODE = Soullery.prefixMsg("item", "carrier.set_mode");
    protected static final String CAPACITY = Soullery.prefixMsg("item", "carrier.capacity");
    protected static final String TAG = "SoulCarrier.";
    protected static final String IO_TAG = TAG + "IOMode";
    private final DequeNBTOperator<T> operator;

    protected CarrierItem(Properties properties, String name, int size, int type) {
        super(properties);
        operator = new DequeNBTOperator<>(TAG + name, size, type);
    }

    @Override
    public void changeMode(ItemStack stack, ServerPlayerEntity player) {
        if (maxSize(stack) <= 1) {
            return;
        }
        IOMode mode = setNextIOMode(stack);
        player.sendMessage(new TranslationTextComponent(CHANGE_MODE).append(mode.getText()), Util.NIL_UUID);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (!isEmpty(context.getItemInHand())) {
            if (isEmpty(context.getItemInHand())) {
                return ActionResultType.FAIL;
            }
            if (context.getLevel().isClientSide()) {
                return ActionResultType.SUCCESS;
            }
            E e = remove(context.getItemInHand(), context);
            if (e == null) {
                return ActionResultType.FAIL;
            }
            useOn(e, context);
            return ActionResultType.CONSUME;
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        if (isSpecial(stack)) {
            text.add(IOMode.MODE.copy().append(getIOMode(stack).getText()));
        }
        text.add(new TranslationTextComponent(CAPACITY, size(stack), maxSize(stack)));
    }

    @Nullable
    protected abstract E fromNBT(T nbt, ItemUseContext context);

    protected abstract T toNBT(E e, ItemUseContext context);

    protected void useOn(E e, ItemUseContext context) {}

    protected void addNBT(ItemStack stack, T nbt) {
        getIOMode(stack).add(stack.getOrCreateTag(), operator, nbt);
    }

    @Nullable
    public E remove(ItemStack stack, ItemUseContext context) {
        return fromNBT(removeNBT(stack), context);
    }

    protected T removeNBT(ItemStack stack) {
        return getIOMode(stack).remove(stack.getOrCreateTag(), operator);
    }

    @Nullable
    protected T getNBT(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        }
        return getIOMode(stack).get(stack.getOrCreateTag(), operator);
    }

    public boolean isFull(ItemStack stack) {
        if (!stack.hasTag()) {
            return false;
        }
        return operator.isFull(stack.getTag());
    }

    public boolean isEmpty(ItemStack stack) {
        if (!stack.hasTag()) {
            return true;
        }
        return operator.isEmpty(stack.getTag());
    }

    public int size(ItemStack stack) {
        if (!stack.hasTag()) {
            return 0;
        }
        return operator.size(stack.getTag());
    }

    public int maxSize(ItemStack stack) {
        return operator.maxSize();
    }

    @SoftOverride
    public boolean isSoulFoil(ItemStack stack) {
        return isSpecial(stack);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        Rarity rarity = super.getRarity(stack);
        if (size(stack) > 0) {
            rarity = next(rarity);
        }
        if (isSpecial(stack)) {
            rarity = next(next(rarity));
        }
        return rarity;
    }

    protected boolean isSpecial(ItemStack stack) {
        return maxSize(stack) > 1;
    }

    private static Rarity next(Rarity rarity) {
        switch (rarity) {
            case COMMON:
                return Rarity.UNCOMMON;
            case UNCOMMON:
                return Rarity.RARE;
            case RARE:
                return Rarity.EPIC;
            case EPIC:
                return ModRarities.LEGENDARY;
            default:
                return rarity == ModRarities.LEGENDARY ? ModRarities.MAX : rarity;
        }
    }

    public static IOMode getIOMode(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(IO_TAG, Constants.NBT.TAG_ANY_NUMERIC) ? IOMode.byId(stack.getTag().getInt(IO_TAG)) : IOMode.DEFAULT;
    }

    public static void setIOMode(ItemStack stack, IOMode mode) {
        stack.getOrCreateTag().putInt(IO_TAG, mode.getId());
    }

    public static IOMode setNextIOMode(ItemStack stack) {
        IOMode next = getIOMode(stack).next();
        setIOMode(stack, next);
        return next;
    }

    public enum IOMode implements IIdentifiableEnum {
        FIFO(Soullery.prefixMsg("item", "carrier.fifo")) {
            @Override
            public <T extends INBT> void add(CompoundNBT compoundNBT, DequeNBTOperator<T> operator, T nbt) {
                operator.addFirst(compoundNBT, nbt);
            }

            @Override
            public <T extends INBT> T remove(CompoundNBT compoundNBT, DequeNBTOperator<T> operator) {
                return operator.pop(compoundNBT);
            }

            @Nullable
            @Override
            public <T extends INBT> T get(CompoundNBT compoundNBT, DequeNBTOperator<T> operator) {
                if (operator.isEmpty(compoundNBT)) {
                    return null;
                }
                return operator.front(compoundNBT);
            }
        },
        LIFO(Soullery.prefixMsg("item", "carrier.lifo")) {
            @Override
            public <T extends INBT> void add(CompoundNBT compoundNBT, DequeNBTOperator<T> operator, T nbt) {
                operator.push(compoundNBT, nbt);
            }

            @Override
            public <T extends INBT> T remove(CompoundNBT compoundNBT, DequeNBTOperator<T> operator) {
                return operator.pop(compoundNBT);
            }

            @Nullable
            @Override
            public <T extends INBT> T get(CompoundNBT compoundNBT, DequeNBTOperator<T> operator) {
                if (operator.isEmpty(compoundNBT)) {
                    return null;
                }
                return operator.top(compoundNBT);
            }
        };
        public static final IOMode DEFAULT = LIFO;
        public static final ITextComponent MODE = new TranslationTextComponent(Soullery.prefixMsg("item", "carrier.io_mode"));
        private final ITextComponent text;

        IOMode(String text) {
            this(new TranslationTextComponent(text).withStyle(TextFormatting.GOLD));
        }

        IOMode(ITextComponent text) {
            this.text = text;
        }

        public static IOMode byId(int id) {
            try {
                return IIdentifiableEnum.byOrdinal(values(), id);
            } catch (EnumConstantNotFoundException e) {
                return DEFAULT;
            }
        }

        public abstract <T extends INBT> void add(CompoundNBT compoundNBT, DequeNBTOperator<T> operator, T nbt);

        public abstract <T extends INBT> T remove(CompoundNBT compoundNBT, DequeNBTOperator<T> operator);

        @Nullable
        public abstract <T extends INBT> T get(CompoundNBT compoundNBT, DequeNBTOperator<T> operator);

        public IOMode next() {
            if (getId() == values().length - 1) {
                return byId(0);
            }
            return byId(getId() + 1);
        }

        public ITextComponent getText() {
            return text;
        }
    }
}
