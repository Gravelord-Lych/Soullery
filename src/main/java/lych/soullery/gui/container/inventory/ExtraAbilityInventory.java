package lych.soullery.gui.container.inventory;

import lych.soullery.gui.container.ExtraAbilityContainer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ExtraAbilityInventory extends Inventory {
    @Nullable
    private ExtraAbilityContainer container;

    public ExtraAbilityInventory(int size) {
        super(size);
    }

    public ExtraAbilityInventory(ItemStack... stacks) {
        super(stacks);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (getContainer() != null) {
            getContainer().slotsChanged(this);
        }
    }

    @Nullable
    public ExtraAbilityContainer getContainer() {
        return container;
    }

    public void setContainer(@Nullable ExtraAbilityContainer container) {
        this.container = container;
    }
}
