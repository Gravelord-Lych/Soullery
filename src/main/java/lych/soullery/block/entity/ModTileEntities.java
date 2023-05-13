package lych.soullery.block.entity;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlockNames;
import lych.soullery.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soullery.Soullery.make;

@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModTileEntities {
    public static final TileEntityType<DepthSEGeneratorTileEntity> DEPTH_SEGEN = Builder.of(() -> new DepthSEGeneratorTileEntity(ModTileEntities.DEPTH_SEGEN, 1), ModBlocks.DEPTH_SEGEN).build(null);
    public static final TileEntityType<DepthSEGeneratorTileEntity> DEPTH_SEGEN_II = Builder.of(() -> new DepthSEGeneratorTileEntity(ModTileEntities.DEPTH_SEGEN_II, 2), ModBlocks.DEPTH_SEGEN_II).build(null);
    public static final TileEntityType<MagneticFieldGeneratorTileEntity> MAGNETIC_FIELD_GENERATOR = Builder.of(() -> new MagneticFieldGeneratorTileEntity(ModTileEntities.MAGNETIC_FIELD_GENERATOR), ModBlocks.MAGNETIC_FIELD_GENERATOR).build(null);
    public static final TileEntityType<HeatSEGeneratorTileEntity> HEAT_SEGEN = Builder.of(() -> new HeatSEGeneratorTileEntity(ModTileEntities.HEAT_SEGEN, 1), ModBlocks.HEAT_SEGEN).build(null);
    public static final TileEntityType<HeatSEGeneratorTileEntity> HEAT_SEGEN_II = Builder.of(() -> new HeatSEGeneratorTileEntity(ModTileEntities.HEAT_SEGEN_II, 2), ModBlocks.HEAT_SEGEN_II).build(null);
    public static final TileEntityType<NetherSEGeneratorTileEntity> NETHER_SEGEN = Builder.of(() -> new NetherSEGeneratorTileEntity(ModTileEntities.NETHER_SEGEN, 1), ModBlocks.NETHER_SEGEN).build(null);
    public static final TileEntityType<NetherSEGeneratorTileEntity> NETHER_SEGEN_II = Builder.of(() -> new NetherSEGeneratorTileEntity(ModTileEntities.NETHER_SEGEN_II, 2), ModBlocks.NETHER_SEGEN_II).build(null);
    public static final TileEntityType<SEGeneratorTileEntity> SEGEN = Builder.of(() -> new SEGeneratorTileEntity(ModTileEntities.SEGEN, 1), ModBlocks.SEGEN).build(null);
    public static final TileEntityType<SEGeneratorTileEntity> SEGEN_II = Builder.of(() -> new SEGeneratorTileEntity(ModTileEntities.SEGEN_II, 2), ModBlocks.SEGEN_II).build(null);
    public static final TileEntityType<SkySEGeneratorTileEntity> SKY_SEGEN = Builder.of(() -> new SkySEGeneratorTileEntity(ModTileEntities.SKY_SEGEN, 1), ModBlocks.SKY_SEGEN).build(null);
    public static final TileEntityType<SkySEGeneratorTileEntity> SKY_SEGEN_II = Builder.of(() -> new SkySEGeneratorTileEntity(ModTileEntities.SKY_SEGEN_II, 2), ModBlocks.SKY_SEGEN_II).build(null);
    public static final TileEntityType<SolarSEGeneratorTileEntity> SOLAR_SEGEN = Builder.of(() -> new SolarSEGeneratorTileEntity(ModTileEntities.SOLAR_SEGEN, 1), ModBlocks.SOLAR_SEGEN).build(null);
    public static final TileEntityType<SolarSEGeneratorTileEntity> SOLAR_SEGEN_II = Builder.of(() -> new SolarSEGeneratorTileEntity(ModTileEntities.SOLAR_SEGEN_II, 2), ModBlocks.SOLAR_SEGEN_II).build(null);
    public static final TileEntityType<SEStorageTileEntity> SOUL_ENERGY_STORAGE = Builder.of(() -> new SEStorageTileEntity(ModTileEntities.SOUL_ENERGY_STORAGE, SEStorageTileEntity.CAPACITY, 1), ModBlocks.SOUL_ENERGY_STORAGE).build(null);
    public static final TileEntityType<SEStorageTileEntity> SOUL_ENERGY_STORAGE_II = Builder.of(() -> new SEStorageTileEntity(ModTileEntities.SOUL_ENERGY_STORAGE_II, SEStorageTileEntity.CAPACITY_II, 2), ModBlocks.SOUL_ENERGY_STORAGE_II).build(null);

    private ModTileEntities() {}

    @SubscribeEvent
    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(make(DEPTH_SEGEN, ModBlockNames.DEPTH_SEGEN));
        registry.register(make(DEPTH_SEGEN_II, ModBlockNames.DEPTH_SEGEN_II));
        registry.register(make(HEAT_SEGEN, ModBlockNames.HEAT_SEGEN));
        registry.register(make(HEAT_SEGEN_II, ModBlockNames.HEAT_SEGEN_II));
        registry.register(make(MAGNETIC_FIELD_GENERATOR, ModBlockNames.MAGNETIC_FIELD_GENERATOR));
        registry.register(make(NETHER_SEGEN, ModBlockNames.NETHER_SEGEN));
        registry.register(make(NETHER_SEGEN_II, ModBlockNames.NETHER_SEGEN_II));
        registry.register(make(SEGEN, ModBlockNames.SEGEN));
        registry.register(make(SEGEN_II, ModBlockNames.SEGEN_II));
        registry.register(make(SKY_SEGEN, ModBlockNames.SKY_SEGEN));
        registry.register(make(SKY_SEGEN_II, ModBlockNames.SKY_SEGEN_II));
        registry.register(make(SOLAR_SEGEN, ModBlockNames.SOLAR_SEGEN));
        registry.register(make(SOLAR_SEGEN_II, ModBlockNames.SOLAR_SEGEN_II));
        registry.register(make(SOUL_ENERGY_STORAGE, ModBlockNames.SOUL_ENERGY_STORAGE));
        registry.register(make(SOUL_ENERGY_STORAGE_II, ModBlockNames.SOUL_ENERGY_STORAGE_II));
    }
}
