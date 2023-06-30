package lych.soullery.api.capability;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IItemVanishingSkillData extends INBTSerializable<CompoundNBT> {
    default void vanishAt(int slotIndex, int time) {
        vanishAt(slotIndex, time, false);
    }

    void vanishAt(int slotIndex, int time, boolean timestamp);

    int getVanishTicksRemaining(int slotIndex);

    void tick(PlayerInventory inventory);

    boolean hasVanishingItem();

    Int2IntMap getVanishingItems();
}
