package lych.soullery.data;

import com.google.common.collect.ImmutableList;
import lych.soullery.Soullery;
import lych.soullery.data.advancement.AdvancementRegisterer;
import lych.soullery.data.advancement.SoulLandAdvancement;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.AdvancementProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class AdvancementDataGen extends AdvancementProvider {
    public AdvancementDataGen(DataGenerator generatorIn, ExistingFileHelper fileHelperIn) {
        super(generatorIn, fileHelperIn);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper fileHelper) {
        ImmutableList.Builder<AdvancementRegisterer> tabs = ImmutableList.builder();
        tabs.add(new SoulLandAdvancement());
        tabs.build().forEach(registerer -> registerer.accept(consumer, fileHelper));
    }

    @Override
    public String toString() {
        return super.toString() + " :" + Soullery.MOD_ID;
    }
}
