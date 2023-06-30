package lych.soullery.advancements;

import lych.soullery.advancements.criterion.*;

import static net.minecraft.advancements.CriteriaTriggers.register;

public final class ModCriteriaTriggers {
    public static final BurningUpTrigger BURNING_UP = register(new BurningUpTrigger());
    public static final CraftedSoulContainerTrigger CRAFTED_SOUL_CONTAINER = register(new CraftedSoulContainerTrigger());
    public static final DestroyerTrigger DESTROYER = register(new DestroyerTrigger());
    public static final DestroyerIITrigger DESTROYER_II = register(new DestroyerIITrigger());
    public static final DroppedSoulPieceTrigger DROPPED_SOUL_PIECE = register(new DroppedSoulPieceTrigger());

    private ModCriteriaTriggers() {}

    public static void init() {}
}
