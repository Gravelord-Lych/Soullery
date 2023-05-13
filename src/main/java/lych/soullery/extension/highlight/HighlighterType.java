package lych.soullery.extension.highlight;

import lych.soullery.world.event.manager.NotLoadedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HighlighterType {
    public static final HighlighterType NO_HIGHLIGHT = new HighlighterType(Util.NIL_UUID, DummyHighlighter::new, DummyHighlighter::new);
    public static final HighlighterType HORCRUX = new HighlighterType("DF0ACF17-2A34-ABED-8A5E-379870B6D6D3", HorcruxHighlighter::new, HorcruxHighlighter::new);
    public static final HighlighterType MIND_OPERATOR_HELPER = new HighlighterType("51697B6C-C99D-8ED3-688E-2AA7E9AB9A05", MindOperatorHelperHighlighter::new, MindOperatorHelperHighlighter::new);
    public static final HighlighterType MIND_OPERATOR_HELPER_ALT = new HighlighterType("CA56EF44-7E20-D0B3-85E0-E97273BA5B1D", MindOperatorHelperAltHighlighter::new, MindOperatorHelperAltHighlighter::new);
    public static final HighlighterType MONSTER_VIEW = new HighlighterType("C9A92E28-1765-40AB-853A-F3874408A039", MonsterViewHighlighter::new, MonsterViewHighlighter::new);
    public static final HighlighterType SOUL_CONTROL = new HighlighterType("9855C4A0-2B69-C250-92D2-A9230193F2BC", SoulControlHighlighter::new, SoulControlHighlighter::new);
    public static final HighlighterType SOUL_CONTROLLER = new HighlighterType("AEC70D6B-080C-021F-8A88-555BE39B9971", SoulControllerHighlighter::new, SoulControllerHighlighter::new);

    static {
        HIGHLIGHTERS = new HashMap<>();
        registerHighlighter(NO_HIGHLIGHT);
        registerHighlighter(HORCRUX);
        registerHighlighter(MIND_OPERATOR_HELPER);
        registerHighlighter(MIND_OPERATOR_HELPER_ALT);
        registerHighlighter(MONSTER_VIEW);
        registerHighlighter(SOUL_CONTROL);
        registerHighlighter(SOUL_CONTROLLER);
    }

    private static final Map<UUID, HighlighterType> HIGHLIGHTERS;
    private final UUID uuid;
    private final HighlighterCreator creator;
    private final HighlighterLoader loader;

    public HighlighterType(String uuid, HighlighterCreator creator, HighlighterLoader loader) {
        this(UUID.fromString(uuid), creator, loader);
    }

    public HighlighterType(UUID uuid, HighlighterCreator creator, HighlighterLoader loader) {
        this.uuid = uuid;
        this.creator = creator;
        this.loader = loader;
    }

    public Highlighter create(UUID entity, long highlightTicksRemaining) {
        return creator.create(entity, highlightTicksRemaining);
    }

    public Highlighter load(UUID entityUUID, CompoundNBT compoundNBT) throws NotLoadedException {
        return loader.load(entityUUID, compoundNBT);
    }

    public UUID getUUID() {
        return uuid;
    }

    public static void registerHighlighter(HighlighterType type) {
        HIGHLIGHTERS.put(type.getUUID(), type);
    }

    public static HighlighterType get(UUID uuid) {
        return HIGHLIGHTERS.get(uuid);
    }

    @FunctionalInterface
    public interface HighlighterCreator {
        Highlighter create(UUID entity, long highlightTicksRemaining);
    }

    @FunctionalInterface
    public interface HighlighterLoader {
        Highlighter load(UUID entityUUID, CompoundNBT nbt) throws NotLoadedException;
    }
}
