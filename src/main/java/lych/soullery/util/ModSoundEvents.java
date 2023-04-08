package lych.soullery.util;

import lych.soullery.Soullery;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Soullery.MOD_ID);
    public static final RegistryObject<SoundEvent> DEFENSIVE_META8_SHARE_SHIELD = register("mob.meta8.share_stronger_shield");
    public static final RegistryObject<SoundEvent> ENERGY_SOUND_BREAK = register("random.energy_shield_break");
    public static final RegistryObject<SoundEvent> ETHEMOVE = register("random.ethemove");
    public static final RegistryObject<SoundEvent> META8_LASER = register("mob.meta8.laser");
    public static final RegistryObject<SoundEvent> META8_SHARE_SHIELD = register("mob.meta8.share_shield");
    public static final RegistryObject<SoundEvent> MIND_OPERATE = register("random.mind_operate");
    public static final RegistryObject<SoundEvent> ROBOT_DEATH = register("mob.robot.death");
    public static final RegistryObject<SoundEvent> ROBOT_HURT = register("mob.robot.hurt");
    public static final RegistryObject<SoundEvent> ROBOT_STEP = register("mob.robot.step");
    public static final RegistryObject<SoundEvent> SOUL_RABBIT_AMBIENT = register("mob.soul_rabbit.ambient");
    public static final RegistryObject<SoundEvent> SOUL_RABBIT_ATTACK = register("mob.soul_rabbit.attack");
    public static final RegistryObject<SoundEvent> SOUL_RABBIT_DEATH = register("mob.soul_rabbit.death");
    public static final RegistryObject<SoundEvent> SOUL_RABBIT_HURT = register("mob.soul_rabbit.hurt");
    public static final RegistryObject<SoundEvent> SOUL_RABBIT_JUMP = register("mob.soul_rabbit.jump");
    public static final RegistryObject<SoundEvent> SOUL_SKELETON_AMBIENT = register("mob.soul_skeleton.ambient");
    public static final RegistryObject<SoundEvent> SOUL_SKELETON_DEATH = register("mob.soul_skeleton.death");
    public static final RegistryObject<SoundEvent> SOUL_SKELETON_HURT = register("mob.soul_skeleton.hurt");
    public static final RegistryObject<SoundEvent> SOUL_SKELETON_SHOOT = register("mob.soul_skeleton.shoot");
    public static final RegistryObject<SoundEvent> SOUL_SKELETON_STEP = register("mob.soul_skeleton.step");
    public static final RegistryObject<SoundEvent> WANDERER_AMBIENT = register("mob.wanderer.ambient");
    public static final RegistryObject<SoundEvent> WANDERER_DEATH = register("mob.wanderer.death");
    public static final RegistryObject<SoundEvent> WANDERER_HURT = register("mob.wanderer.hurt");
    public static final RegistryObject<SoundEvent> WANDERER_LASER = register("mob.wanderer.laser");
    public static final RegistryObject<SoundEvent> WANDERER_STEP = register("mob.wanderer.step");

    private ModSoundEvents() {}

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> new SoundEvent(Soullery.prefix(name)));
    }
}
