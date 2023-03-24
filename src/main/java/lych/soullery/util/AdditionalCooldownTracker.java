package lych.soullery.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class AdditionalCooldownTracker {
    private final Map<ResourceLocation, Cooldown> cooldownMap = new HashMap<>();
    private int tickCount;

    public void reloadFrom(CompoundNBT compoundNBT) {
        tickCount = compoundNBT.getInt("TickCount");
        cooldownMap.clear();
        ListNBT listNBT = compoundNBT.getList("Cooldowns", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < listNBT.size(); i++) {
            CompoundNBT singleNBT = listNBT.getCompound(i);
            String name = singleNBT.getString("CooldownType");
            ResourceLocation location = ResourceLocation.tryParse(name);
            if (location == null) {
                continue;
            }
            int startTime = singleNBT.getInt("StartTime");
            int endTime = singleNBT.getInt("EndTime");
            cooldownMap.put(location, new Cooldown(startTime, endTime));
        }
    }

    public void tick() {
        tickCount++;
        if (!cooldownMap.isEmpty()) {
            cooldownMap.entrySet().removeIf(e -> getCooldownRemaining(e.getValue()) <= 0);
        }
    }

    public void addCooldown(ResourceLocation location, int cooldown) {
        cooldownMap.put(location, new Cooldown(tickCount, tickCount + cooldown));
    }

    public void removeCooldown(ResourceLocation location) {
        cooldownMap.remove(location);
    }

    public int getCooldownRemaining(ResourceLocation location) {
        if (!cooldownMap.containsKey(location)) {
            return 0;
        }
        return getCooldownRemaining(cooldownMap.get(location));
    }

    private int getCooldownRemaining(Cooldown cooldown) {
        return Math.max(0, cooldown.endTime - tickCount);
    }

    public int getTotalCooldown(ResourceLocation location) {
        return cooldownMap.get(location).getTotalCooldown();
    }

    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("TickCount", tickCount);
        ListNBT listNBT = new ListNBT();
        for (Map.Entry<ResourceLocation, Cooldown> entry : cooldownMap.entrySet()) {
            CompoundNBT singleNBT = new CompoundNBT();
            singleNBT.putString("CooldownType", entry.getKey().toString());
            singleNBT.putInt("StartTime", entry.getValue().startTime);
            singleNBT.putInt("EndTime", entry.getValue().endTime);
            listNBT.add(singleNBT);
        }
        compoundNBT.put("Cooldowns", listNBT);
        return compoundNBT;
    }

    private static class Cooldown {
        private final int startTime;
        private final int endTime;

        private Cooldown(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }

        private int getTotalCooldown() {
            return endTime - startTime;
        }
    }
}
