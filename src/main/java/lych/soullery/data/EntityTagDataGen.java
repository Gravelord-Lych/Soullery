package lych.soullery.data;

import lych.soullery.Soullery;
import lych.soullery.entity.ModEntities;
import lych.soullery.tag.ModEntityTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class EntityTagDataGen extends EntityTypeTagsProvider {
    public EntityTagDataGen(DataGenerator generator, @Nullable ExistingFileHelper helper) {
        super(generator, Soullery.MOD_ID, helper);
    }

    @Override
    protected void addTags() {
        super.addTags();
        tag(EntityTypeTags.SKELETONS).add(ModEntities.SOUL_SKELETON, ModEntities.SOUL_SKELETON_KING);
        tag(EntityTypeTags.ARROWS).add(ModEntities.FANGS_SUMMONER, ModEntities.SOUL_ARROW);
        tag(ModEntityTags.TIERED_BOSS).add(ModEntities.SKELETON_KING, ModEntities.GIANT_X);
        tag(EntityTypeTags.RAIDERS).add(ModEntities.DARK_EVOKER, ModEntities.ENGINEER);
    }

    @Override
    public String getName() {
        return super.getName() + ": " + Soullery.MOD_ID;
    }
}
