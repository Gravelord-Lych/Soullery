package lych.soullery.capability;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import lych.soullery.api.capability.IItemVanishingSkillData;
import lych.soullery.util.ModSoundEvents;
import lych.soullery.world.ItemDestroyer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.util.Constants;

public class ItemVanishingSkillData implements IItemVanishingSkillData {
    private final ServerPlayerEntity player;
    private final Int2IntMap toVanish = new Int2IntArrayMap();
    private int tickCount;
    private boolean firstTick = true;

    public ItemVanishingSkillData(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void vanishAt(int slotIndex, int time, boolean timestamp) {
        ItemDestroyer.checkIndex(slotIndex);
        toVanish.put(slotIndex, timestamp ? time : tickCount + time);
    }

    @Override
    public int getVanishTicksRemaining(int slotIndex) {
        return Math.max(0, toVanish.get(slotIndex) - tickCount);
    }

    @Override
    public void tick(PlayerInventory inventory) {
        tickCount++;
        if (firstTick) {
            for (int slotIndex : toVanish.keySet()) {
                ItemDestroyer.sync(player, slotIndex, true);
            }
            firstTick = false;
        }
        for (ObjectIterator<Int2IntMap.Entry> iterator = toVanish.int2IntEntrySet().iterator(); iterator.hasNext(); ) {
            Int2IntMap.Entry entry = iterator.next();
            if (entry.getIntValue() <= tickCount) {
                boolean changed = !inventory.getItem(entry.getIntKey()).isEmpty();
                inventory.setItem(entry.getIntKey(), ItemStack.EMPTY);
                iterator.remove();
                if (changed) {
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSoundEvents.ITEM_VANISHING.get(), SoundCategory.PLAYERS, 0.5f, 1);
                }
                ItemDestroyer.sync((ServerPlayerEntity) inventory.player, entry.getIntKey(), false);
            }
        }
    }

    @Override
    public boolean hasVanishingItem() {
        return !toVanish.isEmpty();
    }

    @Override
    public Int2IntMap getVanishingItems() {
        return Int2IntMaps.unmodifiable(toVanish);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("TickCount", tickCount);
        ListNBT toVanishNBT = new ListNBT();
        for (Int2IntMap.Entry entry : toVanish.int2IntEntrySet()) {
            CompoundNBT entryNBT = new CompoundNBT();
            entryNBT.putInt("Slot", entry.getIntKey());
            entryNBT.putInt("Timestamp", entry.getIntValue());
            toVanishNBT.add(entryNBT);
        }
        compoundNBT.put("ToVanish", toVanishNBT);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        tickCount = nbt.getInt("TickCount");
        if (nbt.contains("ToVanish", Constants.NBT.TAG_LIST)) {
            toVanish.clear();
            ListNBT toVanishNBT = nbt.getList("ToVanish", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < toVanishNBT.size(); i++) {
                CompoundNBT entryNBT = toVanishNBT.getCompound(i);
                int slot = entryNBT.getInt("Slot");
                int timestamp = entryNBT.getInt("Timestamp");
                toVanish.put(slot, timestamp);
            }
        }
    }
}
