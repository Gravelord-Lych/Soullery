package lych.soullery.data;

import lych.soullery.Soullery;
import lych.soullery.fluid.ModFluids;
import lych.soullery.tag.ModFluidTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FluidTagDataGen extends FluidTagsProvider {
    public FluidTagDataGen(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, Soullery.MOD_ID, existingFileHelper);
    }

    @Override
    public void addTags() {
        tag(FluidTags.LAVA).add(ModFluids.SOUL_LAVA, ModFluids.FLOWING_SOUL_LAVA);
        tag(ModFluidTags.SOUL_LAVA).add(ModFluids.SOUL_LAVA, ModFluids.FLOWING_SOUL_LAVA);
    }

    @Override
    public String getName() {
        return "Fluid Tags: " + Soullery.MOD_ID;
    }
}
