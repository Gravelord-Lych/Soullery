package lych.soullery.world.gen.structure;

import com.mojang.serialization.Codec;
import lych.soullery.world.gen.structure.piece.SCP0;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;

public class SkyCityStructure extends Structure<NoFeatureConfig> {
    public SkyCityStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return SkyCityStructure.Start::new;
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
            SCP0.Start start = new SCP0.Start(random, (chunkX << 4) + 2, (chunkZ << 4) + 2);

            do {
                pieces.clear();
                pieces.add(start);
                start.addChildren(start, pieces, random);
                List<StructurePiece> pendingChildren = start.pendingChildren;
                while (!pendingChildren.isEmpty()) {
                    int index = random.nextInt(pendingChildren.size());
                    StructurePiece piece = pendingChildren.remove(index);
                    piece.addChildren(start, pieces, random);
                }
            } while (pieces.isEmpty() || !start.hasBossRoom);

            calculateBoundingBox();
            moveInsideHeights(random, 150, 200);
        }
    }
}
