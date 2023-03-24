package lych.soullery.entity.iface;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The implementations of the interface will not "be red" in 0.5s if it is hurt.
 */
@OnlyIn(Dist.CLIENT)
public interface INoRedOverlayEntity {
}
