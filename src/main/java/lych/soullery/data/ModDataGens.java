package lych.soullery.data;

import lych.soullery.Soullery;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModDataGens {
    private ModDataGens() {}

    @SubscribeEvent
    public static void onDataGen(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        gen.addProvider(new AdvancementDataGen(gen, helper));
        gen.addProvider(new BlockModelDataGen(gen, helper));
        gen.addProvider(new BlockStateDataGen(gen, helper));
        BlockTagDataGen btg = new BlockTagDataGen(gen, helper);
        gen.addProvider(btg);
        gen.addProvider(new DimensionDataGen(gen));
        gen.addProvider(new EntityTagDataGen(gen, helper));
        gen.addProvider(new FluidTagDataGen(gen, helper));
        gen.addProvider(new ItemModelDataGen(gen, helper));
        gen.addProvider(new ItemTagDataGen(gen, btg, helper));
        gen.addProvider(new LootDataGen(gen));
        gen.addProvider(new ParticleDataGen(gen));
        gen.addProvider(new RecipeDataGen(gen));
        gen.addProvider(new ShaderDataGen(gen));
        gen.addProvider(new SoundDataGen(gen, helper));
    }

    public static String registryNameToString(ForgeRegistryEntry<?> entry) {
        Objects.requireNonNull(entry.getRegistryName(), "Registry name should be non-null");
        return entry.getRegistryName().toString();
    }
}
