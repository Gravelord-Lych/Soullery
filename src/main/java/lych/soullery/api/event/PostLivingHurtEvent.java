package lych.soullery.api.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;

public class PostLivingHurtEvent extends LivingEvent {
    private final DamageSource source;
    private final float amount;
    private final boolean successfullyHurt;

    public PostLivingHurtEvent(LivingEntity entity, DamageSource source, float amount, boolean successfullyHurt) {
        super(entity);
        this.source = source;
        this.amount = amount;
        this.successfullyHurt = successfullyHurt;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public boolean isSuccessfullyHurt() {
        return successfullyHurt;
    }
}
