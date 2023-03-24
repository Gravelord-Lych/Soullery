package lych.soullery.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

/**
 * Any {@link Item} which implement this interface can store SE. <br>
 */
public interface ItemSEContainer {
    String TRANSFER_MODE_TAG = "SETransferableItem.Mode";

    int getCapacity(ItemStack stack);

    int getMaxReceive(ItemStack stack);

    int getMaxExtract(ItemStack stack);

    default boolean isTransferable(ItemStack stack) {
        return getMaxReceive(stack) > 0 && getMaxExtract(stack) > 0;
    }

    static TransferMode getMode(ItemStack stack) {
        if (!stack.hasTag()) {
            return TransferMode.NORMAL;
        }
        return TransferMode.byId(stack.getTag().getInt(TRANSFER_MODE_TAG)).orElse(TransferMode.NORMAL);
    }

    static void setMode(ItemStack stack, TransferMode mode) {
        stack.getOrCreateTag().putInt(TRANSFER_MODE_TAG, mode.getId());
    }

    enum TransferMode {
        NORMAL(0, "normal"),
        IN(1, "in"),
        OUT(2, "out");

        public static final TranslationTextComponent MODE_KEY = new TranslationTextComponent("item.soullery.item_soul_energy_container.transfer.mode");

        private final int id;
        private final String key;

        TransferMode(int id, String key) {
            this.id = id;
            this.key = key;
        }

        public int getId() {
            return id;
        }

        public TranslationTextComponent makeTranslationKey() {
            return new TranslationTextComponent(MODE_KEY.getKey() + "." + key);
        }

        public static Optional<TransferMode> byId(int id) {
            for (TransferMode mode : TransferMode.values()) {
                if (mode.id == id) {
                    return Optional.of(mode);
                }
            }
            return Optional.empty();
        }
    }
}
