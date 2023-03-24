package lych.soullery.item;

import lych.soullery.Soullery;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class ModItemGroups {
    public static final ItemGroup DEFAULT = new ItemGroup(Soullery.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.SOUL_POWDER);
        }
    };
    public static final ItemGroup MACHINE = new ItemGroup(Soullery.MOD_ID + "_machine") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.SEGEN);
        }
    };

    private ModItemGroups() {}
}
