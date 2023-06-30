package lych.soullery.data.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface AdvancementRegisterer extends BiConsumer<Consumer<Advancement>, ExistingFileHelper> {
    void register(Consumer<Advancement> registry, ExistingFileHelper helper);

    @Override
    default void accept(Consumer<Advancement> advancementConsumer, ExistingFileHelper helper) {
        register(advancementConsumer, helper);
    }
}
