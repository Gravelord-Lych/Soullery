package lych.soullery.block;

import lych.soullery.Soullery;
import lych.soullery.block.entity.*;
import lych.soullery.block.plant.SoulWartBlock;
import lych.soullery.block.plant.SoulifiedBushBlock;
import lych.soullery.fluid.ModFluids;
import lych.soullery.item.ModMaterials;
import lych.soullery.tag.ModBlockTags;
import lych.soullery.util.ModConstants;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

import static lych.soullery.Soullery.make;
import static net.minecraft.block.AbstractBlock.Properties.copy;
import static net.minecraft.block.AbstractBlock.Properties.of;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBlocks {
    public static final SoulMetalBarsBlock CHIPPED_SOUL_METAL_BARS = new SoulMetalBarsBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(2).noDrops().strength(5, 36).sound(SoundType.METAL).noOcclusion().lightLevel(state -> 2), 2);
    public static final Block CRIMSON_HYPHAL_SOIL = new HyphalSoulSoilBlock(of(Material.DIRT, MaterialColor.CRIMSON_NYLIUM).randomTicks().strength(0.5f).sound(SoundType.SOUL_SOIL));
    public static final SoulMetalBarsBlock DAMAGED_SOUL_METAL_BARS = new SoulMetalBarsBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops().strength(5, 6).sound(SoundType.METAL).noOcclusion().lightLevel(state -> 1), 1);
    public static final Block DECAYED_STONE = new Block(of(Material.STONE, MaterialColor.COLOR_BLACK).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(1.5f, 6));
    public static final Block DEPTH_SEGEN = createSegenBlock(MaterialColor.STONE, 2, 1.5f, 2, () -> new DepthSEGeneratorTileEntity(ModTileEntities.DEPTH_SEGEN, 1));
    public static final Block DEPTH_SEGEN_II = createSegenBlock(MaterialColor.STONE, 3, 3, 4, () -> new DepthSEGeneratorTileEntity(ModTileEntities.DEPTH_SEGEN_II, 2));
    public static final Block GUIDER = new GuiderBlock(of(Material.STONE, MaterialColor.COLOR_BLUE).strength(-1, 3600000).noDrops().lightLevel(state -> 15));
    public static final Block HEAT_SEGEN = createSegenBlock(MaterialColor.FIRE, 2, 1.5f, 8, () -> new HeatSEGeneratorTileEntity(ModTileEntities.HEAT_SEGEN, 1));
    public static final Block HEAT_SEGEN_II = createSegenBlock(MaterialColor.FIRE, 3, 3, 15, () -> new HeatSEGeneratorTileEntity(ModTileEntities.HEAT_SEGEN_II, 2));
    public static final SimpleFireBlock INFERNO = new SimpleFireBlock(fireProperties(MaterialColor.FIRE, ModConstants.INFERNO_LIGHT_LEVEL), ModBlockTags.INFERNO_BASE_BLOCKS);
    public static final Block NETHER_SEGEN = createSegenBlock(MaterialColor.NETHER, 2, 1.5f, 5, () -> new NetherSEGeneratorTileEntity(ModTileEntities.NETHER_SEGEN, 1));
    public static final Block NETHER_SEGEN_II = createSegenBlock(MaterialColor.NETHER, 3, 3, 10, () -> new NetherSEGeneratorTileEntity(ModTileEntities.NETHER_SEGEN_II, 2));
    public static final Block PARCHED_SOIL = new Block(of(Material.DIRT, MaterialColor.COLOR_RED).strength(0.5F).sound(SoundType.SOUL_SOIL));
    public static final AdvancedFireBlock POISONOUS_FIRE = new AdvancedFireBlock(fireProperties(MaterialColor.COLOR_LIGHT_GREEN, ModConstants.POISONOUS_FIRE_LIGHT_LEVEL), ModBlocks.POISONOUS_FIRE, ModBlockTags.POISONOUS_FIRE_BASE_BLOCKS);
    public static final Block POTTED_SOULIFIED_BUSH = new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> ModBlocks.SOULIFIED_BUSH, copy(Blocks.DEAD_BUSH));
    public static final SimpleFireBlock PURE_SOUL_FIRE = new SimpleFireBlock(fireProperties(MaterialColor.COLOR_BLUE, ModConstants.PURE_SOUL_FIRE_LIGHT_LEVEL), ModBlockTags.PURE_SOUL_FIRE_BASED_BLOCKS);
    public static final Block REFINED_SOUL_METAL_BLOCK = new Block(of(Material.METAL, MaterialColor.COLOR_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(3).requiresCorrectToolForDrops().strength(8, 24).sound(SoundType.METAL).lightLevel(state -> 10));
    public static final Block REFINED_SOUL_SAND = new SoulSandBlock(of(Material.SAND, MaterialColor.COLOR_BROWN).strength(0.5F).speedFactor(0.2F).sound(SoundType.SOUL_SAND).isValidSpawn(ModBlocks::always).isRedstoneConductor(ModBlocks::always).isViewBlocking(ModBlocks::always).isSuffocating(ModBlocks::always));
    public static final Block REFINED_SOUL_SOIL = new Block(of(Material.DIRT, MaterialColor.COLOR_BROWN).strength(0.5F).speedFactor(0.9F).sound(SoundType.SOUL_SOIL));
    public static final Block SEGEN = createSegenBlock(MaterialColor.COLOR_LIGHT_BLUE, 2, 1.5f, 5, () -> new SEGeneratorTileEntity(ModTileEntities.SEGEN, 1));
    public static final Block SEGEN_II = createSegenBlock(MaterialColor.COLOR_BLUE, 3, 3, 10, () -> new SEGeneratorTileEntity(ModTileEntities.SEGEN_II, 2));
    public static final Block SKY_SEGEN = createSegenBlock(MaterialColor.COLOR_LIGHT_BLUE, 2, 1.5f, 8, () -> new SkySEGeneratorTileEntity(ModTileEntities.SKY_SEGEN, 1));
    public static final Block SKY_SEGEN_II = createSegenBlock(MaterialColor.COLOR_LIGHT_BLUE, 3, 3, 15, () -> new SkySEGeneratorTileEntity(ModTileEntities.SKY_SEGEN_II, 2));
    public static final Block SOLAR_SEGEN = createSegenBlock(MaterialColor.COLOR_LIGHT_BLUE, 2, 1.5f, 5, () -> new SolarSEGeneratorTileEntity(ModTileEntities.SOLAR_SEGEN, 1));
    public static final Block SOLAR_SEGEN_II = createSegenBlock(MaterialColor.COLOR_BLUE, 3, 3, 10, () -> new SolarSEGeneratorTileEntity(ModTileEntities.SOLAR_SEGEN_II, 2));
    public static final Block SOUL_ENERGY_STORAGE = new SEStorageBlock(of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops().strength(1.5f).lightLevel(state -> state.getValue(ModBlockStateProperties.SOUL_ENERGY_LEVEL) + 5), () -> new SEStorageTileEntity(ModTileEntities.SOUL_ENERGY_STORAGE, SEStorageTileEntity.CAPACITY, 1));
    public static final Block SOUL_ENERGY_STORAGE_II = new SEStorageBlock(of(Material.STONE, MaterialColor.COLOR_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(3).requiresCorrectToolForDrops().strength(3).lightLevel(state -> state.getValue(ModBlockStateProperties.SOUL_ENERGY_LEVEL) + 10), () -> new SEStorageTileEntity(ModTileEntities.SOUL_ENERGY_STORAGE_II, SEStorageTileEntity.CAPACITY_II, 2));
    public static final FlowingFluidBlock SOUL_LAVA_FLUID_BLOCK = new FlowingFluidBlock(() -> ModFluids.SOUL_LAVA, of(ModMaterials.SOUL_LAVA).noCollission().randomTicks().strength(100).noDrops().lightLevel(state -> 15));
    public static final SoulMetalBarsBlock SOUL_METAL_BARS = new SoulMetalBarsBlock(AbstractBlock.Properties.of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(2).noDrops().strength(5, 1296).sound(SoundType.METAL).noOcclusion().lightLevel(state -> 3), 3);
    public static final Block SOUL_METAL_BLOCK = new Block(of(Material.METAL, MaterialColor.COLOR_LIGHT_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(2).requiresCorrectToolForDrops().strength(4, 6).sound(SoundType.METAL).lightLevel(state -> 5));
    public static final Block SOUL_OBSIDIAN = new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK).harvestTool(ToolType.PICKAXE).harvestLevel(3).requiresCorrectToolForDrops().strength(75, 1200).isValidSpawn(ModBlocks::never).lightLevel(state -> 3));
    public static final Block SOUL_REINFORCEMENT_TABLE = new SoulReinforcementTableBlock(of(Material.STONE, MaterialColor.COLOR_LIGHT_BLUE).harvestTool(ToolType.PICKAXE).harvestLevel(0).requiresCorrectToolForDrops().strength(1.5f, 6));
    public static final Block SOUL_STONE = new Block(of(Material.STONE, MaterialColor.COLOR_BLUE).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops().strength(1.5f, 6));
    public static final Block SOUL_WART = new SoulWartBlock(of(Material.PLANT, MaterialColor.COLOR_CYAN).noCollission().randomTicks().sound(SoundType.NETHER_WART));
    public static final Block SOULIFIED_BUSH = new SoulifiedBushBlock(copy(Blocks.DEAD_BUSH));
    public static final Block WARPED_HYPHAL_SOIL = new HyphalSoulSoilBlock(of(Material.DIRT, MaterialColor.WARPED_NYLIUM).randomTicks().strength(0.5f).sound(SoundType.SOUL_SOIL));

//  ----------------------------------------EXTENSIONS----------------------------------------
    public static final Block CHISELED_SOUL_STONE_BRICKS = new Block(copy(SOUL_STONE));
    public static final Block CRACKED_DECAYED_STONE_BRICKS = new Block(copy(DECAYED_STONE));
    public static final SlabBlock CRACKED_DECAYED_STONE_BRICK_SLAB = new SlabBlock(copy(DECAYED_STONE));
    public static final StairsBlock CRACKED_DECAYED_STONE_BRICK_STAIRS = new StairsBlock(CRACKED_DECAYED_STONE_BRICKS::defaultBlockState, copy(DECAYED_STONE));
    public static final WallBlock CRACKED_DECAYED_STONE_BRICK_WALL = new WallBlock(copy(DECAYED_STONE));
    public static final Block CRACKED_SOUL_STONE_BRICKS = new Block(copy(SOUL_STONE));
    public static final SlabBlock CRACKED_SOUL_STONE_BRICK_SLAB = new SlabBlock(copy(SOUL_STONE));
    public static final StairsBlock CRACKED_SOUL_STONE_BRICK_STAIRS = new StairsBlock(CRACKED_SOUL_STONE_BRICKS::defaultBlockState, copy(SOUL_STONE));
    public static final WallBlock CRACKED_SOUL_STONE_BRICK_WALL = new WallBlock(copy(SOUL_STONE));
    public static final Block DECAYED_STONE_BRICKS = new Block(copy(DECAYED_STONE));
    public static final SlabBlock DECAYED_STONE_BRICK_SLAB = new SlabBlock(copy(DECAYED_STONE));
    public static final StairsBlock DECAYED_STONE_BRICK_STAIRS = new StairsBlock(DECAYED_STONE_BRICKS::defaultBlockState, copy(SOUL_STONE));
    public static final WallBlock DECAYED_STONE_BRICK_WALL = new WallBlock(copy(DECAYED_STONE));
    public static final SlabBlock DECAYED_STONE_SLAB = new SlabBlock(copy(DECAYED_STONE));
    public static final StairsBlock DECAYED_STONE_STAIRS = new StairsBlock(DECAYED_STONE::defaultBlockState, copy(SOUL_STONE));
    public static final WallBlock DECAYED_STONE_WALL = new WallBlock(copy(DECAYED_STONE));
    public static final Block SMOOTH_SOUL_STONE = new Block(copy(SOUL_STONE));
    public static final SlabBlock SMOOTH_SOUL_STONE_SLAB = new SlabBlock(copy(SOUL_STONE));
    public static final StairsBlock SMOOTH_SOUL_STONE_STAIRS = new StairsBlock(SMOOTH_SOUL_STONE::defaultBlockState, copy(SOUL_STONE));
    public static final WallBlock SMOOTH_SOUL_STONE_WALL = new WallBlock(copy(SOUL_STONE));
    public static final SlabBlock SOUL_STONE_SLAB = new SlabBlock(copy(SOUL_STONE));
    public static final StairsBlock SOUL_STONE_STAIRS = new StairsBlock(SOUL_STONE::defaultBlockState, copy(SOUL_STONE));
    public static final WallBlock SOUL_STONE_WALL = new WallBlock(copy(SOUL_STONE));
    public static final Block SOUL_STONE_BRICKS = new Block(copy(SOUL_STONE));
    public static final SlabBlock SOUL_STONE_BRICK_SLAB = new SlabBlock(copy(SOUL_STONE));
    public static final StairsBlock SOUL_STONE_BRICK_STAIRS = new StairsBlock(SOUL_STONE_BRICKS::defaultBlockState, copy(SOUL_STONE));
    public static final WallBlock SOUL_STONE_BRICK_WALL = new WallBlock(copy(SOUL_STONE));
//  ------------------------------------------------------------------------------------------

    private ModBlocks() {}

    private static StatedSEGeneratorBlock createSegenBlock(MaterialColor color, int harvestLevel, float strength, int lightLevel, Supplier<? extends AbstractSEGeneratorTileEntity> supplier) {
        return new StatedSEGeneratorBlock(of(Material.STONE, color).sound(SoundType.STONE).harvestTool(ToolType.PICKAXE).harvestLevel(harvestLevel).requiresCorrectToolForDrops().strength(strength).lightLevel(state -> state.getValue(ModBlockStateProperties.IS_GENERATING_SE) ? lightLevel : 0), supplier);
    }

    private static boolean always(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    private static boolean always(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> type) {
        return true;
    }

    private static boolean never(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static AbstractBlock.Properties fireProperties(MaterialColor color, int light) {
        return of(Material.FIRE, color).noCollission().instabreak().lightLevel(state -> light).sound(SoundType.WOOL);
    }

    @SubscribeEvent
    public static void registerPlants(FMLCommonSetupEvent event) {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        pot.addPlant(SOULIFIED_BUSH.getRegistryName(), POTTED_SOULIFIED_BUSH.delegate);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(make(CHIPPED_SOUL_METAL_BARS, ModBlockNames.CHIPPED_SOUL_METAL_BARS));
        registry.register(make(CHISELED_SOUL_STONE_BRICKS, ModBlockNames.CHISELED_SOUL_STONE_BRICKS));
        registry.register(make(CRACKED_DECAYED_STONE_BRICK_SLAB, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_SLAB));
        registry.register(make(CRACKED_DECAYED_STONE_BRICK_STAIRS, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_STAIRS));
        registry.register(make(CRACKED_DECAYED_STONE_BRICK_WALL, ModBlockNames.CRACKED_DECAYED_STONE_BRICK_WALL));
        registry.register(make(CRACKED_DECAYED_STONE_BRICKS, ModBlockNames.CRACKED_DECAYED_STONE_BRICKS));
        registry.register(make(CRACKED_SOUL_STONE_BRICK_SLAB, ModBlockNames.CRACKED_SOUL_STONE_BRICK_SLAB));
        registry.register(make(CRACKED_SOUL_STONE_BRICK_STAIRS, ModBlockNames.CRACKED_SOUL_STONE_BRICK_STAIRS));
        registry.register(make(CRACKED_SOUL_STONE_BRICK_WALL, ModBlockNames.CRACKED_SOUL_STONE_BRICK_WALL));
        registry.register(make(CRACKED_SOUL_STONE_BRICKS, ModBlockNames.CRACKED_SOUL_STONE_BRICKS));
        registry.register(make(CRIMSON_HYPHAL_SOIL, ModBlockNames.CRIMSON_HYPHAL_SOIL));
        registry.register(make(DAMAGED_SOUL_METAL_BARS, ModBlockNames.DAMAGED_SOUL_METAL_BARS));
        registry.register(make(DECAYED_STONE, ModBlockNames.DECAYED_STONE));
        registry.register(make(DECAYED_STONE_BRICK_SLAB, ModBlockNames.DECAYED_STONE_BRICK_SLAB));
        registry.register(make(DECAYED_STONE_BRICK_STAIRS, ModBlockNames.DECAYED_STONE_BRICK_STAIRS));
        registry.register(make(DECAYED_STONE_BRICK_WALL, ModBlockNames.DECAYED_STONE_BRICK_WALL));
        registry.register(make(DECAYED_STONE_BRICKS, ModBlockNames.DECAYED_STONE_BRICKS));
        registry.register(make(DECAYED_STONE_SLAB, ModBlockNames.DECAYED_STONE_SLAB));
        registry.register(make(DECAYED_STONE_STAIRS, ModBlockNames.DECAYED_STONE_STAIRS));
        registry.register(make(DECAYED_STONE_WALL, ModBlockNames.DECAYED_STONE_WALL));
        registry.register(make(DEPTH_SEGEN, ModBlockNames.DEPTH_SEGEN));
        registry.register(make(DEPTH_SEGEN_II, ModBlockNames.DEPTH_SEGEN_II));
        registry.register(make(HEAT_SEGEN, ModBlockNames.HEAT_SEGEN));
        registry.register(make(HEAT_SEGEN_II, ModBlockNames.HEAT_SEGEN_II));
        registry.register(make(INFERNO, ModBlockNames.INFERNO));
        registry.register(make(NETHER_SEGEN, ModBlockNames.NETHER_SEGEN));
        registry.register(make(NETHER_SEGEN_II, ModBlockNames.NETHER_SEGEN_II));
        registry.register(make(PARCHED_SOIL, ModBlockNames.PARCHED_SOIL));
        registry.register(make(POISONOUS_FIRE, ModBlockNames.POISONOUS_FIRE));
        registry.register(make(POTTED_SOULIFIED_BUSH, ModBlockNames.POTTED_SOULIFIED_BUSH));
        registry.register(make(PURE_SOUL_FIRE, ModBlockNames.PURE_SOUL_FIRE));
        registry.register(make(REFINED_SOUL_METAL_BLOCK, ModBlockNames.REFINED_SOUL_METAL_BLOCK));
        registry.register(make(REFINED_SOUL_SAND, ModBlockNames.REFINED_SOUL_SAND));
        registry.register(make(REFINED_SOUL_SOIL, ModBlockNames.REFINED_SOUL_SOIL));
        registry.register(make(SMOOTH_SOUL_STONE, ModBlockNames.SMOOTH_SOUL_STONE));
        registry.register(make(SMOOTH_SOUL_STONE_SLAB, ModBlockNames.SMOOTH_SOUL_STONE_SLAB));
        registry.register(make(SMOOTH_SOUL_STONE_STAIRS, ModBlockNames.SMOOTH_SOUL_STONE_STAIRS));
        registry.register(make(SMOOTH_SOUL_STONE_WALL, ModBlockNames.SMOOTH_SOUL_STONE_WALL));
        registry.register(make(SEGEN, ModBlockNames.SEGEN));
        registry.register(make(SEGEN_II, ModBlockNames.SEGEN_II));
        registry.register(make(SKY_SEGEN, ModBlockNames.SKY_SEGEN));
        registry.register(make(SKY_SEGEN_II, ModBlockNames.SKY_SEGEN_II));
        registry.register(make(SOLAR_SEGEN, ModBlockNames.SOLAR_SEGEN));
        registry.register(make(SOLAR_SEGEN_II, ModBlockNames.SOLAR_SEGEN_II));
        registry.register(make(SOUL_ENERGY_STORAGE, ModBlockNames.SOUL_ENERGY_STORAGE));
        registry.register(make(SOUL_ENERGY_STORAGE_II, ModBlockNames.SOUL_ENERGY_STORAGE_II));
        registry.register(make(SOUL_LAVA_FLUID_BLOCK, ModBlockNames.SOUL_LAVA_FLUID_BLOCK));
        registry.register(make(SOUL_METAL_BARS, ModBlockNames.SOUL_METAL_BARS));
        registry.register(make(SOUL_METAL_BLOCK, ModBlockNames.SOUL_METAL_BLOCK));
        registry.register(make(SOUL_OBSIDIAN, ModBlockNames.SOUL_OBSIDIAN));
        registry.register(make(SOUL_REINFORCEMENT_TABLE, ModBlockNames.SOUL_REINFORCEMENT_TABLE));
        registry.register(make(SOUL_STONE, ModBlockNames.SOUL_STONE));
        registry.register(make(SOUL_STONE_BRICK_SLAB, ModBlockNames.SOUL_STONE_BRICK_SLAB));
        registry.register(make(SOUL_STONE_BRICK_STAIRS, ModBlockNames.SOUL_STONE_BRICK_STAIRS));
        registry.register(make(SOUL_STONE_BRICK_WALL, ModBlockNames.SOUL_STONE_BRICK_WALL));
        registry.register(make(SOUL_STONE_BRICKS, ModBlockNames.SOUL_STONE_BRICKS));
        registry.register(make(SOUL_STONE_SLAB, ModBlockNames.SOUL_STONE_SLAB));
        registry.register(make(SOUL_STONE_STAIRS, ModBlockNames.SOUL_STONE_STAIRS));
        registry.register(make(SOUL_STONE_WALL, ModBlockNames.SOUL_STONE_WALL));
        registry.register(make(SOUL_WART, ModBlockNames.SOUL_WART));
        registry.register(make(SOULIFIED_BUSH, ModBlockNames.SOULIFIED_BUSH));
        registry.register(make(WARPED_HYPHAL_SOIL, ModBlockNames.WARPED_HYPHAL_SOIL));
    }
}
