package lych.soullery.data;

import lych.soullery.Soullery;
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
//        Advancement.Builder.advancement();
    }


    @Override
    public String toString() {
        return super.toString() + " :" + Soullery.MOD_ID;
    }
}
