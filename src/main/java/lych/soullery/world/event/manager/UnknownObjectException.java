package lych.soullery.world.event.manager;

import net.minecraft.util.ResourceLocation;

public class UnknownObjectException extends NotLoadedException {
    private final ResourceLocation unknownLocation;

    public UnknownObjectException(ResourceLocation unknownLocation) {
        this.unknownLocation = unknownLocation;
    }

    public UnknownObjectException(ResourceLocation unknownLocation, Throwable cause) {
        super(cause);
        this.unknownLocation = unknownLocation;
    }

    public ResourceLocation getUnknownLocation() {
        return unknownLocation;
    }
}
