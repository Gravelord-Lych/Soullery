package lych.soullery.extension.fire;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import org.jetbrains.annotations.Nullable;

public class FireProperties {
    Block fireBlock = Blocks.FIRE;
    Block[] additionalFireBlocks = new Block[0];
    Pair<RenderMaterial, RenderMaterial> fireOverlays = Fire.DEFAULT_FIRE_OVERLAYS;
    @Nullable
    ITag<Fluid> lavaTag;
    Fire.Handler handler = Fire.noHandlerNeeded();
    float fireDamage = 1;
    int priority = Fire.DEFAULT_PRIORITY;
    int specialDegree = 1;

    public FireProperties setBlock(Block fireBlock, Block... additionalFireBlocks) {
        this.fireBlock = fireBlock;
        this.additionalFireBlocks = additionalFireBlocks;
        return this;
    }

    public FireProperties useOverlays(RenderMaterial fire0, RenderMaterial fire1) {
        return useOverlays(Pair.of(fire0, fire1));
    }

    public FireProperties useOverlays(Pair<RenderMaterial, RenderMaterial> fireOverlays) {
        this.fireOverlays = fireOverlays;
        return this;
    }

    public FireProperties withCustomLava(ITag<Fluid> lavaTag) {
        this.lavaTag = lavaTag;
        return this;
    }

    public FireProperties noLava() {
        this.lavaTag = null;
        return this;
    }

    public FireProperties handler(Fire.Handler handler) {
        this.handler = handler;
        return this;
    }

    public FireProperties withDamage(float fireDamage) {
        this.fireDamage = fireDamage;
        return this;
    }

    public FireProperties withPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public FireProperties setSpecialDegree(int specialDegree) {
        this.specialDegree = specialDegree;
        return this;
    }
}