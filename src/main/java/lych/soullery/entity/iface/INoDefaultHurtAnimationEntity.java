package lych.soullery.entity.iface;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface INoDefaultHurtAnimationEntity extends INoRedOverlayEntity, INoDeathRotationEntity {
}
