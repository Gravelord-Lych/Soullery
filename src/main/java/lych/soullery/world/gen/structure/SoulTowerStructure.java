package lych.soullery.world.gen.structure;

import com.mojang.serialization.Codec;
import lych.soullery.world.gen.config.SoulTowerConfig;
import lych.soullery.world.gen.structure.piece.SoulTowerPieces;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SoulTowerStructure extends Structure<SoulTowerConfig> {
    public SoulTowerStructure(Codec<SoulTowerConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<SoulTowerConfig> getStartFactory() {
        return Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public static class Start extends StructureStart<SoulTowerConfig> {
        public Start(Structure<SoulTowerConfig> feature, int chunkX, int chunkZ, MutableBoundingBox boundingBox, int references, long seed) {
            super(feature, chunkX, chunkZ, boundingBox, references, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager manager, int chunkX, int chunkZ, Biome biome, SoulTowerConfig config) {
            SoulTowerPieces.MainTower mainTower = new SoulTowerPieces.MainTower(random, chunkX << 4, chunkZ << 4, config);
            pieces.add(mainTower);
            mainTower.addChildren(mainTower, pieces, random);
            calculateBoundingBox();
        }
    }
}
