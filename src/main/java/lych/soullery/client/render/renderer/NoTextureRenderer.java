package lych.soullery.client.render.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoTextureRenderer<T extends Entity> extends EntityRenderer<T> {
    public NoTextureRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
