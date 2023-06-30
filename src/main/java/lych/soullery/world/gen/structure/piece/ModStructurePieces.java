package lych.soullery.world.gen.structure.piece;

import lych.soullery.Soullery;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModStructurePieces {
    public static final IStructurePieceType FIRE_TEMPLE = FireTemplePiece::new;
    public static final IStructurePieceType ST_MAIN_TOWER = SoulTowerPieces.MainTower::new;
    public static final IStructurePieceType ST_TOP_ROOM = SoulTowerPieces.TopRoom::new;
    public static final IStructurePieceType SC_BRIDGE_CROSSING = SkyCityPieces.Crossing3::new;
    public static final IStructurePieceType SC_BRIDGE_END_FILLER = SkyCityPieces.End::new;
    public static final IStructurePieceType SC_BRIDGE_STRAIGHT = SkyCityPieces.Straight::new;
    public static final IStructurePieceType SC_CASTLE_CORRIDOR_STAIRS = SkyCityPieces.Corridor3::new;
    public static final IStructurePieceType SC_CASTLE_CORRIDOR_T_BALCONY = SkyCityPieces.Corridor4::new;
    public static final IStructurePieceType SC_CASTLE_ENTRANCE = SkyCityPieces.Entrance::new;
    public static final IStructurePieceType SC_CASTLE_BOSS_ROOM = SkyCityPieces.BossRoom::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR_CROSSING = SkyCityPieces.Crossing2::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR_LEFT_TURN = SkyCityPieces.Corridor::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR = SkyCityPieces.Corridor5::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = SkyCityPieces.Corridor2::new;
    public static final IStructurePieceType SC_CASTLE_STALK_ROOM = SkyCityPieces.SoulStalkRoom::new;
    public static final IStructurePieceType SC_MONSTER_THRONE = SkyCityPieces.Throne::new;
    public static final IStructurePieceType SC_ROOM_CROSSING = SkyCityPieces.Crossing::new;
    public static final IStructurePieceType SC_STAIRS_ROOM = SkyCityPieces.Stairs::new;
    public static final IStructurePieceType SC_START = SkyCityPieces.Start::new;

    private ModStructurePieces() {}

    private static String prefix(String name) {
        return Soullery.prefix(name).toString();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        setPieceId(FIRE_TEMPLE, "Ft");
        setPieceId(ST_MAIN_TOWER, "StMT");
        setPieceId(ST_TOP_ROOM, "StTR");
        setPieceId(SC_BRIDGE_CROSSING, "ScBCr");
        setPieceId(SC_BRIDGE_END_FILLER, "ScBEF");
        setPieceId(SC_BRIDGE_STRAIGHT, "ScBS");
        setPieceId(SC_CASTLE_CORRIDOR_STAIRS, "ScCCS");
        setPieceId(SC_CASTLE_CORRIDOR_T_BALCONY, "ScCTB");
        setPieceId(SC_CASTLE_ENTRANCE, "ScCE");
        setPieceId(SC_CASTLE_BOSS_ROOM, "ScBR");
        setPieceId(SC_CASTLE_SMALL_CORRIDOR_CROSSING, "ScSCSC");
        setPieceId(SC_CASTLE_SMALL_CORRIDOR_LEFT_TURN, "ScSCLT");
        setPieceId(SC_CASTLE_SMALL_CORRIDOR, "ScSC");
        setPieceId(SC_CASTLE_SMALL_CORRIDOR_RIGHT_TURN, "ScSCRT");
        setPieceId(SC_CASTLE_STALK_ROOM, "ScCSR");
        setPieceId(SC_MONSTER_THRONE, "ScMT");
        setPieceId(SC_ROOM_CROSSING, "ScRC");
        setPieceId(SC_STAIRS_ROOM, "ScSR");
        setPieceId(SC_START, "ScStart");
    }

    private static String prefix(String structure, String type) {
        return prefix(structure + "_" + type);
    }

    private static IStructurePieceType setPieceId(IStructurePieceType type, String name) {
        return IStructurePieceType.setPieceId(type, Soullery.MOD_ID + "_" + name);
    }
}
