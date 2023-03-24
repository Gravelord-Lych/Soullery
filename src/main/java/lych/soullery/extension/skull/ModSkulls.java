package lych.soullery.extension.skull;

import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class ModSkulls {
    private static final Map<EntityType<?>, Item> SKULLS = new HashMap<>();

    public static void bind(EntityType<?> type, Item skull) {
        SKULLS.put(type, skull);
    }

    public static boolean matches(EntityType<?> type, Item item) {
        return SKULLS.get(type) == item;
    }
}
