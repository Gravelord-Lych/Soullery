package lych.soullery.world.event.manager;

import lych.soullery.util.ModConstants;
import lych.soullery.world.event.SoulDragonFight;
import lych.soullery.world.gen.feature.PlateauSpikeFeature.Spike;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.LinkedList;
import java.util.List;

public class SoulDragonFightManager extends EventManager<SoulDragonFight> {
    private static final String NAME = "SoulDragonFights";
    private static final ITextComponent NOT_IN_RANGE = SoulDragonFight.makeText("not_in_range");
    private static final String NOT_ENOUGH_SE = SoulDragonFight.makeTextString("not_enough_se");

    public SoulDragonFightManager(ServerWorld level) {
        super(NAME, level);
    }

    public static SoulDragonFightManager get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(() -> new SoulDragonFightManager(world), NAME);
    }

    public static void tryAddFight(BlockPos center, ServerWorld level, List<BlockPos> posList) {
        get(level).tryAddFight(center, posList);
    }

    public void tryAddFight(BlockPos center, List<BlockPos> posList) {
        clear(center);
        List<Spike> spikes = new LinkedList<>();
        for (BlockPos posIn : posList) {
            int height = level.getRandom().nextInt(20) + 20;
            spikes.add(new Spike(posIn.getX(), posIn.getZ(), level.getRandom().nextInt(2) + 5, height));
        }
        SoulDragonFight fight = new SoulDragonFight(getUniqueID(), level, center, spikes);
        eventMap.put(fight.getId(), fight);
        setDirty();
        clear(center);
    }

    private void clear(BlockPos center) {
        level.destroyBlock(center, false);
        for (FallingBlockEntity entity : level.getEntitiesOfClass(FallingBlockEntity.class, new AxisAlignedBB(center.offset(-2, -2, -2), center.offset(2, 2, 2)))) {
            entity.kill();
        }
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(center.offset(-1, -2, -1), center.offset(1, 2, 1)))) {
            entity.kill();
        }
        BlockPos.betweenClosed(center.offset(-1, -1, -1), center.offset(1, -1, 1)).forEach(posIn -> level.destroyBlock(posIn, false));
    }

    public SoulDragonFight getFight(int id) {
        return eventMap.get(id);
    }

    public static void warnPlayerOutOfRange(PlayerEntity player) {
        player.sendMessage(NOT_IN_RANGE.copy().withStyle(TextFormatting.RED), Util.NIL_UUID);
    }

    public static void warnPlayerWithoutEnoughEnergy(PlayerEntity player, int energy) {
        player.sendMessage(new TranslationTextComponent(NOT_ENOUGH_SE, energy, ModConstants.SUMMON_DRAGON_SE_COST).withStyle(TextFormatting.RED), Util.NIL_UUID);
    }

    @Override
    protected SoulDragonFight loadEvent(ServerWorld world, CompoundNBT compoundNBT) throws NotLoadedException {
        return new SoulDragonFight(world, compoundNBT);
    }
}
