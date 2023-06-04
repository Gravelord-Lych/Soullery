package lych.soullery.tag;

import lych.soullery.Soullery;
import lych.soullery.api.TagNames;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

public final class ModItemTags {
    public static final Tags.IOptionalNamedTag<Item> SOUL_EXTRACTORS = tag(TagNames.SOUL_EXTRACTORS.getPath());

    private ModItemTags() {}

    private static Tags.IOptionalNamedTag<Item> tag(String name) {
        return ItemTags.createOptional(Soullery.prefix(name));
    }
}
