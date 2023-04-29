package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import lych.soullery.util.WeightedRandom;
import net.minecraft.entity.LivingEntity;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class PhaseType<T extends Phase> implements WeightedRandom.Item {
    private static Set<PhaseType<? extends AttackPhase>> attackPhases;
    private static PhaseType<?>[] phases = new PhaseType[0];
    public static final PhaseType<DefaultPhase> DEFAULT = create(DefaultPhase::new, "Default");
    public static final PhaseType<BombardPhase> BOMBARD = create(BombardPhase::new, "Bombard").setIsAttackPhase(50);
    public static final PhaseType<ChargingPlayerPhase> CHARGING_PLAYER = create(ChargingPlayerPhase::new, "ChargingPlayer");
    public static final PhaseType<DyingPhase> DYING = create(DyingPhase::new, "Dying");
    public static final PhaseType<HoveringPhase> HOVERING = create(HoveringPhase::new, "Hovering");
    public static final PhaseType<LandingPhase> LANDING = create(LandingPhase::new, "Landing");
    public static final PhaseType<LandingApproachPhase> LANDING_APPROACH = create(LandingApproachPhase::new, "LandingApproach");
    public static final PhaseType<StrafePlayerPhase> STRAFE_PLAYER = create(StrafePlayerPhase::new, "StrafePlayer").setIsAttackPhase(100);
    public static final PhaseType<SpawnSoulBoltPhase> SPAWN_SOUL_BOLT = create(SpawnSoulBoltPhase::new, "SpawnSoulBolt").setIsAttackPhase(75);
    public static final PhaseType<TakeoffPhase> TAKEOFF = create(TakeoffPhase::new, "Takeoff");

    private final int id;
    private final Function<? super SoulDragonEntity, ? extends T> creator;
    private final String name;
    private int attackPhaseWeight = -1;

    private PhaseType(int id, Function<? super SoulDragonEntity, ? extends T> creator, String name) {
        this.id = id;
        this.creator = creator;
        this.name = name;
    }

    public static <T extends Phase> PhaseType<T> create(Function<? super SoulDragonEntity, ? extends T> creator, String name) {
        PhaseType<T> type = new PhaseType<>(phases.length, creator, name);
        phases = Arrays.copyOf(phases, phases.length + 1);
        phases[type.getId()] = type;
        return type;
    }

    public static PhaseType<?> get(int id) {
        return id >= 0 && id < phases.length ? phases[id] : DEFAULT;
    }

    public static int getCount() {
        return phases.length;
    }

    public static AttackPhase findRandomAttackPhase(PhaseManager manager, Random random, LivingEntity target) {
        if (attackPhases == null || attackPhases.isEmpty()) {
            throw new IllegalStateException("No phase found");
        }
        Set<PhaseType<? extends AttackPhase>> attackTypes = new HashSet<>(attackPhases);
        List<WeightedRandom.ItemImpl<? extends AttackPhase>> items = attackTypes.stream()
                .map(manager::getPhase)
                .map(phase -> WeightedRandom.makeItem(phase, (int) (phase.getBaseWeight() * phase.getWeightMultiplier(target))))
                .filter(item -> item.getWeight() > 0)
                .collect(Collectors.toList());
        return WeightedRandom.getRandomItem(random, items).get();
    }

    @SuppressWarnings("unchecked")
    public PhaseType<T> setIsAttackPhase(int weight) {
        if (attackPhases == null) {
            attackPhases = new HashSet<>();
        }
        if (attackPhaseWeight > 0) {
            throw new IllegalStateException("AttackPhaseWeight has already been specified!");
        } else if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        try {
            attackPhases.add((PhaseType<? extends AttackPhase>) this);
            attackPhaseWeight = weight;
        } catch (ClassCastException e) {
            throw new IllegalStateException("Found non-AttackPhase type");
        }
        return this;
    }

    public int getId() {
        return id;
    }

    public T create(SoulDragonEntity dragon) {
        return creator.apply(dragon);
    }

    public String toString() {
        return String.format("%s (#%d)", name, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PhaseType)) {
            return false;
        }
        return ((PhaseType<?>) obj).id == id;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public int getWeight() {
        if (attackPhaseWeight <= 0) {
            throw new UnsupportedOperationException();
        }
        return attackPhaseWeight;
    }

    public boolean isAttackPhase() {
        return attackPhaseWeight > 0;
    }
}
