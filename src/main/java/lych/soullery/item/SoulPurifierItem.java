package lych.soullery.item;

import lych.soullery.extension.control.dict.ControlDictionaries;
import lych.soullery.extension.laser.LaserAttackResult;
import lych.soullery.extension.laser.LaserData;
import lych.soullery.util.EntityUtils;
import lych.soullery.util.Lasers;
import lych.soullery.util.ModSoundEvents;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public class SoulPurifierItem extends TargetedWandItem<SoulPurifierItem> {
    public static final float HUE = 0.5f;
    public static final float SATURATION = 0.2f;
    public static final float BRIGHTNESS = 1f;
    private static final LaserData SOUL_PURIFIER = new LaserData.Builder().color(new Color(Color.HSBtoRGB(HUE, SATURATION, BRIGHTNESS))).build();
    private static final int RENDER_TIME = 60;
    private static final int WIDTH = 6;
    private static final int COST = 400;
    private static final int TIME = 400;
    private static final int TIME_II = 800;

    public SoulPurifierItem(Properties properties, int tier) {
        super(properties, COST, tier);
    }

    @Nullable
    @Override
    protected ActionResultType performWandOn(ServerWorld level, ServerPlayerEntity player, EntityRayTraceResult ray, Hand hand) {
        if (ray.getEntity() instanceof MobEntity) {
            MobEntity mob = (MobEntity) ray.getEntity();
            if (ControlDictionaries.SOUL_PURIFIER.control(mob, player, getTier() > 1 ? TIME_II : TIME) != null) {
                Vector3d mobPos = EntityUtils.centerOf(mob);
                Lasers.renderLaser(new LaserAttackResult(mobPos, SOUL_PURIFIER, level), player, RENDER_TIME, WIDTH);
                return ActionResultType.CONSUME;
            }
            return null;
        }
        return null;
    }

    @Nullable
    @Override
    public SoundEvent getSound() {
        return ModSoundEvents.SOUL_PURIFY.get();
    }

    @Override
    protected int getWandDistance() {
        return getTier() > 1 ? LONG_DISTANCE : DEFAULT_DISTANCE;
    }
}
