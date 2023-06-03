package lych.soullery.data;

import lych.soullery.Soullery;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import static lych.soullery.item.ModItems.*;

public class ItemTagDataGen extends ItemTagsProvider {
    public ItemTagDataGen(DataGenerator gen, BlockTagsProvider blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(gen, blockTagProvider, Soullery.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(Tags.Items.DUSTS).add(SOUL_POWDER);
        tag(Tags.Items.INGOTS).add(SOUL_METAL_INGOT, REFINED_SOUL_METAL_INGOT);
        tag(Tags.Items.NUGGETS).add(SOUL_METAL_INGOT, REFINED_SOUL_METAL_INGOT);
    }

    @Override
    public String getName() {
        return "Item Tags: " + Soullery.MOD_ID;
    }
}
