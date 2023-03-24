package lych.soullery.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;

public class LockableLookController extends LookController {
    private boolean locked;

    public LockableLookController(MobEntity mob) {
        super(mob);
    }

    @Override
    public void tick() {
        if (locked) {
            return;
        }
        super.tick();
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }
}
