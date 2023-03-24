package lych.soullery.tag;

import lych.soullery.Soullery;
import lych.soullery.api.TagNames;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.Tags;

public final class ModFluidTags {
    public static final Tags.IOptionalNamedTag<Fluid> SOUL_LAVA = tag(TagNames.SOUL_LAVA.getPath());

    private ModFluidTags() {}

    private static Tags.IOptionalNamedTag<Fluid> tag(String name) {
        return FluidTags.createOptional(Soullery.prefix(name));
    }
}
