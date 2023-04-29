package lych.soullery.entity.monster.boss.souldragon.phase;

import lych.soullery.entity.monster.boss.souldragon.SoulDragonEntity;
import net.minecraft.entity.LivingEntity;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import static lych.soullery.Soullery.LOGGER;

public class PhaseManager {
    private static final Marker MARKER = MarkerManager.getMarker("SoulDragonPhase");
    private final SoulDragonEntity dragon;
    private final Phase[] phases = new Phase[PhaseType.getCount()];
    private Phase currentPhase;

    public PhaseManager(SoulDragonEntity dragon) {
        this.dragon = dragon;
        setPhase(PhaseType.HOVERING);
    }

    public void setPhase(PhaseType<?> type) {
        if (currentPhase == null || type != currentPhase.getPhase()) {
            if (currentPhase != null) {
                currentPhase.end();
            }
            currentPhase = getPhase(type);
            if (!dragon.level.isClientSide()) {
                dragon.setPhaseId(type.getId());
            }
            LOGGER.info(MARKER, "SoulDragon is now in phase {} on the {}", type, dragon.level.isClientSide() ? "client" : "server");
            currentPhase.begin();
        }
    }

    public void setRandomAttackPhase(LivingEntity target) {
        AttackPhase phase = PhaseType.findRandomAttackPhase(this, dragon.getRandom(), target);
        setPhase(phase.getPhase());
        phase.setTarget(target);
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    @SuppressWarnings("unchecked")
    public <T extends Phase> T getPhase(PhaseType<T> type) {
        int id = type.getId();
        if (phases[id] == null) {
            phases[id] = type.create(dragon);
        }
        return (T) phases[id];
    }
}
