package lych.soullery.extension.soulpower.debuff;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.api.exa.MobDebuff;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class MobDebuffMap {
    private static final BiMap<IExtraAbility, MobDebuff> DEBUFF_MAP = HashBiMap.create();

    private MobDebuffMap() {}

    @Nullable
    public static MobDebuff bind(IExtraAbility exa, MobDebuff debuff) {
        return DEBUFF_MAP.put(exa, debuff);
    }

    public static Set<IExtraAbility> keySet() {
        return DEBUFF_MAP.keySet();
    }

    public static Set<MobDebuff> values() {
        return DEBUFF_MAP.values();
    }

    public static Optional<MobDebuff> getDebuff(IExtraAbility exa) {
        return Optional.ofNullable(DEBUFF_MAP.get(exa));
    }

    public static Optional<IExtraAbility> getAbility(MobDebuff debuff) {
        return Optional.ofNullable(DEBUFF_MAP.inverse().get(debuff));
    }

    public static boolean hasBuff(PlayerEntity player, MobDebuff debuff) {
        return getAbility(debuff).isPresent() && getAbility(debuff).get().isOn(player);
    }
}
