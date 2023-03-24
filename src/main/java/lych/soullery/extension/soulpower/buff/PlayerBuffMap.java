package lych.soullery.extension.soulpower.buff;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lych.soullery.api.exa.IExtraAbility;
import lych.soullery.api.exa.PlayerBuff;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class PlayerBuffMap {
    private static final BiMap<IExtraAbility, PlayerBuff> BUFF_MAP = HashBiMap.create();

    private PlayerBuffMap() {}

    @Nullable
    public static PlayerBuff bind(IExtraAbility exa, PlayerBuff buff) {
        return BUFF_MAP.put(exa, buff);
    }

    public static Set<IExtraAbility> keySet() {
        return BUFF_MAP.keySet();
    }

    public static Set<PlayerBuff> values() {
        return BUFF_MAP.values();
    }

    public static Optional<PlayerBuff> getBuff(IExtraAbility exa) {
        return Optional.ofNullable(BUFF_MAP.get(exa));
    }

    public static Optional<IExtraAbility> getAbility(PlayerBuff buff) {
        return Optional.ofNullable(BUFF_MAP.inverse().get(buff));
    }

    public static boolean hasBuff(PlayerEntity player, PlayerBuff buff) {
        return getAbility(buff).isPresent() && getAbility(buff).get().isOn(player);
    }
}
