package lych.soullery.entity.iface;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * The implementations of the interface will not fall down when dying.
 */
@OnlyIn(Dist.CLIENT)
public interface INoDeathRotationEntity {
}
