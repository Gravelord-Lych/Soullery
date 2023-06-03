package lych.soullery.world.gen.structure.piece;

import lych.soullery.Soullery;
import lych.soullery.world.gen.structure.ModStructureNames;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static net.minecraft.world.gen.feature.structure.IStructurePieceType.setPieceId;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModStructurePieces {
    public static final IStructurePieceType FIRE_TEMPLE = FireTemplePiece::new;
    public static final IStructurePieceType ST_MAIN_TOWER = SoulTowerPieces.MainTower::new;
    public static final IStructurePieceType ST_TOP_ROOM = SoulTowerPieces.TopRoom::new;

    private ModStructurePieces() {}

    private static String prefix(String name) {
        return Soullery.prefix(name).toString();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        setPieceId(FIRE_TEMPLE, prefix(ModStructureNames.FIRE_TEMPLE));
        setPieceId(ST_MAIN_TOWER, prefix(ModStructureNames.SOUL_TOWER, "main_tower"));
        setPieceId(ST_TOP_ROOM, prefix(ModStructureNames.SOUL_TOWER, "top_room"));
    }

    private static String prefix(String structure, String type) {
        return prefix(structure + "_" + type);
    }
}
