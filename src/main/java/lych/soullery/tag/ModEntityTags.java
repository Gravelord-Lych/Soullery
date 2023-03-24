package lych.soullery.tag;

import lych.soullery.Soullery;
import lych.soullery.api.TagNames;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.Tags;

public final class ModEntityTags {
    public static final Tags.IOptionalNamedTag<EntityType<?>> TIERED_BOSS = tag(TagNames.TIERED_BOSS.getPath());

    private ModEntityTags() {}

    private static Tags.IOptionalNamedTag<EntityType<?>> tag(String name) {
        return EntityTypeTags.createOptional(Soullery.prefix(name));
    }
}
