package lych.soullery.world.gen.structure;

import com.mojang.serialization.Codec;
import lych.soullery.world.gen.structure.piece.FireTemplePiece;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FireTempleStructure extends Structure<NoFeatureConfig> {
    public FireTempleStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return Start::new;
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> feature, int chunkX, int chunkZ, MutableBoundingBox boundingBox, int references, long seed) {
            super(feature, chunkX, chunkZ, boundingBox, references, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager manager, int chunkX, int chunkZ, Biome biome, NoFeatureConfig config) {
            FireTemplePiece piece = new FireTemplePiece(random, chunkX << 4, chunkZ << 4);
            pieces.add(piece);
            calculateBoundingBox();
        }
    }
}
