package lych.soullery.util;

import lych.soullery.Soullery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("deprecation")
public final class FireRenderMaterials {
    public static final RenderMaterial INFERNO_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, Soullery.prefix("block/inferno_0"));
    public static final RenderMaterial INFERNO_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, Soullery.prefix("block/inferno_1"));
    public static final RenderMaterial POISONOUS_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, Soullery.prefix("block/poisonous_fire_0"));
    public static final RenderMaterial POISONOUS_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, Soullery.prefix("block/poisonous_fire_1"));

    public static final RenderMaterial PURE_SOUL_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, Soullery.prefix("block/pure_soul_fire_0"));
    public static final RenderMaterial PURE_SOUL_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, Soullery.prefix("block/pure_soul_fire_1"));
    public static final RenderMaterial SOUL_FIRE_0 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_0"));
    public static final RenderMaterial SOUL_FIRE_1 = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS, new ResourceLocation("block/soul_fire_1"));

    private FireRenderMaterials() {}
}
