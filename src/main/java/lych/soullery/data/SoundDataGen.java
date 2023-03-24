package lych.soullery.data;

import com.google.common.base.Preconditions;
import lych.soullery.Soullery;
import lych.soullery.util.DefaultValues;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.fml.RegistryObject;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static lych.soullery.util.ModSoundEvents.*;

public class SoundDataGen extends SoundDefinitionsProvider {
    private static final String GENERIC_FOOTSTEPS = "subtitles.block.generic.footsteps";
    private static final String BOW_PATH = "random/bow";
    private static final ResourceLocation LASER_PATH = Soullery.prefix("random/laser");

    public SoundDataGen(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, Soullery.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(DEFENSIVE_META8_SHARE_SHIELD);
        multiple(ENERGY_SOUND_BREAK, "random/explode1", "random/explode2", "random/explode3", "random/explode4");
        redirect(META8_LASER, LASER_PATH, 3);
        add(META8_SHARE_SHIELD);
        multiple(SOUL_RABBIT_AMBIENT, 0.25, "mob/rabbit/idle1", "mob/rabbit/idle2", "mob/rabbit/idle3", "mob/rabbit/idle4");
        multiple(SOUL_RABBIT_ATTACK, "entity/rabbit/attack1", "entity/rabbit/attack2", "entity/rabbit/attack3", "entity/rabbit/attack4");
        single(SOUL_RABBIT_DEATH, new SoundPair("mob/rabbit/bunnymurder", 0.5));
        multiple(SOUL_RABBIT_HURT, 0.5, "mob/rabbit/hurt1", "mob/rabbit/hurt2", "mob/rabbit/hurt3", "mob/rabbit/hurt4");
        multiple(SOUL_RABBIT_JUMP, 0.1, "mob/rabbit/hop1", "mob/rabbit/hop2", "mob/rabbit/hop3", "mob/rabbit/hop4");
        single(ROBOT_DEATH, "mob/irongolem/death");
        multiple(ROBOT_HURT, "mob/irongolem/hit1", "mob/irongolem/hit2", "mob/irongolem/hit3", "mob/irongolem/hit4");
        multiple(ROBOT_STEP, def -> def.subtitle(GENERIC_FOOTSTEPS), "mob/irongolem/walk1", "mob/irongolem/walk2", "mob/irongolem/walk3", "mob/irongolem/walk4");
        add(SOUL_SKELETON_AMBIENT, 3);
        add(SOUL_SKELETON_DEATH);
        add(SOUL_SKELETON_HURT, 4);
        redirect(SOUL_SKELETON_SHOOT, BOW_PATH);
        add(SOUL_SKELETON_STEP, 4, def -> def.subtitle(GENERIC_FOOTSTEPS));
        add(WANDERER_AMBIENT, 4);
        add(WANDERER_DEATH);
        add(WANDERER_HURT, 4);
        redirect(WANDERER_LASER, LASER_PATH, 3);
        add(WANDERER_STEP, 2, def -> def.subtitle(GENERIC_FOOTSTEPS));
    }

    @Override
    public String getName() {
        return "Sounds: " + Soullery.MOD_ID;
    }

    private void single(RegistryObject<SoundEvent> sound, String name) {
        single(sound, new SoundPair(name));
    }

    private void single(RegistryObject<SoundEvent> sound,  SoundPair pair) {
        single(sound, DefaultValues.dummyConsumer(), pair);
    }

    private void single(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, SoundPair pair) {
        add(sound, Util.make(definition().with(sound(pair.name).volume(pair.volume).pitch(pair.pitch)).subtitle(makeSubtitle(sound)), additionalOperations::accept));
    }

    private void multiple(RegistryObject<SoundEvent> sound,  String... names) {
        multiple(sound, DefaultValues.dummyConsumer(), names);
    }

    private void multiple(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, String... names) {
        multiple(sound, additionalOperations, 1, names);
    }

    private void multiple(RegistryObject<SoundEvent> sound,  double volume, String... names) {
        multiple(sound, DefaultValues.dummyConsumer(), volume, names);
    }

    private void multiple(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, double volume, String... names) {
        multiple(sound, additionalOperations, volume, 1, names);
    }

    private void multiple(RegistryObject<SoundEvent> sound,  double volume, double pitch, String... names) {
        multiple(sound, DefaultValues.dummyConsumer(), volume, pitch, names);
    }

    private void multiple(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, double volume, double pitch, String... names) {
        multiple(sound, additionalOperations, Arrays.stream(names).map(name -> new SoundPair(name, volume, pitch)).toArray(SoundPair[]::new));
    }


    private void multiple(RegistryObject<SoundEvent> sound,  SoundPair... pairs) {
        multiple(sound, DefaultValues.dummyConsumer(), pairs);
    }

    private void multiple(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, SoundPair... pairs) {
        Objects.requireNonNull(pairs);
        Preconditions.checkArgument(pairs.length > 1);
        SoundDefinition definition = definition().subtitle(makeSubtitle(sound));
        for (SoundPair pair : pairs) {
            definition.with(sound(pair.name).volume(pair.volume).pitch(pair.pitch));
        }
        additionalOperations.accept(definition);
        add(sound, definition);
    }

    private void add(RegistryObject<SoundEvent> sound) {
        add(sound, 1);
    }

    private void add(RegistryObject<SoundEvent> sound, int count) {
        add(sound, count, DefaultValues.dummyConsumer());
    }

    private void add(RegistryObject<SoundEvent> sound, int count, Consumer<? super SoundDefinition> additionalOperations) {
        Preconditions.checkArgument(count > 0, "Count must be positive");
        SoundDefinition definition = definition();
        if (count > 1) {
            for (int i = 1; i <= count; i++) {
                definition.with(sound((sound.getId() + String.valueOf(i)).replace('.', '/')));
            }
        } else {
            definition.with(sound(sound.getId().toString().replace('.', '/')));
        }
        definition.subtitle(makeSubtitle(sound));
        additionalOperations.accept(definition);
        add(sound, definition);
    }

    private void redirect(RegistryObject<SoundEvent> sound, String redirectTarget) {
        redirect(sound, new ResourceLocation(redirectTarget));
    }

    private void redirect(RegistryObject<SoundEvent> sound, String redirectTarget, int count) {
        redirect(sound, new ResourceLocation(redirectTarget), count);
    }

    private void redirect(RegistryObject<SoundEvent> sound, ResourceLocation redirectTarget) {
        redirect(sound, redirectTarget, 1);
    }

    private void redirect(RegistryObject<SoundEvent> sound, ResourceLocation redirectTarget, int count) {
        SoundDefinition definition = definition().subtitle(makeSubtitle(sound));
        if (count > 1) {
            for (int i = 1; i <= count; i++) {
                definition.with(sound(redirectTarget + String.valueOf(i)));
            }
        } else {
            definition.with(sound(redirectTarget));
        }
        add(sound, definition);
    }

    private static String makeSubtitle(RegistryObject<SoundEvent> sound) {
        return "subtitles." + sound.getId();
    }

    protected static class SoundPair {
        public final String name;
        public final double volume;
        public final double pitch;

        public SoundPair(String name) {
            this(name, 1);
        }

        public SoundPair(String name, double volume) {
            this(name, volume, 1);
        }

        public SoundPair(String name, double volume, double pitch) {
            this.name = name;
            this.volume = volume;
            this.pitch = pitch;
        }
    }
}
