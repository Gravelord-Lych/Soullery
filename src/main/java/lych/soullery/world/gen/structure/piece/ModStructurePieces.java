package lych.soullery.world.gen.structure.piece;

import lych.soullery.Soullery;
import lych.soullery.world.gen.structure.ModStructureNames;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModStructurePieces {
    public static final IStructurePieceType FIRE_TEMPLE = FireTemplePiece::new;
    public static final IStructurePieceType SC_MAIN_BUILDING = SkyCityPieces.MainBuilding::new;
    public static final IStructurePieceType SC_WALKWAY = SkyCityPieces.Walkway::new;
    public static final IStructurePieceType ST_MAIN_TOWER = SoulTowerPieces.MainTower::new;
    public static final IStructurePieceType ST_TOP_ROOM = SoulTowerPieces.TopRoom::new;
    public static final IStructurePieceType SC_BRIDGE_CROSSING = SCP0.Crossing3::new;
    public static final IStructurePieceType SC_BRIDGE_END_FILLER = SCP0.End::new;
    public static final IStructurePieceType SC_BRIDGE_STRAIGHT = SCP0.Straight::new;
    public static final IStructurePieceType SC_CASTLE_CORRIDOR_STAIRS = SCP0.Corridor3::new;
    public static final IStructurePieceType SC_CASTLE_CORRIDOR_T_BALCONY = SCP0.Corridor4::new;
    public static final IStructurePieceType SC_CASTLE_ENTRANCE = SCP0.Entrance::new;
    public static final IStructurePieceType SC_CASTLE_BOSS_ROOM = SCP0.BossRoom::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR_CROSSING = SCP0.Crossing2::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR_LEFT_TURN = SCP0.Corridor::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR = SCP0.Corridor5::new;
    public static final IStructurePieceType SC_CASTLE_SMALL_CORRIDOR_RIGHT_TURN = SCP0.Corridor2::new;
    public static final IStructurePieceType SC_CASTLE_STALK_ROOM = SCP0.SoulStalkRoom::new;
    public static final IStructurePieceType SC_MONSTER_THRONE = SCP0.Throne::new;
    public static final IStructurePieceType SC_ROOM_CROSSING = SCP0.Crossing::new;
    public static final IStructurePieceType SC_STAIRS_ROOM = SCP0.Stairs::new;
    public static final IStructurePieceType SC_START = SCP0.Start::new;

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

//?????????????????????
        setPieceId(SC_MAIN_BUILDING, prefix(ModStructureNames.SKY_CITY, "main_building"));
        setPieceId(SC_WALKWAY, prefix(ModStructureNames.SKY_CITY, "walkway"));
    }

    private static String prefix(String structure, String type) {
        return prefix(structure + "_" + type);
    }

    private static IStructurePieceType setPieceId(IStructurePieceType type, String name) {
        return IStructurePieceType.setPieceId(type, Soullery.MOD_ID + "_" + name);
    }
}
